package com.heycine.slash.auth.service.oauth.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * 钉钉认证的token
 * @author alikes
 */
public class DingTalkAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Object principal;

	private final String state;


	public DingTalkAuthenticationToken(Object principal, String state) {
		super(null);
		this.principal = principal;
		this.state = state;
		super.setAuthenticated(true);
	}

	public DingTalkAuthenticationToken(Object principal,String state, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.state = state;
		super.setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
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

	public String getState() {
		return state;
	}
}

