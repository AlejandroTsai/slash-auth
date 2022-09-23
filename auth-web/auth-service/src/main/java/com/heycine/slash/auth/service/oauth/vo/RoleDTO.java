package com.heycine.slash.auth.service.oauth.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RoleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String roleId;
	private String roleName;
	private String roleKey;
	private String roleSort;
	private String dataScope;
	private boolean menuCheckStrictly;
	private boolean deptCheckStrictly;
	private String status;
	private String delFlag;
	private boolean flag = false;
	private String[] menuIds = new String[0];
	private String[] deptIds = new String[0];
	@ApiModelProperty(
			value = "搜索值",
			hidden = true
	)
	private String searchValue;
	@ApiModelProperty("创建人")
	private String createBy;
	@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("创建时间")
	private Date createTime;
	@ApiModelProperty("最后更新人")
	private String updateBy;
	@JsonFormat(
			pattern = "yyyy-MM-dd HH:mm:ss"
	)
	@ApiModelProperty("最后更新时间")
	private Date updateTime;
	@ApiModelProperty(
			value = "备注",
			hidden = true
	)
	private String remark;
	@ApiModelProperty(
			value = "参数",
			hidden = true
	)
	private Map<String, Object> params = new HashMap();

	public RoleDTO(String roleId) {
		this.roleId = roleId;
	}

	@JsonIgnore
	public boolean isAdmin() {
		return isAdmin(this.roleId);
	}

	@JsonIgnore
	public static boolean isAdmin(String roleId) {
		return roleId != null && "1".equals(roleId);
	}

	@NotBlank(
			message = "角色名称不能为空"
	)
	@Size(
			min = 0,
			max = 30,
			message = "角色名称长度不能超过30个字符"
	)
	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@NotBlank(
			message = "权限字符不能为空"
	)
	@Size(
			min = 0,
			max = 100,
			message = "权限字符长度不能超过100个字符"
	)
	public String getRoleKey() {
		return this.roleKey;
	}

	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}

	@NotBlank(
			message = "显示顺序不能为空"
	)
	public String getRoleSort() {
		return this.roleSort;
	}

	public void setRoleSort(String roleSort) {
		this.roleSort = roleSort;
	}

	public String getDataScope() {
		return this.dataScope;
	}

	public void setDataScope(String dataScope) {
		this.dataScope = dataScope;
	}

	public boolean isMenuCheckStrictly() {
		return this.menuCheckStrictly;
	}

	public void setMenuCheckStrictly(boolean menuCheckStrictly) {
		this.menuCheckStrictly = menuCheckStrictly;
	}

	public boolean isDeptCheckStrictly() {
		return this.deptCheckStrictly;
	}

	public void setDeptCheckStrictly(boolean deptCheckStrictly) {
		this.deptCheckStrictly = deptCheckStrictly;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public boolean isFlag() {
		return this.flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String[] getMenuIds() {
		return this.menuIds == null ? new String[0] : Arrays.copyOf(this.menuIds, this.menuIds.length);
	}

	public void setMenuIds(String[] menuIds) {
		this.menuIds = menuIds == null ? new String[0] : Arrays.copyOf(menuIds, menuIds.length);
	}

	public String[] getDeptIds() {
		return this.deptIds == null ? new String[0] : Arrays.copyOf(this.deptIds, this.deptIds.length);
	}

	public void setDeptIds(String[] deptIds) {
		this.deptIds = deptIds == null ? new String[0] : Arrays.copyOf(deptIds, deptIds.length);
	}

	public String getRoleId() {
		return this.roleId;
	}

	public String getSearchValue() {
		return this.searchValue;
	}

	public String getCreateBy() {
		return this.createBy;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public String getUpdateBy() {
		return this.updateBy;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public String getRemark() {
		return this.remark;
	}

	public Map<String, Object> getParams() {
		return this.params;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public RoleDTO() {
	}

	public RoleDTO(String roleId, String roleName, String roleKey, String roleSort, String dataScope, boolean menuCheckStrictly, boolean deptCheckStrictly, String status, String delFlag, boolean flag, String[] menuIds, String[] deptIds, String searchValue, String createBy, Date createTime, String updateBy, Date updateTime, String remark, Map<String, Object> params) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleKey = roleKey;
		this.roleSort = roleSort;
		this.dataScope = dataScope;
		this.menuCheckStrictly = menuCheckStrictly;
		this.deptCheckStrictly = deptCheckStrictly;
		this.status = status;
		this.delFlag = delFlag;
		this.flag = flag;
		this.menuIds = menuIds;
		this.deptIds = deptIds;
		this.searchValue = searchValue;
		this.createBy = createBy;
		this.createTime = createTime;
		this.updateBy = updateBy;
		this.updateTime = updateTime;
		this.remark = remark;
		this.params = params;
	}

}
