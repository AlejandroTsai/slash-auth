package com.heycine.slash.auth.service.config;

import com.heycine.slash.auth.business.constant.AuthConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * @author alikes
 */
@Configuration
@EnableResourceServer
@Order(1)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources
				// 接收访问的资源id
				.resourceId("auth-service");
				// 无状态的
//				.tokenServices(tokenServices()).stateless(true);
	}

	/**
	 * 远程验证Token信息
	 *
	 * @return
	 */
	/*public ResourceServerTokenServices tokenServices() {
		RemoteTokenServices tokenServices = new RemoteTokenServices();
		tokenServices.setCheckTokenEndpointUrl("http://localhost:9899/oauth/check_token");
		tokenServices.setClientId("joe");
		tokenServices.setClientSecret("joe");
		return tokenServices;
	}*/

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				// 表单登录
				.formLogin().loginPage("/auth/login").loginProcessingUrl("/login")
				.and().authorizeRequests()
				// options方法放行
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				// 白名单放行，此处生效
				.antMatchers(AuthConstant.WHITE_LIST).permitAll()
				// 任何请求
				.anyRequest().authenticated()
				// 登出处理
				.and()
				.logout().invalidateHttpSession(true).deleteCookies("JSESSIONID")
				// 关闭跨域防御
				.and()
				.csrf().disable();
	}

}
