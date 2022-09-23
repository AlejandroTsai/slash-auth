package com.heycine.slash.auth.business.constant;

/**
 * 常量类
 *
 * @author alikes
 */
public class AuthConstant {

	public final static String[] WHITE_LIST = new String[]{
			"/publicKey"
			, "/webjars/**"
			, "/v2/api-docs"
			, "/v3/api-docs"
			, "/swagger-ui.html**"
			, "/swagger-resources/**"
			, "/doc.html**"

			, "/test/**"
			, "/code/**"
			, "/captcha/**"
			, "/actuator/**"
			, "/auth/login"
			, "/login"
			, "/res/**"
			, "/static/**"
			, "/oauth/token**"
			, "/oauth/removeToken"
			, "/oauth/login**"
			, "/oauth/getToken"
			, "/oauth/check_token"
			, "/public/**"
			, "/callback/**"
	};


	public static final String USER_NAME = "user_name";

	public static final String USER_ID = "user_id";

	public static final String COMPANY_ID = "company_id";

	public static final String USER_TYPE = "user_type";

	public static final String NICK_NAME = "nick_name";

	public static final String AUTHORITIES = "authorities";

	public static final String AUTH_TYPE = "auth_type";

	public static final String LOCK_SMS_CODE_KEY_PREFIX = "ATHENA:LOCK:";

	/**
	 * 验证码 redis key
	 */
	public static final String CAPTCHA_CODE_KEY = "captcha_codes:";
	/**
	 * 短信验证码 redis key
	 */
	public static final String SMS_CAPTCHA_CODE_KEY = "captcha_codes:sms:";

	public static final String OAUTH_TOKEN_KEY_PREFIX = "AUTH-SERVICE:OAUTH:";
	/**
	 * 短信验证码 redis key
	 */
	public static final String SMS_LOGIN_CODE_KEY = "login:sms:";

}
