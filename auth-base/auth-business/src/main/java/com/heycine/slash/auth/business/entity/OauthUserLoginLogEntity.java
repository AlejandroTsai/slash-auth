package com.heycine.slash.auth.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("oauth_user_login_log")
public class OauthUserLoginLogEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long batchId;

    /**
     * 公司ID
     */
    private String companyId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 登录状态
     */
    private Integer loginStatus;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 手机型号
     */
    private String modelType;

    /**
     * 传入参数
     */
    private String loginParams;
}
