package com.heycine.slash.auth.service.oauth.component.granter;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import com.heycine.slash.auth.service.oauth.token.*;

/**
 * @author Alikes
 * 生成自定义token，用来处理不包装token的情况
 */
public class CustomClientTokenGranter extends AbstractTokenGranter {
	
	private static final String GRANT_TYPE = "client";
	private boolean allowRefresh = false;
	
	private final ClientDetailsService clientDetailsService;
	
	public CustomClientTokenGranter(AuthorizationServerTokenServices tokenServices,
									ClientDetailsService clientDetailsService,
									OAuth2RequestFactory requestFactory) {
		this(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
	}
	
	protected CustomClientTokenGranter(AuthorizationServerTokenServices tokenServices,
	                                   ClientDetailsService clientDetailsService,
									   OAuth2RequestFactory requestFactory,
									   String grantType) {
		super(tokenServices, clientDetailsService, requestFactory, grantType);
		this.clientDetailsService = clientDetailsService;
	}
	
	public void setAllowRefresh(boolean allowRefresh) {

		this.allowRefresh = allowRefresh;
	}
	
	@Override
	public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
		String clientId = tokenRequest.getClientId();
		ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
		OAuth2AccessToken token = super.grant(grantType, tokenRequest);
		if (token != null) {
			CustomOAuth2AccessToken noRefresh = new CustomOAuth2AccessToken(token);
			if (!allowRefresh) {
				noRefresh.setRefreshToken(null);
			}
			noRefresh.setAdditionalInformation(client.getAdditionalInformation());
			token = noRefresh;
		}
		return token;
	}
	
}
