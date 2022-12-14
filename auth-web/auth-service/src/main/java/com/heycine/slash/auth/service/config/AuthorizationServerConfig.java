package com.heycine.slash.auth.service.config;

import com.heycine.slash.auth.business.constant.AuthConstant;
import com.heycine.slash.auth.service.oauth.converter.CheckAccessTokenConverter;
import com.heycine.slash.auth.service.oauth.converter.CustomUserAuthenticationConverter;
import com.heycine.slash.auth.service.oauth.entrypoint.CustomAuthenticationEntryPoint;
import com.heycine.slash.auth.service.oauth.exception.CustomWebResponseExceptionTranslator;
import com.heycine.slash.auth.service.oauth.filter.CustomClientCredentialsTokenEndpointFilter;
import com.heycine.slash.auth.service.oauth.component.granter.CustomClientTokenGranter;
import com.heycine.slash.auth.service.oauth.component.granter.DingTalkTokenGranter;
import com.heycine.slash.auth.service.oauth.component.granter.SmsTokenGranter;
import com.heycine.slash.auth.service.oauth.component.provider.CustomAuthenticationProvider;
import com.heycine.slash.auth.service.oauth.services.CustomTokenServices;
import com.heycine.slash.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ::??????????????????????????????
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/9/7 ??????11:41
 */
@Configuration
@EnableAuthorizationServer
@Slf4j
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Resource
	private CustomAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private DataSource dataSource;
	@Autowired
	private RedisService redisService;
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Autowired
	private CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator;

	@Resource
	private CustomAuthenticationProvider customAuthenticationProvider;

	/**
	 * ?? ?????????????????????????????????
	 *
	 * @param security
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		CustomClientCredentialsTokenEndpointFilter endpointFilter = new CustomClientCredentialsTokenEndpointFilter(
				security, "/oauth/login");
		endpointFilter.afterPropertiesSet();
		endpointFilter.setAuthenticationEntryPoint(authenticationEntryPoint);
		endpointFilter.setAuthenticationManager(new ProviderManager(
						Arrays.asList(
								customAuthenticationProvider,
								new AnonymousAuthenticationProvider("default")
						)
				)
		);

		security
				// ???????????????????????????Token??????????????? ????????????
				.checkTokenAccess("permitAll()")
				// ????????????????????????Token???????????????
				.tokenKeyAccess("permitAll()")
				// ???????????????????????????
				.allowFormAuthenticationForClients()
				// ???????????????
				.passwordEncoder(passwordEncoder())

				.authenticationEntryPoint(authenticationEntryPoint)
				.addTokenEndpointAuthenticationFilter(endpointFilter)
		;
		;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		/*clients.inMemory()
				// ???????????????
				.withClient("joe")
				// ???????????? ??????
				.secret(new BCryptPasswordEncoder().encode("joe"))
				// ???????????????,????????????????????? Token??????
				.redirectUris("http://www.baidu.com")
				// ???????????????,??????????????????
				.autoApprove(false)
				// ????????? all read writer
				.scopes("all")
				// ???????????????????????????
				.authorizedGrantTypes(
						"authorization_code",
						"password",
						"client_credentials",
						"implicit",
						"refresh_token"
				)
				.resourceIds("c1") // ???????????????????????????
		;*/

