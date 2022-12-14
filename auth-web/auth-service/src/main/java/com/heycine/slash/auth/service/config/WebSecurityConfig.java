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
 * ::??????????????????????????????
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/9/7 ??????11:36
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
	 * TODO ??????????????????
	 *
	 * @param auth
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				// ??????????????????
				.passwordEncoder(passwordEncoder)
				// ?????????
				.withUser("admin")
				// ??????
				.password(passwordEncoder.encode("admin123"))
				// ??????
				.roles("p1");
	}

	/**
	 * ??????????????????
	 */
	@Override
	public void configure(WebSecurity web) {
		// ????????????????????????????????????
		web.ignoring().antMatchers("/res/**", "/druid/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// ?????????????????????
				.formLogin().loginPage("/auth/login").loginProcessingUrl("/login")
				.and().authorizeRequests()

				// options????????????
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				// ?????????????????????????????????????????????????????????ResourceServerConfig
				.antMatchers("/**").permitAll()
				// ????????????????????????????????????
				.anyRequest().authenticated()

				// ?????????????????????????????????????????????
				.and()
				.logout().invalidateHttpSession(true).deleteCookies("JSESSIONID")
				.logoutSuccessHandler(customLogoutSuccessHandler).permitAll()

				// ?????????????????????csrf????????????
				.and()
				.cors()
				.and()
				.csrf().disable();
	}

	/**
	 * ??????authenticationManager ?????????AuthoricationServerConfig??????????????????AuthenticationManager????????????
	 * ??????????????????????????????????????????AuthenticationManager
	 *
	 * @return
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return new ProviderManager(Arrays.<AuthenticationProvider>asList(
				// ??????????????????
				userAuthenticationProvider,
				// ????????????
				smsAuthenticationProvider,
				// ????????????
				dingTalkAuthenticationProvider
		)
		);
	}

	/**
	 * ????????????????????????????????????
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
