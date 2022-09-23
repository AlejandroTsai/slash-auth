package com.heycine.slash.auth.service.oauth.authentication;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * @author alikes
 */
@Data
public class CustomGrantedAuthority implements GrantedAuthority, Serializable {

    private String authority;

    public CustomGrantedAuthority() {
    }

    public CustomGrantedAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
