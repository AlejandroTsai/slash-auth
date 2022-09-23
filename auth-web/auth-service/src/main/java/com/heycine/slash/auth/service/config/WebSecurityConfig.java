package com.heycine.slash.auth.service.config;

import com.heycine.slash.auth.service.oauth.component.provider.CustomAuthenticationProvider;
import com.heycine.slash.auth.service.oauth.component.provider.DingTalkAuthenticationProvider;
import com.heycine.slash.auth.service.oauth.component.provider.SmsAuthenticationProvider;
import com.heycine.slash.auth.service.oauth.component.provider.UserAuthenticationProvider;
import com.heycine.slash.auth.service.oauth.handler.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * ::优雅编程，此刻做起！
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/9/7 上午11:36
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Resource
	private DingTalkAuthenticationProvider dingTalkAuthenticationProvider;
	@Resource
	private SmsAuthenticationProvider smsAuthenticationProvider;
	@Resource
	private UserAuthenticationProvider userAuthenticationProvider;
	@Resource
	private CustomLogoutSuccessHandler customLogoutSuccessHandler;

	/**
	 * TODO 配置账号测试
	 *
	 * @param auth
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				// 密码加密算法
				.passwordEncoder(passwordEncoder)
				// 用户名
				.withUser("admin")
				// 密码
				.password(passwordEncoder.encode("admin123"))
				// 角色
				.roles("p1");
	}

	/**
	 * 放行静态资源
	 */
	@Override
	public void configure(WebSecurity web) {
		// 解决静态资源被拦截的问题
		web.ignoring().antMatchers("/res/**", "/druid/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// 默认的表单登录
				.formLogin().loginPage("/auth/login").loginProcessingUrl("/login")
				.and().authorizeRequests()

				// options方式放行
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				// 白名单放行，此处不生效，请查看资源服务ResourceServerConfig
				.antMatchers("/**").permitAll()
				// 任何请求，都需要身份认证
				.anyRequest().authenticated()

				// 登出的时候，删除会话，进行处理
				.and()
				.logout().invalidateHttpSession(true).deleteCookies("JSESSIONID")
				.logoutSuccessHandler(customLogoutSuccessHandler).permitAll()

				// 允许跨域，关闭csrf跨域防护
				.and()
				.cors()
				.and()
				.csrf().disable();
	}

	/**
	 * 重写authenticationManager 不重写AuthoricationServerConfig认证中心中的AuthenticationManager无法注入
	 * 将自定义密码认证处理器注册到AuthenticationManager
	 *
	 * @return
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return new ProviderManager(Arrays.<AuthenticationProvider>asList(
				// 用户密码登录
				userAuthenticationProvider,
				// 短信登录
				smsAuthenticationProvider,
				// 钉钉登录
				dingTalkAuthenticationProvider
		)
		);
	}

	/**
	 * 定义自定义密码认证处理器
	 *
	 * @return
	 */
	@Bean
	public CustomAuthenticationProvider customAuthenticationProvider() {
		ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(clientDetailsService);
		clientDetailsUserDetailsService.setPasswordEncoder(passwordEncoder);

		CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
		customAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		customAuthenticationProvider.setUserDetailsService(clientDetailsUserDetailsService);

		return customAuthenticationProvider;
	}

}
