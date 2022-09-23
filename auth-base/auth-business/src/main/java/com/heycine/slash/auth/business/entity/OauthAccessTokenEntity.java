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
@ApiModel(value="OauthAccessTokenEntity")
@TableName("oauth_access_token")
public class OauthAccessTokenEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "加密的access_token的值")
    private String tokenId;

    @ApiModelProperty(value = "OAuth2AccessToken.java对象序列化后的二进制数据")
    private Blob token;

    @ApiModelProperty(value = "加密过的username,client_id,scope")
    private String authenticationId;

    @ApiModelProperty(value = "登录的用户名")
    private String userName;

    @ApiModelProperty(value = "客户端ID")
    private String clientId;

    @ApiModelProperty(value = "OAuth2Authentication.java对象序列化后的二进制数据")
    private Blob authentication;

    @ApiModelProperty(value = "加密的refresh_token的值")
    private String refreshToken;


}
