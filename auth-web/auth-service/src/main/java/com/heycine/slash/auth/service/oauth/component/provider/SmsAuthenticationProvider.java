package com.heycine.slash.auth.service.oauth.component.provider;

import com.anji.captcha.util.StringUtils;
import com.heycine.slash.auth.business.constant.AuthConstant;
import com.heycine.slash.auth.service.oauth.authentication.UserAuthentication;
import com.heycine.slash.auth.service.oauth.feign.RemoteUserService;
import com.heycine.slash.auth.service.oauth.token.SmsAuthenticationToken;
import com.heycine.slash.auth.service.oauth.vo.LoginUserDTO;
import com.heycine.slash.auth.service.oauth.vo.RegisterUserDTO;
import com.heycine.slash.common.basic.http.R;
import com.heycine.slash.common.redis.service.RedisService;
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
 *
 * @author alikes
 */
@Slf4j
@Component
public class SmsAuthenticationProvider implements AuthenticationProvider {

	@Resource
	private RemoteUserService remoteUserService;

	@Resource
	private ApplicationEventPublisher publisher;

	@Resource
	private RedisService redisService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SmsAuthenticationToken smsAuthenticationToken = (SmsAuthenticationToken) authentication;
		String username = smsAuthenticationToken.getName();
		String code = smsAuthenticationToken.getCode();
		log.info("phone:{}, code:{}", username, code);

		// 短信验证码存在缓存中
		String cacheCode = redisService.getCacheObject(AuthConstant.SMS_LOGIN_CODE_KEY + username);
		if (StringUtils.isBlank(cacheCode) || !cacheCode.equals(code)) {
			redisService.deleteObject(AuthConstant.SMS_LOGIN_CODE_KEY + username);
			throw new BadCredentialsException("对不起，验证码已失效或输入错误,请重新获取!");
		}

		try {
			//TODO 获取本系统用户,如不存在,则进行新建并绑定,或输入已存在账号进行绑定
			R<LoginUserDTO> res = remoteUserService.login(null, username);
			UserAuthentication userAuthentication = new UserAuthentication();
			if (res.isOk()) {
				//用户存在
				LoginUserDTO userInfo = res.getData();
				userAuthentication.setUserId(userInfo.getUserId());
				userAuthentication.setCompanyId(userInfo.getCompanyId());
				userAuthentication.setUsername(userInfo.getUsername());
				userAuthentication.setPassword(userInfo.getPassword());
				userAuthentication.setEmail(userInfo.getEmail());
				userAuthentication.setPhone(userInfo.getPhone());
				userAuthentication.setStatus(userInfo.getStatus());
				userAuthentication.setPermissions(userInfo.getPermissions());
				userAuthentication.setUserType(userInfo.getUserType());
				userAuthentication.setNickName(userInfo.getNickName());
				userAuthentication.setRoles(userInfo.getRoles());
				Collection<? extends GrantedAuthority> authorities = Arrays.asList();
				SmsAuthenticationToken authenticationToken = new SmsAuthenticationToken(userAuthentication, smsAuthenticationToken.getCode(), authorities);
				authenticationToken.setDetails(smsAuthenticationToken.getDetails());
				return authenticationToken;
			} else {
				//用户不存在,新建用户,无角色
				RegisterUserDTO registerUserDTO = new RegisterUserDTO();
				registerUserDTO.setUserName(username);
				registerUserDTO.setPhone(username);
				registerUserDTO.setNickName("用户[" + username + "]");
				registerUserDTO.setRealName(username);
				registerUserDTO.setUserType("05");
				registerUserDTO.setRemark("短信注册用户");
				registerUserDTO.setPassword("SMS" + code);
				R<String> registerRes = remoteUserService.register(registerUserDTO);
				if (registerRes.isOk()) {
					R<LoginUserDTO> loginRes = remoteUserService.login(null, username);
					if (loginRes.isOk()) {
						LoginUserDTO userInfo = loginRes.getData();
						userAuthentication.setUserId(userInfo.getUserId());
						userAuthentication.setCompanyId(userInfo.getCompanyId());
						userAuthentication.setUsername(userInfo.getUsername());
						userAuthentication.setPassword(userInfo.getPassword());
						userAuthentication.setEmail(userInfo.getEmail());
						userAuthentication.setPhone(userInfo.getPhone());
						userAuthentication.setStatus(userInfo.getStatus());
						userAuthentication.setPermissions(userInfo.getPermissions());
						userAuthentication.setUserType(userInfo.getUserType());
						userAuthentication.setNickName(userInfo.getNickName());
						userAuthentication.setRoles(userInfo.getRoles());
						Collection<? extends GrantedAuthority> authorities = Arrays.asList();
						SmsAuthenticationToken authenticationToken = new SmsAuthenticationToken(userAuthentication, smsAuthenticationToken.getCode(), authorities);
						authenticationToken.setDetails(smsAuthenticationToken.getDetails());
						return authenticationToken;
					} else {
						throw new BadCredentialsException("对不起，登录失败,请重新登录!");
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			//发布密码不正确事件
			publisher.publishEvent(new ApplicationEvent(authentication) {
				@Override
				public Object getSource() {
					return authentication;
				}
			});
			throw new BadCredentialsException("对不起，登录失败,请重新登录!");
		}
		UserAuthentication userAuthentication = new UserAuthentication();
		userAuthentication.setUsername(username);

		//获取用户权限信息
		Collection<? extends GrantedAuthority> authorities = Arrays.asList();
		SmsAuthenticationToken authenticationToken = new SmsAuthenticationToken(userAuthentication, smsAuthenticationToken.getCode(), authorities);
		authenticationToken.setDetails(smsAuthenticationToken.getDetails());
		return authenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return SmsAuthenticationToken.class.isAssignableFrom(authentication);
	}

}

