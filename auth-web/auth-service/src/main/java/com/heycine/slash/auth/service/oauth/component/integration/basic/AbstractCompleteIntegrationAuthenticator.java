package com.heycine.slash.auth.service.oauth.component.integration;

import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthentication;
import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthenticator;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象后置处理
 *
 * @author alikes
 */
@Slf4j
public abstract class AbstractCompleteIntegrationAuthenticator implements IntegrationAuthenticator {

    @Override
    public void complete(IntegrationAuthentication integrationAuthentication) {
        log.info("authType:{}", integrationAuthentication.getAuthType());
    }

}
