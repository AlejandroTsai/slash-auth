package com.heycine.slash.auth.service.oauth.component.provider;

import com.heycine.slash.auth.business.entity.OauthUserLoginLogEntity;
import com.heycine.slash.auth.business.enums.UserLoginLogEnum;
import com.heycine.slash.auth.business.repository.OauthUserLoginLogRepository;
import com.heycine.slash.auth.service.oauth.authentication.UserAuthentication;
import com.heycine.slash.auth.service.oauth.services.IntegrationUserDetailsServices;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户自定义身份认证-账号密码
 *
 * @author alikes
 * @Date: 2019/7/2 17:17
 * @Version: 2.0
 */
@Slf4j
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private IntegrationUserDetailsServices integrationUserDetailsServices;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private ApplicationEventPublisher publisher;

    @Resource
    private OauthUserLoginLogRepository oauthUserLoginLogRepository;

    /**
     * 认证处理，返回一个Authentication的实现类则代表认证成功，返回null则代表认证失败
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("用户名不能为空!");
        }
        if (StringUtils.isBlank(password)) {
            throw new BadCredentialsException("密码不能为空!");
        }

        //获取用户信息
        UserAuthentication user = integrationUserDetailsServices.loadUserByUsername(username);
        //比较前端传入的密码明文，和数据库中加密的密码是否相等
        if (!passwordEncoder.matches(password, user.getPassword())) {
            //发布密码不正确事件
            publisher.publishEvent(new ApplicationEvent(authentication) {
                @Override
                public Object getSource() {
                    return authentication;
                }
            });
            try {
                this.saveUserLoginLog(user, (Map<String, String>) authentication.getDetails(), false);
            } catch (Exception e) {
                log.error("登录失败写入记录表失败, 失败原因为 {}", e.getMessage(), e);
            }
            throw new BadCredentialsException("用户名或密码错误!");
        } else {
            // 增加这个不影响auth 流程
            try {
                this.saveUserLoginLog(user, (Map<String, String>)authentication.getDetails(), true);
            } catch (Exception e) {
                log.error("登录成功写入记录表失败, 失败原因为 {}", e.getMessage(), e);
            }
        }

        //获取用户权限信息
        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveUserLoginLog(UserAuthentication user, Map<String, String> map, Boolean flag) {
        // 定义传入参数
        StringBuilder loginParams = new StringBuilder();
        OauthUserLoginLogEntity oauthUserLoginLog = new OauthUserLoginLogEntity();
        oauthUserLoginLog.setCompanyId(user.getCompanyId());
        oauthUserLoginLog.setUserId(user.getUserId());
        oauthUserLoginLog.setUserName(user.getUsername());
        oauthUserLoginLog.setNickName(user.getNickName());
        if (flag) {
            oauthUserLoginLog.setLoginStatus(UserLoginLogEnum.LOGIN_LOG_SUCCESS.getCode());
        } else {
            oauthUserLoginLog.setLoginStatus(UserLoginLogEnum.LOGIN_LOG_FAILED.getCode());
        }
        oauthUserLoginLog.setLoginTime(LocalDateTime.now());
        oauthUserLoginLog.setLoginIp(map.get("ip"));
        oauthUserLoginLog.setModelType(map.get("model_type"));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            log.info("当前执行的mapKey" + entry.getKey());
            if (!"ip".equals(entry.getKey()) && !"model_type".equals(entry.getKey())) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue();
                loginParams.append(mapKey).append(":").append(mapValue).append(";");
            }
        }
        log.info("当前的loginParams-----------------------------" + loginParams);

        oauthUserLoginLog.setLoginParams(loginParams.toString());

        oauthUserLoginLogRepository.save(oauthUserLoginLog);

    }

    /**
     * 如果该AuthenticationProvider支持传入的Authentication对象，则返回true
     *
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }

}
