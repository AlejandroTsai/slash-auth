package com.heycine.slash.auth.service.oauth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserDTO implements Serializable {
	private String userId;
	private String deptId;
	private String userName;
	private String nickName;
	private String userType;
	private String email;
	private String phone;
	private String realName;
	private String qq;
	private String weixin;
	private String dingding;
	private String sex;
	private String avatar;
	private String password;
	private String status;
	private String deleted;
	private String loginIp;
	private Date loginDate;
	private List<RoleDTO> roles;
	private String[] roleIds = new String[0];
	private String[] postIds = new String[0];
	private String searchValue;
	private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	private String remark;
	@ApiModelProperty("企业ID")
	private String companyId;
	@ApiModelProperty("企业名称")
	private String companyName;
	private String[] deptIds = new String[0];

}
