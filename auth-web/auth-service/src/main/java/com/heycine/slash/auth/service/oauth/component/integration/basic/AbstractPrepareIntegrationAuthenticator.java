package com.heycine.slash.auth.service.oauth.component.integration.basic;

import com.heycine.slash.auth.service.oauth.component.integration.AbstractIntegrationAuthenticator;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象前置通知，默认啥也不干
 *
 * @author alikes
 */
@Slf4j
public abstract class AbstractPrepareIntegrationAuthenticator extends AbstractIntegrationAuthenticator {

    @Override
    public void prepare(IntegrationAuthentication integrationAuthentication) {
        log.info("authType:{}", integrationAuthentication.getAuthType());
    }

}
