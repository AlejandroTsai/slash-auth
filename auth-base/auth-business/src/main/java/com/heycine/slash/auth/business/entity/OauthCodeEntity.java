package com.heycine.slash.auth.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OauthCodeEntity")
@TableName("oauth_code")
public class OauthCodeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "授权码(未加密)")
    private String code;

    @ApiModelProperty(value = "AuthorizationRequestHolder.java对象序列化后的二进制数据")
    private byte[] authentication;


}
