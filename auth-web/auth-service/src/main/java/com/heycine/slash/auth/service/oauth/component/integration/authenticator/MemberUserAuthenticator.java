package com.heycine.slash.auth.service.oauth.component.integration.authenticator;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.heycine.slash.auth.business.constant.Constants;
import com.heycine.slash.auth.business.enums.AuthType;
import com.heycine.slash.auth.business.enums.UserStatus;
import com.heycine.slash.auth.service.oauth.authentication.UserAuthentication;
import com.heycine.slash.auth.service.oauth.exception.CustomOauthException;
import com.heycine.slash.auth.service.oauth.feign.RemoteUserService;
import com.heycine.slash.auth.service.oauth.component.integration.AbstractIntegrationAuthenticator;
import com.heycine.slash.auth.service.oauth.component.integration.basic.IntegrationAuthentication;
import com.heycine.slash.auth.service.oauth.vo.LoginUserDTO;
import com.heycine.slash.common.basic.http.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户名密码
 *
 * @author alikes
 */
@Primary
@Component("memberUserAuthenticator")
public class MemberUserAuthenticator extends AbstractIntegrationAuthenticator {

	@Resource
	private RemoteUserService remoteUserService;

	@Override
	public boolean support(IntegrationAuthentication integrationAuthentication) {

		return StringUtils.isNotBlank(integrationAuthentication.getAuthType())
				&& AuthType.MEMBER.equals(AuthType.valueOf(integrationAuthentication.getAuthType().toUpperCase()));
	}

	@Override
	public UserAuthentication authenticate(IntegrationAuthentication integrationAuthentication) {
		UserAuthentication userAuthentication = new UserAuthentication();
		String username = integrationAuthentication.getUsername();
		String companyNo = null;
		String userName = username;
		String splitChar = "@";
		if (username.contains(splitChar)) {
			companyNo = username.split(splitChar)[1];
			userName = username.split(splitChar)[0];
		}
		R<LoginUserDTO> userResult = remoteUserService.login(companyNo, userName);
		if (Constants.FAIL == userResult.getCode()) {
			throw new CustomOauthException(userResult.getMsg());
		}
		if (ObjectUtils.isNull(userResult) || ObjectUtils.isNull(userResult.getData())) {
			throw new CustomOauthException(401, "对不起，您的账号：" + username + " 不存在");
		}
		LoginUserDTO userInfo = userResult.getData();
		if (UserStatus.DELETED.getCode().equals(userInfo.getDeleted())) {
			throw new CustomOauthException(401, "对不起，您的账号：" + username + " 已被删除");
		}
		if (UserStatus.DISABLE.getCode().equals(userInfo.getStatus())) {
			throw new CustomOauthException(401, "对不起，您的账号：" + username + " 已停用");
		}
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
		userAuthentication.setAuthType(integrationAuthentication.getAuthType());
		return userAuthentication;
	}

}
