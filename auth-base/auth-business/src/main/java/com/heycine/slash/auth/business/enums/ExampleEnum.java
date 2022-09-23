package com.heycine.slash.auth.business.enums;

import com.heycine.slash.common.basic.BasicEnum;

/**
 * @author zzj
 */
public enum ExampleEnum implements BasicEnum<Integer, String> {
	/**
	 * 否
	 */
	EXAMPLE_1(0, "正常"),
	/**
	 * 是
	 */
	EXAMPLE_2(1, "已被冻结");

	private final Integer code;
	private final String info;

	ExampleEnum(Integer code, String info) {
		this.code = code;
		this.info = info;
	}

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getInfo() {
		return info;
	}
}
