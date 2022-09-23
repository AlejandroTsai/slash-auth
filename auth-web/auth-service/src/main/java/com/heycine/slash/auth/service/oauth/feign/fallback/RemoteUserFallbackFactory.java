package com.heycine.slash.auth.service.oauth.feign.fallback;

import com.heycine.slash.auth.service.oauth.vo.LoginUserDTO;
import com.heycine.slash.auth.service.oauth.vo.RegisterUserDTO;
import com.heycine.slash.auth.service.oauth.vo.RoleDTO;
import com.heycine.slash.auth.service.oauth.vo.UserDTO;
import com.heycine.slash.common.basic.http.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import com.heycine.slash.auth.service.oauth.feign.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {

	@Override
	public RemoteUserService create(Throwable throwable) {
		log.error("用户服务调用失败:{}", throwable);

		return new RemoteUserService() {
			@Override
			public R<UserDTO> getInfo(String userId) {
				return R.fail("获取用户失败:" + throwable.getMessage());
			}

			@Override
			public R<LoginUserDTO> getUserInfoByUserId(String userId) {
				return R.fail("获取用户失败:" + throwable.getMessage());
			}

			@Override
			public R<List<LoginUserDTO>> getUserInfoListByUserIdList(List<String> userIdList) {
				return R.fail("获取用户信息查询失败:" + throwable.getMessage());
			}

			@Override
			public R<LoginUserDTO> getUserInfo(String username) {
				return R.fail("获取用户失败:" + throwable.getMessage());
			}

			@Override
			public R<LoginUserDTO> login(String companyNo, String username) {
				return R.fail("获取用户信息查询失败:" + throwable.getMessage());
			}

			@Override
			public R<RoleDTO> getRoleInfo(String roleId) {
				return R.fail("获取角色查询失败:" + throwable.getMessage());
			}

			@Override
			public R<String> register(RegisterUserDTO user) {
				return R.fail("注册失败:" + throwable.getMessage());
			}

			@Override
			public R<Integer> resetPwd(UserDTO user) {
				return R.fail("重置密码失败:" + throwable.getMessage());
			}

			@Override
			public R<List<UserDTO>> getUserList(List<String> userIds) {
				return R.fail("获取用户列表失败:" + throwable.getMessage());
			}

			@Override
			public R<String> getCompanyId(String userId) {
				return R.fail("获取企业id失败:" + throwable.getMessage());
			}
		};
	}

}
