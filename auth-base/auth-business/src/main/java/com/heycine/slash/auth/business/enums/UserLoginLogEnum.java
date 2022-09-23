package com.heycine.slash.auth.business.enums;


import lombok.Getter;

/**
 * @author wenhao lv
 */

@Getter
public enum UserLoginLogEnum {
	/**
	 * 登录成功
	 */
	LOGIN_LOG_SUCCESS(0 ,"登录成功"),
	/**
	 * 登录失败
	 */
	LOGIN_LOG_FAILED(1 ,"登录失败"),
	;
	private Integer code;
	private String desc;
	
	UserLoginLogEnum(Integer code ,String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public static UserLoginLogEnum getTargetEnum(Integer code) {
		UserLoginLogEnum[] instances = UserLoginLogEnum.values();
		for (UserLoginLogEnum instance : instances) {
			if (instance.getCode().equals(code)) {
				return instance;
			}
		}
		return null;
	}
	
}
