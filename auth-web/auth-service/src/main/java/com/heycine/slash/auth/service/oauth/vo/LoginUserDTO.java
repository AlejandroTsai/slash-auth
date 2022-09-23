package com.heycine.slash.auth.service.oauth.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.heycine.slash.auth.service.oauth.authentication.CustomGrantedAuthority;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class LoginUserDTO implements UserDetails, Serializable {
	private String userId;
	private String companyId;
	private String username;
	private String password;
	private String nickName;
	private String email;
	private String phone;
	private String userType;
	private String sex;
	private String avatar;
	private String status;
	private String deleted;
	private String loginIp;
	private Date loginDate;
	private String remark;
	private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	private Set<String> permissions = new HashSet();
	private Set<String> roleIds = new HashSet();
	private Set<String> roles = new HashSet();
	private Set<String> postIds = new HashSet();
	private Boolean accountNonExpired = true;
	private Boolean accountNonLocked = true;
	private Boolean credentialsNonExpired = true;
	private Boolean enabled = true;

	@Override
	public Collection<CustomGrantedAuthority> getAuthorities() {
		return this.permissions.stream().map(CustomGrantedAuthority::new)
				.collect(Collectors.toSet());
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

}
