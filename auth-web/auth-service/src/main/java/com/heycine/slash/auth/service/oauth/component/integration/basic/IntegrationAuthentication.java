package com.heycine.slash.auth.service.oauth.component.integration.basic;

import lombok.Data;

import java.util.Map;

/**
 * @author Alikes
 *
 * @date 2021-11
 **/
@Data
public class IntegrationAuthentication {

    private String authType = "";

    private String username;

    private Map<String, String[]> authParameters;

    public String getAuthParameter(String paramter) {
        String[] values = this.authParameters.get(paramter);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }
}
