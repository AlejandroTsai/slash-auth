package com.heycine.slash.auth.service.oauth.component.integration;

import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthentication;
import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthenticator;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象集合认证处理器
 *
 * @author alikes
 */
@Slf4j
public abstract class AbstractIntegrationAuthenticator implements IntegrationAuthenticator {

    @Override
    public void prepare(IntegrationAuthentication integrationAuthentication) {
        log.info("authType:{}", integrationAuthentication.getAuthType());
    }

    @Override
    public void complete(IntegrationAuthentication integrationAuthentication) {
        log.info("authType:{}", integrationAuthentication.getAuthType());
    }

}
