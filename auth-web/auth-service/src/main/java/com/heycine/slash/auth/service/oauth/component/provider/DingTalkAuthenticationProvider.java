package com.heycine.slash.auth.service.oauth.component.provider;

import com.alibaba.fastjson.JSON;

import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.heycine.slash.auth.service.oauth.authentication.UserAuthentication;
import com.heycine.slash.auth.service.oauth.token.DingTalkAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;


/**
 * 钉钉扫码登录
 * @author alikes
 */
@Slf4j
@Component
public class DingTalkAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private ApplicationEventPublisher publisher;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DingTalkAuthenticationToken dingTalkAuthenticationToken = (DingTalkAuthenticationToken) authentication;
        String username = dingTalkAuthenticationToken.getName();
        try {
            GetUserResponseBody getUserResponseBody = getUserinfo(username);
            String openId = getUserResponseBody.getOpenId();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            //发布密码不正确事件
            publisher.publishEvent(new ApplicationEvent(authentication){
                @Override
                public Object getSource() {
                    return authentication;
                }
            });
            throw new BadCredentialsException("对不起，登录失败,请重新扫码登录");
        }
        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUsername(username);

        //获取用户权限信息
        Collection<? extends GrantedAuthority> authorities = Arrays.asList();
        DingTalkAuthenticationToken authenticationToken = new DingTalkAuthenticationToken(userAuthentication, dingTalkAuthenticationToken.getState(),authorities);
        authenticationToken.setDetails(dingTalkAuthenticationToken.getDetails());
        return authenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return DingTalkAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 获取token
     * @param code
     * @return
     * @throws Exception
     */
    private String getAccessToken(String code) throws Exception {
        com.aliyun.dingtalkoauth2_1_0.Client client = new com.aliyun.dingtalkoauth2_1_0.Client(config());
        GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                //应用基础信息-应用信息的AppKey,请务必替换为开发的应用AppKey
                .setClientId("dingzlyopftigii4e8dz")
                //应用基础信息-应用信息的AppSecret，,请务必替换为开发的应用AppSecret
                .setClientSecret("vCs1sYwTchaiAfa_sG_ZC8DxijeTsZAsRL0FLmW6160Cq-Ek6gtWMoZ16H1yshws")
                .setCode(code)
                .setGrantType("authorization_code");
        GetUserTokenResponse getUserTokenResponse = client.getUserToken(getUserTokenRequest);
        //获取用户个人token
        String accessToken = getUserTokenResponse.getBody().getAccessToken();
        return  accessToken;
    }

    /**
     * 获取用户个人信息
     * @param code
     * @return
     * @throws Exception
     */
    public GetUserResponseBody getUserinfo(String code) throws Exception {
        com.aliyun.dingtalkcontact_1_0.Client client = new com.aliyun.dingtalkcontact_1_0.Client(config());
        GetUserHeaders getUserHeaders = new GetUserHeaders();
        getUserHeaders.xAcsDingtalkAccessToken = getAccessToken(code);
        GetUserResponse cetUserResponse = client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
        GetUserResponseBody getUserResponseBody = cetUserResponse.getBody();
        //获取用户个人信息，如需获取当前授权人的信息，unionId参数必须传me
        String me = JSON.toJSONString(getUserResponseBody);
        log.info(me);
        return getUserResponseBody;
    }

    private Config config(){
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return config;
    }

}

