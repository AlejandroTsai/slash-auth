package com.heycine.slash.auth.api;
import com.heycine.slash.common.basic.http.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AI任务-Feign
 *
 * @author zzj
 */
@FeignClient(contextId = "ExampleFeignClient", value = "example-service")
public interface ExampleFeignClient {

	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	@GetMapping({"/user/getUserInfo"})
	R<Object> getUserInfo(@RequestParam("userId") String userId);

}
