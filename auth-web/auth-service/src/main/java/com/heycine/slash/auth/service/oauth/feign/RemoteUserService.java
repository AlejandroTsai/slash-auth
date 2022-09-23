package com.heycine.slash.auth.service.oauth.feign;

import java.util.List;

import com.heycine.slash.auth.service.oauth.vo.LoginUserDTO;
import com.heycine.slash.auth.service.oauth.vo.RegisterUserDTO;
import com.heycine.slash.auth.service.oauth.vo.RoleDTO;
import com.heycine.slash.auth.service.oauth.vo.UserDTO;
import com.heycine.slash.common.basic.http.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/*@FeignClient(
    contextId = "remoteUserService",
    value = "user-center",
    fallbackFactory = RemoteUserFallbackFactory.class
)*/
public interface RemoteUserService {
    @GetMapping({"/{userId}"})
    R<UserDTO> getInfo(@PathVariable("userId") String var1);

    @GetMapping({"/user/info/getUserInfoByUserId"})
    R<LoginUserDTO> getUserInfoByUserId(@RequestParam("userId") String var1);

    @PostMapping({"/user/info/getUserInfoListByUserIdList"})
    R<List<LoginUserDTO>> getUserInfoListByUserIdList(@RequestBody List<String> var1);

    @GetMapping({"/user/info/{username}"})
    R<LoginUserDTO> getUserInfo(@PathVariable("username") String var1);

    @GetMapping({"/user/login"})
    R<LoginUserDTO> login(@RequestParam(value = "companyNo",required = false) String var1, @RequestParam("username") String var2);

    @GetMapping({"/user/register"})
    R<String> register(@RequestBody RegisterUserDTO var1);

    @GetMapping({"/role/{roleId}"})
    R<RoleDTO> getRoleInfo(@PathVariable("roleId") String var1);

    @PutMapping({"/user/resetPwd"})
    R<Integer> resetPwd(@RequestBody UserDTO var1);

    @PostMapping({"/user/getUserList"})
    R<List<UserDTO>> getUserList(@RequestBody List<String> var1);

    @GetMapping({"/user/getCompanyId"})
    R<String> getCompanyId(@RequestParam("userId") String var1);
}
