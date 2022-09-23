package com.heycine.slash.auth.service.oauth.services;

import cn.hutool.core.util.IdUtil;
import com.heycine.slash.auth.business.constant.CacheConstants;
import com.heycine.slash.auth.service.oauth.authentication.UserAuthentication;
import com.heycine.slash.common.redis.service.RedisService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zzj
 */
@Slf4j
@Data
public class CustomTokenServices implements AuthorizationServerTokenServices, ResourceServerTokenServices,
		ConsumerTokenServices, InitializingBean {

	private int refreshTokenValiditySeconds = 2592000;
	private int accessTokenValiditySeconds = 43200;
	private boolean supportRefreshToken = false;
	private boolean reuseRefreshToken = true;
	private TokenStore tokenStore;
	private ClientDetailsService clientDetailsService;
	private TokenEnhancer accessTokenEnhancer;
	private AuthenticationManager authenticationManager;
	private RedisService redisService;


	/**
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.tokenStore, "tokenStore must be set");
	}

	/**
	 * 创建token
	 *
	 * @param authentication
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
		OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
		OAuth2AccessToken accessToken = null != existingAccessToken ? existingAccessToken : createAccessToken(authentication, createRefreshToken(authentication));
		tokenStore.storeAccessToken(accessToken, authentication);
		if (accessToken.getRefreshToken() != null) {
			tokenStore.storeRefreshToken(accessToken.getRefreshToken(), authentication);
		}
		String token = accessToken.getValue();
		JWTClaimsSet claims = getClaims(token);
		if (null != authentication.getUserAuthentication()
				&& authentication.getUserAuthentication() instanceof UsernamePasswordAuthenticationToken
				&& null != claims
		) {
			UserAuthentication securityUser;
			try {
				securityUser = (UserAuthentication) authentication.getPrincipal();
			} catch (Exception ex) {
				securityUser = new UserAuthentication();
				securityUser.setUsername("admin");
				securityUser.setPassword("admin123");
			}

			try {
				String jti = claims.getStringClaim("jti");
				redisService.setCacheObject(CacheConstants.JTI_KEY + jti, token);
				Map<String, Object> info = new HashMap<>();
				Map<String, Object> userInfo = new HashMap<>();
				// 把用户ID设置到JWT中
				userInfo.put("userId", securityUser.getUserId());
				userInfo.put("companyId", securityUser.getCompanyId());
				userInfo.put("userName", securityUser.getUsername());
				userInfo.put("userType", securityUser.getUserType());
				userInfo.put("nickName", securityUser.getNickName());
				info.put("userInfo", userInfo);
				((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
			} catch (ParseException e) {
				log.info(e.getMessage(), e);
			}
		} else {
			ClientDetails client = this.clientDetailsService.loadClientByClientId(authentication.getOAuth2Request().getClientId());
			Map<String, Object> map = new HashMap<>();
			map.putAll(client.getAdditionalInformation());
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
		}
		return accessToken;
	}

	/**
	 * 刷新token
	 *
	 * @param refreshTokenValue
	 * @param tokenRequest
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
		if (!this.supportRefreshToken) {
			throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
		} else {
			OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
			if (refreshToken == null) {
				throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
			} else {
				OAuth2Authentication authentication = this.tokenStore.readAuthenticationForRefreshToken(refreshToken);
				if (this.authenticationManager != null && !authentication.isClientOnly()) {
					Authentication clientUser = new PreAuthenticatedAuthenticationToken(
							authentication.getUserAuthentication(), "", authentication.getAuthorities());
					Authentication user = this.authenticationManager.authenticate(clientUser);
					Object details = authentication.getDetails();
					authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
					authentication.setDetails(details);
				}
				String clientId = authentication.getOAuth2Request().getClientId();
				if (clientId != null && clientId.equals(tokenRequest.getClientId())) {
					this.tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
					if (this.isExpired(refreshToken)) {
						this.tokenStore.removeRefreshToken(refreshToken);
						throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
					} else {
						authentication = this.createRefreshedAuthentication(authentication, tokenRequest);
						if (!this.reuseRefreshToken) {
							this.tokenStore.removeRefreshToken(refreshToken);
							refreshToken = this.createRefreshToken(authentication);
						}

						OAuth2AccessToken accessToken = this.createAccessToken(authentication, refreshToken);
						this.tokenStore.storeAccessToken(accessToken, authentication);
						if (!this.reuseRefreshToken) {
							this.tokenStore.storeRefreshToken(accessToken.getRefreshToken(), authentication);
						}

						return accessToken;
					}
				} else {
					throw new InvalidGrantException("Wrong client for this refresh token: " + refreshTokenValue);
				}
			}
		}
	}

	/**
	 * 获取token
	 *
	 * @param authentication
	 * @return
	 */
	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {

		return this.tokenStore.getAccessToken(authentication);
	}

	/**
	 * 移除token
	 *
	 * @param tokenValue
	 * @return
	 */
	@Override
	public boolean revokeToken(String tokenValue) {
		OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(tokenValue);
		if (accessToken == null) {
			return false;
		} else {
			if (accessToken.getRefreshToken() != null) {
				this.tokenStore.removeRefreshToken(accessToken.getRefreshToken());
			}
			this.tokenStore.removeAccessToken(accessToken);
			return true;
		}
	}

	/**
	 * 加载验证
	 *
	 * @param accessTokenValue
	 * @return
	 * @throws AuthenticationException
	 * @throws InvalidTokenException
	 */
	@Override
	public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
		OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);
		if (accessToken == null) {
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		} else if (accessToken.isExpired()) {
			this.tokenStore.removeAccessToken(accessToken);
			throw new InvalidTokenException("Access token expired: " + accessTokenValue);
		} else {
			OAuth2Authentication result = this.tokenStore.readAuthentication(accessToken);
			if (result == null) {
				throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
			} else {
				if (this.clientDetailsService != null) {
					String clientId = result.getOAuth2Request().getClientId();

					try {
						this.clientDetailsService.loadClientByClientId(clientId);
					} catch (ClientRegistrationException var6) {
						throw new InvalidTokenException("Client not valid: " + clientId, var6);
					}
				}
				return result;
			}
		}
	}

	/**
	 * 读取token
	 *
	 * @param accessToken
	 * @return
	 */
	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		return this.tokenStore.readAccessToken(accessToken);
	}

	public static JWTClaimsSet getClaims(String token) {
		try {
			SignedJWT sjwt = SignedJWT.parse(token);
			JWTClaimsSet claims = sjwt.getJWTClaimsSet();
			return claims;
		} catch (Exception var3) {
			log.error(var3.getMessage(), var3);
			return null;
		}
	}

	private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
		if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
			return null;
		}
		int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
		String value = IdUtil.fastSimpleUUID();
		if (validitySeconds > 0) {
			return new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis()
					+ (validitySeconds * 1000L)));
		}
		return new DefaultOAuth2RefreshToken(value);
	}

	private OAuth2AccessToken createAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(IdUtil.fastSimpleUUID());
		int validitySeconds = this.getAccessTokenValiditySeconds(authentication.getOAuth2Request());
		if (validitySeconds > 0) {
			token.setExpiration(new Date(System.currentTimeMillis() + (long) validitySeconds * 1000L));
		}
		token.setRefreshToken(refreshToken);
		token.setScope(authentication.getOAuth2Request().getScope());
		ClientDetails client = this.clientDetailsService.loadClientByClientId(authentication.getOAuth2Request().getClientId());
		Map<String, Object> map = new HashMap<>();
		map.putAll(client.getAdditionalInformation());
		map.putAll(token.getAdditionalInformation());
		token.setAdditionalInformation(map);
		return (OAuth2AccessToken) (this.accessTokenEnhancer != null ? this.accessTokenEnhancer.enhance(token, authentication) : token);
	}

	protected boolean isSupportRefreshToken(OAuth2Request clientAuth) {
		if (this.clientDetailsService != null) {
			ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
			return client.getAuthorizedGrantTypes().contains("refresh_token");
		} else {
			return this.supportRefreshToken;
		}
	}

	protected int getAccessTokenValiditySeconds(OAuth2Request clientAuth) {
		if (this.clientDetailsService != null) {
			ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
			Integer validity = client.getAccessTokenValiditySeconds();
			if (validity != null) {
				return validity;
			}
		}

		return this.accessTokenValiditySeconds;
	}

	protected int getRefreshTokenValiditySeconds(OAuth2Request clientAuth) {
		if (this.clientDetailsService != null) {
			ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
			Integer validity = client.getRefreshTokenValiditySeconds();
			if (validity != null) {
				return validity;
			}
		}

		return this.refreshTokenValiditySeconds;
	}

	public void setTokenEnhancer(TokenEnhancer accessTokenEnhancer) {
		this.accessTokenEnhancer = accessTokenEnhancer;
	}

	protected boolean isExpired(OAuth2RefreshToken refreshToken) {
		if (!(refreshToken instanceof ExpiringOAuth2RefreshToken)) {
			return false;
		} else {
			ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
			return expiringToken.getExpiration() == null || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
		}
	}

	private OAuth2Authentication createRefreshedAuthentication(OAuth2Authentication authentication,
															   TokenRequest request) {
		Set<String> scope = request.getScope();
		OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
		if (scope != null && !scope.isEmpty()) {
			Set<String> originalScope = clientAuth.getScope();
			if (originalScope == null || !originalScope.containsAll(scope)) {
				throw new InvalidScopeException(
						"Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
			}
			clientAuth = clientAuth.narrowScope(scope);
		}
		OAuth2Authentication narrowed = new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
		return narrowed;
	}

}
