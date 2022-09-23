package com.heycine.slash.auth.service.config.captcha;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({ AjCaptchaProperties.class })
@ComponentScan({ "com.anji.captcha" })
@Import({ AjCaptchaServiceAutoConfig.class, AjCaptchaStorageAutoConfig.class })
public class AjCaptchaAutoConfig {
    public AjCaptchaAutoConfig() {
    }
}
