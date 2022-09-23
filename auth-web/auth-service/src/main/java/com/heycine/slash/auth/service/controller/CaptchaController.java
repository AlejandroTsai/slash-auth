package com.heycine.slash.auth.service.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.util.StringUtils;
import com.heycine.slash.common.basic.http.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping({"/captcha"})
@RestController
public class CaptchaController {

	@Autowired
	private CaptchaService captchaService;

	@PostMapping("/get")
	public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) {
		assert request.getRemoteHost()!=null;
		data.setBrowserInfo(getRemoteId(request));
		return captchaService.get(data);
	}

	@PostMapping("/check")
	public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) {
		data.setBrowserInfo(getRemoteId(request));
		return captchaService.check(data);
	}

	//@PostMapping("/verify")
	public ResponseModel verify(@RequestBody CaptchaVO data, HttpServletRequest request) {
		return captchaService.verification(data);
	}

	public static final String getRemoteId(HttpServletRequest request) {
		String xfwd = request.getHeader("X-Forwarded-For");
		String ip = getRemoteIpFromXfwd(xfwd);
		String ua = request.getHeader("user-agent");
		if (StringUtils.isNotBlank(ip)) {
			return ip + ua;
		}
		return request.getRemoteAddr() + ua;
	}

	private static String getRemoteIpFromXfwd(String xfwd) {
		if (StringUtils.isNotBlank(xfwd)) {
			String[] ipList = xfwd.split(",");
			return StringUtils.trim(ipList[0]);
		}
		return null;
	}

}
