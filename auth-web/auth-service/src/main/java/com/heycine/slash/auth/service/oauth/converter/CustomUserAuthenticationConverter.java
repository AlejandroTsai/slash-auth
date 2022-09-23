package com.heycine.slash.auth.service.oauth.converter;

import com.heycine.slash.auth.business.constant.AuthConstant;
import com.heycine.slash.auth.service.oauth.authentication.UserAuthentication;
import com.heycine.slash.auth.service.oauth.vo.LoginUserDTO;
import com.heycine.slash.auth.service.oauth.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义token处理器
 *
 * @author alikes
 */
@Slf4j
public class CustomUserAuthenticationConverter implements UserAuthenticationConverter {

	private Collection<? extends GrantedAuthority> defaultAuthorities = new ArrayList<>();

	@Resource
	private RemoteUserService remoteUserService;

	@Override
	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		Map<String, Object> response = new LinkedHashMap<>();
		UserAuthentication userAuthentication = (UserAuthentication) authentication.getPrincipal();
		response.put(AuthConstant.USER_NAME, authentication.getName());
		response.put(AuthConstant.USER_ID, userAuthentication.getUserId());
		response.put(AuthConstant.COMPANY_ID, userAuthentication.getCompanyId());
		response.put(AuthConstant.USER_TYPE, userAuthentication.getUserType());
		response.put(AuthConstant.NICK_NAME, userAuthentication.getNickName());
//		response.put(AuthConstant.AUTH_TYPE, userAuthentication.getAuthType());
//        response.put(AuthConstant.AUTHORITIES, userAuthentication.getPermissions());
		return response;
	}

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		if (map.containsKey(AuthConstant.USER_NAME)) {
			Object principal = map.get(AuthConstant.USER_NAME);
			Collection<? extends GrantedAuthority> authorities = this.getAuthorities(map);
			LoginUserDTO loginUserDTO = remoteUserService.getUserInfo((String) map.get(AuthConstant.USER_NAME)).getData();

			if (loginUserDTO != null) {
				authorities = loginUserDTO.getAuthorities();
			}
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
			usernamePasswordAuthenticationToken.setDetails(loginUserDTO);
			return usernamePasswordAuthenticationToken;
		} else {
			return null;
		}

	}

	private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
		if (!map.containsKey(AuthConstant.AUTHORITIES)) {
			return this.defaultAuthorities;
		} else {
			Object authorities = map.get(AuthConstant.AUTHORITIES);
			if (authorities instanceof String) {
				return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
			} else if (authorities instanceof Collection) {
				return AuthorityUtils.commaSeparatedStringToAuthorityList(
						StringUtils.collectionToCommaDelimitedString((Collection) authorities));
			} else {
				throw new IllegalArgumentException("Authorities must be either a String or a Collection");
			}
		}
	}

}
