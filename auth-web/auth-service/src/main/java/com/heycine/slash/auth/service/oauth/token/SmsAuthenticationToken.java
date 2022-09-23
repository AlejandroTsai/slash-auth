package com.heycine.slash.auth.service.oauth.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * 短信认证的token
 * @author alikes
 */
public class SmsAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Object principal;

	private final String code;


	public SmsAuthenticationToken(Object principal, String code) {
		super(null);
		this.principal = principal;
		this.code = code;
		super.setAuthenticated(true);
	}

	public SmsAuthenticationToken(Object principal, String code, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.code = code;
		super.setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return this.code;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}
		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
	}

	public String getCode() {
		return code;
	}
}

