package com.heycine.slash.auth.service.controller;

import com.heycine.slash.auth.business.constant.AuthConstant;
import com.heycine.slash.auth.business.constant.Constants;
import com.heycine.slash.common.basic.http.ErrorCode;
import com.heycine.slash.common.basic.http.R;
import com.heycine.slash.common.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * 发送短信验证码
 *
 * @author alikes
 */
@Slf4j
@RestController
@RequestMapping("/public/sms")
@Api(tags = "短信消息")
public class SmsController {
	@Resource
	private RedissonClient redissonClient;

	@Resource
	private RedisService redisService;

	/**
	 * 短信过期时间，10分钟
	 */
	private final static long EXPIRE_TIME = Constants.CAPTCHA_EXPIRATION * 60;


	@GetMapping("/sendSmsCaptcha")
	@ApiOperation("发送短信消息")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "大陆手机号码", name = "phone", paramType = "query", required = true, dataTypeClass = String.class)
	})
	public R<?> sendSmsCaptcha(@RequestParam String phone) {
		R<?> result = R.fail(ErrorCode.SERVICE_ERROR_C0501);

		String code = getRandomCode();
		log.info("开始发送sms消息: {},{}", phone, code);

		RLock codeLock = redissonClient.getLock(AuthConstant.LOCK_SMS_CODE_KEY_PREFIX + phone);
		// 支持过期解锁功能,10秒钟以后自动解锁, 无需调用unlock方法手动解锁 lock.lock(10, TimeUnit.SECONDS);
		try {
			boolean isLock = codeLock.tryLock(10, 60, TimeUnit.SECONDS);
			// 判断是否获取锁
			if (!isLock) {
				// 获取失败
				return R.fail("该手机号码验证码已发送,请稍后再试");
			}
			try {
				// 短信验证码存在缓存中
				redisService.setCacheObject(AuthConstant.SMS_LOGIN_CODE_KEY + phone, code, EXPIRE_TIME, TimeUnit.SECONDS);
				// 执行发送短信 TODO：模拟成功
				result = R.ok("模拟短信发送成功！");
				log.info("成功发送sms消息: {}，{}", phone, code);
			} finally {
				// 释放锁
				codeLock.unlock();
			}
		} catch (InterruptedException e) {
			log.error("定时任务获取分布式锁异常", e);
		}
		return result;
	}

	public static String getRandomCode() {

		return String.valueOf(new SecureRandom().nextInt(900000) + 100000);
	}

}
