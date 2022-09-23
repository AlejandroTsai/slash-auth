package com.heycine.slash.auth.service.oauth.authentication;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证主体
 *
 * @author zzj
 *
 * @date 2021-11
 */
@Data
public class UserAuthentication  implements UserDetails, Serializable {
    
    private static final long serialVersionUID = 2L;
    
    private String username;
    
    private String companyId;

    private String password;

    private String email;

    private String phone;

    private String status;

    private String nickName;

    private String userType;
    
    private String authType;

    private String userId;
    
    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 角色列表
     */
    private Set<String> roles;

    @Override
    public Collection<CustomGrantedAuthority> getAuthorities() {
        return permissions.stream().map(CustomGrantedAuthority::new)
                .collect(Collectors.toSet());
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return "0".equals(this.status);
    }
}
