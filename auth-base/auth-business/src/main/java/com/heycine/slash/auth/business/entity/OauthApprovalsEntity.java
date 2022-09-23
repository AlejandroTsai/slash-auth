package com.heycine.slash.auth.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OauthApprovalsEntity")
@TableName("oauth_approvals")
public class OauthApprovalsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录的用户名")
    @TableField("userId")
    private String userid;

    @ApiModelProperty(value = "客户端ID")
    @TableField("clientId")
    private String clientid;

    @ApiModelProperty(value = "申请的权限范围")
    private String scope;

    @ApiModelProperty(value = "状态（Approve或Deny）")
    private String status;

    @ApiModelProperty(value = "过期时间")
    @TableField("expiresAt")
    private LocalDateTime expiresat;

    @ApiModelProperty(value = "最终修改时间")
    @TableField("lastModifiedAt")
    private LocalDateTime lastmodifiedat;


}
