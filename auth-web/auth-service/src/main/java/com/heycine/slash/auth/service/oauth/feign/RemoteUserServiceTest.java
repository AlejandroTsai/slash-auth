package com.heycine.slash.auth.service.oauth.feign;
import java.util.Date;
import com.google.common.collect.Sets;

import com.heycine.slash.auth.service.oauth.vo.LoginUserDTO;
import com.heycine.slash.auth.service.oauth.vo.RegisterUserDTO;
import com.heycine.slash.auth.service.oauth.vo.RoleDTO;
import com.heycine.slash.auth.service.oauth.vo.UserDTO;
import com.heycine.slash.common.basic.http.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ::优雅编程，此刻做起！
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/9/22 下午7:20
 */
@Service
public class RemoteUserServiceTest implements RemoteUserService{

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public R<UserDTO> getInfo(String var1) {
		return null;
	}

	@Override
	public R<LoginUserDTO> getUserInfoByUserId(String var1) {
		return null;
	}

	@Override
	public R<List<LoginUserDTO>> getUserInfoListByUserIdList(List<String> var1) {
		return null;
	}

	@Override
	public R<LoginUserDTO> getUserInfo(String var1) {
		return null;
	}

	@Override
	public R<LoginUserDTO> login(String var1, String var2) {
		if ("admin".equals(var2)) {
			LoginUserDTO admin = new LoginUserDTO();
			admin.setUserId("1");
			admin.setCompanyId("1");
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setNickName("大猛1");
			admin.setEmail("493816389@qq.COM");
			admin.setPhone("18990985032");
			admin.setUserType("1");
			admin.setSex("男");
			admin.setAvatar("");
			admin.setStatus("0");
			admin.setDeleted("0");
			admin.setLoginIp("127.0.0.1");
			admin.setLoginDate(new Date());
			admin.setRemark("测试用户啦");

			admin.setPermissions(Sets.newHashSet());
			admin.setRoleIds(Sets.newHashSet());
			admin.setRoles(Sets.newHashSet());
			admin.setPostIds(Sets.newHashSet());
			admin.setAccountNonExpired(false);
			admin.setAccountNonLocked(false);
			admin.setCredentialsNonExpired(false);
			admin.setEnabled(false);

			return R.ok(admin);
		} else {
			LoginUserDTO other = new LoginUserDTO();
			other.setUserId("2");
			other.setCompanyId("2");
			other.setUsername("18990985032");
			other.setPassword(passwordEncoder.encode("joe"));
			return R.ok(other);
		}
	}

	@Override
	public R<String> register(RegisterUserDTO var1) {
		return null;
	}

	@Override
	public R<RoleDTO> getRoleInfo(String var1) {
		return null;
	}

	@Override
	public R<Integer> resetPwd(UserDTO var1) {
		return null;
	}

	@Override
	public R<List<UserDTO>> getUserList(List<String> var1) {
		return null;
	}

	@Override
	public R<String> getCompanyId(String var1) {
		return null;
	}
}
