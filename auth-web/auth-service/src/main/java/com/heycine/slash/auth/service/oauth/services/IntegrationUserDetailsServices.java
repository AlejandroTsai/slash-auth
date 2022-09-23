package com.heycine.slash.auth.service.oauth.services;

import com.heycine.slash.auth.service.oauth.authentication.UserAuthentication;
import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthentication;
import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthenticationContext;
import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author alikes
 */
@Service
public class IntegrationUserDetailsServices implements UserDetailsService, Serializable {

    private List<IntegrationAuthenticator> authenticators;

    @Autowired(required = false)
    public void setIntegrationAuthenticators(List<IntegrationAuthenticator> authenticators) {
        this.authenticators = authenticators;
    }

    @Override
    public UserAuthentication loadUserByUsername(String username) throws UsernameNotFoundException {
        // 判断是否是集成登录
        IntegrationAuthentication integrationAuthentication = IntegrationAuthenticationContext.get();
        if (integrationAuthentication == null) {
            integrationAuthentication = new IntegrationAuthentication();
        }
        integrationAuthentication.setUsername(username);

        return this.authenticate(integrationAuthentication);
    }

    private UserAuthentication authenticate(IntegrationAuthentication integrationAuthentication) {
        if (this.authenticators != null) {
            for (IntegrationAuthenticator authenticator : authenticators) {
                if (authenticator.support(integrationAuthentication)) {
                    authenticator.prepare(integrationAuthentication);
                    UserAuthentication userAuthentication =  authenticator.authenticate(integrationAuthentication);
                    authenticator.complete(integrationAuthentication);
                    return userAuthentication;
                }
            }
        }
        return null;
    }

}
