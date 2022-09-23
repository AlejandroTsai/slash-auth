package com.heycine.slash.auth.service.config.captcha;

import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AjCaptchaStorageAutoConfig {
    public AjCaptchaStorageAutoConfig() {
    }

    @Bean(name = { "AjCaptchaCacheService" })
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties ajCaptchaProperties) {
        return CaptchaServiceFactory.getCache(ajCaptchaProperties.getCacheType().name());
    }
}
