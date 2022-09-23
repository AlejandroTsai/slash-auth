package com.heycine.slash.auth.service.oauth.vo;

import java.util.List;

/**
 * ::优雅编程，此刻做起！
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/9/22 下午6:26
 */
public class RegisterUserDTO extends UserDTO{

	private List<String> roleKeys;
	private List<String> companyIds;

	public RegisterUserDTO() {
	}

}
