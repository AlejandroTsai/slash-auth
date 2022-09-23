package com.heycine.slash.auth.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Blob;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OauthRefreshTokenEntity")
@TableName("oauth_refresh_token")
public class OauthRefreshTokenEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "加密过的refresh_token的值")
    private String tokenId;

    @ApiModelProperty(value = "OAuth2RefreshToken.java对象序列化后的二进制数据 ")
    private Blob token;

    @ApiModelProperty(value = "OAuth2Authentication.java对象序列化后的二进制数据")
    private Blob authentication;


}
