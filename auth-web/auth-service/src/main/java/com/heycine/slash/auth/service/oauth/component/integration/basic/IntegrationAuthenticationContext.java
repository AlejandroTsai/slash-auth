package com.heycine.slash.auth.service.oauth.component.integration.basic;

/**
 * @author Alikes
 *
 * @date 2021-11
 **/
public class IntegrationAuthenticationContext {

    private static ThreadLocal<IntegrationAuthentication> holder = new ThreadLocal<>();

    public static void set(IntegrationAuthentication integrationAuthentication) {
        holder.set(integrationAuthentication);
    }

    public static IntegrationAuthentication get() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }
}