//		clients.withClientDetails(jdbcClientDetailsService());

		clients.jdbc(dataSource);
	}

	/**
	 * ?? ???????????????????????????
	 *
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				// ???authenticationManager?????????????????????
				.authenticationManager(authenticationManager)
				// ???????????????
				.exceptionTranslator(customWebResponseExceptionTranslator)
				// check_token??????
//				.accessTokenConverter(customAccessTokenConverter())
				// token????????????
				.tokenServices(tokenService())
				/*.tokenStore(tokenStore())*/
				// todo
				.approvalStore(jdbcApprovalStore())
				// todo
				.reuseRefreshTokens(true)
				// ???????????????
				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET, HttpMethod.OPTIONS)
				// ??????????????????
				.pathMapping("/oauth/token", "/oauth/login")
				//??????????????????+?????????????????????+?????????????????????
				.tokenGranter(tokenGranter());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * ?????????????????????
	 */
	@Bean
	public JdbcClientDetailsService jdbcClientDetailsService() {
		JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
		jdbcClientDetailsService.setPasswordEncoder(passwordEncoder());
		return jdbcClientDetailsService;
	}

	/**
	 * ????????????
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		String password = new BCryptPasswordEncoder().encode("gateway-service");
		System.out.println(password);
		;
	}

	/**
	 * Token???????????????
	 *
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {

		// ??????????????????
//		return new JdbcTokenStore(dataSource);

		// ?????????redis
		RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
		tokenStore.setPrefix(AuthConstant.OAUTH_TOKEN_KEY_PREFIX);
		return tokenStore;
	}

	/**
	 * ????????????????????????
	 *
	 * @return
	 */
	@Bean
	public JdbcApprovalStore jdbcApprovalStore() {

		return new JdbcApprovalStore(dataSource);
	}

	/**
	 * ???????????????????????????
	 *
	 * @return
	 */
	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	/**
	 * token??????
	 *
	 * @return
	 */
	@Bean
	public CustomTokenServices tokenService() {
		CustomTokenServices tokenServices = new CustomTokenServices();
		// ??????token??????
		tokenServices.setTokenStore(tokenStore());
		// ????????????refresh_token???????????????????????????????????????????????????????????????????????????????????????????????????token?????????????????????????????????redis??????token??????
		tokenServices.setSupportRefreshToken(true);
		// ??????refresh_token
		tokenServices.setReuseRefreshToken(true);
		// token??????????????????12??????
		tokenServices.setAccessTokenValiditySeconds(60 * 60 * 24 * 30);
		// refresh_token????????????????????????
		tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 30 * 2);
		// token ??????????????????
		tokenServices.setRedisService(redisService);
		//????????????????????????
		tokenServices.setClientDetailsService(clientDetailsService);

		List<TokenEnhancer> delegates = new ArrayList<>();
		delegates.add(jwtAccessTokenConverter());

		TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
		enhancerChain.setTokenEnhancers(delegates);

		tokenServices.setTokenEnhancer(enhancerChain);
		tokenServices.setAuthenticationManager(authenticationManager);
		return tokenServices;
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//		jwtAccessTokenConverter.setSigningKey("C0SrjKMdYb1VTRFs");
		jwtAccessTokenConverter.setKeyPair(keyPair());
		jwtAccessTokenConverter.setAccessTokenConverter(defaultAccessTokenConverter());
		return jwtAccessTokenConverter;
	}

	@Bean
	public KeyPair keyPair() {
		// ??????
		char[] chars = "trax@123456".toCharArray();
		// ???classpath??????????????????????????????
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
				new ClassPathResource("jwt.jks"),
				chars
		);
		return keyStoreKeyFactory.getKeyPair("jwt", chars);
	}

	@Bean
	@Primary
	public AccessTokenConverter defaultAccessTokenConverter() {
		DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
		converter.setUserTokenConverter(new CustomUserAuthenticationConverter());
		return converter;
	}

	@Bean
	@Primary
	public AccessTokenConverter customAccessTokenConverter() {
		CheckAccessTokenConverter converter = new CheckAccessTokenConverter();
		converter.setUserTokenConverter(new CustomUserAuthenticationConverter());
		return converter;
	}

	/**
	 * ???????????? -?????????
	 *
	 * @return
	 */
	@Bean
	public TokenGranter tokenGranter() {
		return new TokenGranter() {
			private CompositeTokenGranter delegate;

			@Override
			public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
				if (delegate == null) {
					delegate = new CompositeTokenGranter(getTokenGranters());
				}
				return delegate.grant(grantType, tokenRequest);
			}
		};
	}

	/**
	 * ???????????????????????????
	 *
	 * @return
	 */
	private List<TokenGranter> getTokenGranters() {
		AuthorizationCodeServices authorizationCodeServices = authorizationCodeServices();
		OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);

		List<TokenGranter> tokenGranters = new ArrayList<>();
		// ?????????????????????
		tokenGranters.add(new AuthorizationCodeTokenGranter(tokenService(), authorizationCodeServices, clientDetailsService, requestFactory));
		// ???????????????????????????
		tokenGranters.add(new RefreshTokenGranter(tokenService(), clientDetailsService, requestFactory));
		// ????????????????????????
		tokenGranters.add(new ImplicitTokenGranter(tokenService(), clientDetailsService, requestFactory));
		// ?????????????????????
		ClientCredentialsTokenGranter clientCredentialsTokenGranter = new ClientCredentialsTokenGranter(tokenService(), clientDetailsService, requestFactory);
		clientCredentialsTokenGranter.setAllowRefresh(false);
		tokenGranters.add(clientCredentialsTokenGranter);

		// ??????????????????????????????
		CustomClientTokenGranter customClientTokenGranter = new CustomClientTokenGranter(tokenService(), clientDetailsService, requestFactory);
		customClientTokenGranter.setAllowRefresh(false);
		tokenGranters.add(customClientTokenGranter);

		// ?????????????????????????????????????????????????????????????????????
		tokenGranters.add(new DingTalkTokenGranter(authenticationManager, tokenService(), clientDetailsService, requestFactory));
		// ?????????????????????????????????????????????????????????????????????
		tokenGranters.add(new SmsTokenGranter(authenticationManager, tokenService(), clientDetailsService, requestFactory));
		// ??????????????????
		tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenService(), clientDetailsService, requestFactory));

		return tokenGranters;
	}


}
