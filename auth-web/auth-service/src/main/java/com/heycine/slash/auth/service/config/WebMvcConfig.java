package com.heycine.slash.auth.service.config.oauth;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * @author alikes
 */
@Configuration
@EnableConfigurationProperties ({WebProperties.class, WebMvcProperties.class, ResourceProperties.class})
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Resource
    private WebProperties webProperties;

    @Resource
    private WebMvcProperties webMvcProperties;

    /**
     * 静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Override
    @Bean
    public LocaleResolver localeResolver() {
        if (this.webProperties.getLocaleResolver() == WebProperties.LocaleResolver.FIXED) {
            return new FixedLocaleResolver(this.webProperties.getLocale());
        }
        if (this.webMvcProperties.getLocaleResolver() == WebMvcProperties.LocaleResolver.FIXED) {
            return new FixedLocaleResolver(this.webMvcProperties.getLocale());
        }
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        Locale locale = (this.webProperties.getLocale() != null) ? this.webProperties.getLocale()  : this.webMvcProperties.getLocale();
        localeResolver.setDefaultLocale(locale);
        return localeResolver;
    }

}
