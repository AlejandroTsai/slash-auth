package com.heycine.slash.auth.service.config;

import com.heycine.slash.auth.business.constant.AuthConstant;
import com.heycine.slash.auth.service.oauth.converter.CheckAccessTokenConverter;
import com.heycine.slash.auth.service.oauth.converter.CustomUserAuthenticationConverter;
import com.heycine.slash.auth.service.oauth.entrypoint.CustomAuthenticationEntryPoint;
import com.heycine.slash.auth.service.oauth.exception.CustomWebResponseExceptionTranslator;
import com.heycine.slash.auth.service.oauth.filter.CustomClientCredentialsTokenEndpointFilter;
import com.heycine.slash.auth.service.oauth.component.granter.CustomClientTokenGranter;
import com.heycine.slash.auth.service.oauth.component.granter.DingTalkTokenGranter;
import com.heycine.slash.auth.service.oauth.component.granter.SmsTokenGranter;
import com.heycine.slash.auth.service.oauth.component.provider.CustomAuthenticationProvider;
import com.heycine.slash.auth.service.oauth.services.CustomTokenServices;
import com.heycine.slash.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ::优雅编程，此刻做起！
 * ::Elegant programming, start now!
 *
 * @author zhiji.zhou
 * @date 2022/9/7 上午11:41
 */
@Configuration
@EnableAuthorizationServer
@Slf4j
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Resource
	private CustomAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private DataSource dataSource;
	@Autowired
	private RedisService redisService;
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Autowired
	private CustomWebResponseExceptionTranslator customWebResponseExceptionTranslator;

	@Resource
	private CustomAuthenticationProvider customAuthenticationProvider;

	/**
	 * × 授权服务的权限配置信息
	 *
	 * @param security
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		CustomClientCredentialsTokenEndpointFilter endpointFilter = new CustomClientCredentialsTokenEndpointFilter(
				security, "/oauth/login");
		endpointFilter.afterPropertiesSet();
		endpointFilter.setAuthenticationEntryPoint(authenticationEntryPoint);
		endpointFilter.setAuthenticationManager(new ProviderManager(
						Arrays.asList(
								customAuthenticationProvider,
								new AnonymousAuthenticationProvider("default")
						)
				)
		);

		security
				// 资源服务发送的检查Token是否合法的 请求放过
				.checkTokenAccess("permitAll()")
				// 客户端发送的获取Token的服务方法
				.tokenKeyAccess("permitAll()")
				// 支持客户端表单提交
				.allowFormAuthenticationForClients()
				// 密码加密器
				.passwordEncoder(passwordEncoder())

				.authenticationEntryPoint(authenticationEntryPoint)
				.addTokenEndpointAuthenticationFilter(endpointFilter)
		;
		;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		/*clients.inMemory()
				// 客户端编号
				.withClient("joe")
				// 客户端的 密码
				.secret(new BCryptPasswordEncoder().encode("joe"))
				// 重定义地址,携带授权码或者 Token信息
				.redirectUris("http://www.baidu.com")
				// 不自动授权,用户自己选择
				.autoApprove(false)
				// 作用域 all read writer
				.scopes("all")
				// 授权服务支持的类型
				.authorizedGrantTypes(
						"authorization_code",
						"password",
						"client_credentials",
						"implicit",
						"refresh_token"
				)
				.resourceIds("c1") // 要访问的资源的编号
		;*/

//		clients.withClientDetails(jdbcClientDetailsService());

		clients.jdbc(dataSource);
	}

	/**
	 * × 访问服务的配置信息
	 *
	 * @param endpoints
	 * @throws Exception
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
				// 将authenticationManager交给服务器端点
				.authenticationManager(authenticationManager)
				// 自定义异常
				.exceptionTranslator(customWebResponseExceptionTranslator)
				// check_token转化
//				.accessTokenConverter(customAccessTokenConverter())
				// token存储服务
				.tokenServices(tokenService())
				/*.tokenStore(tokenStore())*/
				// todo
				.approvalStore(jdbcApprovalStore())
				// todo
				.reuseRefreshTokens(true)
				// 允许的方法
				.allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET, HttpMethod.OPTIONS)
				// 映射内置路径
				.pathMapping("/oauth/token", "/oauth/login")
				//四种授权模式+刷新令牌的模式+自定义授权模式
				.tokenGranter(tokenGranter());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 客户端信息来源
	 */
	@Bean
	public JdbcClientDetailsService jdbcClientDetailsService() {
		JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
		jdbcClientDetailsService.setPasswordEncoder(passwordEncoder());
		return jdbcClientDetailsService;
	}

	/**
	 * 测试密码
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		String password = new BCryptPasswordEncoder().encode("gateway-service");
		System.out.println(password);
		;
	}

	/**
	 * Token的存储方式
	 *
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {

		// 存储到数据库
//		return new JdbcTokenStore(dataSource);

		// 存储到redis
		RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
		tokenStore.setPrefix(AuthConstant.OAUTH_TOKEN_KEY_PREFIX);
		return tokenStore;
	}

	/**
	 * 授权信息保存方式
	 *
	 * @return
	 */
	@Bean
	public JdbcApprovalStore jdbcApprovalStore() {

		return new JdbcApprovalStore(dataSource);
	}

	/**
	 * 授权码模式数据来源
	 *
	 * @return
	 */
	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	/**
	 * token服务
	 *
	 * @return
	 */
	@Bean
	public CustomTokenServices tokenService() {
		CustomTokenServices tokenServices = new CustomTokenServices();
		// 配置token存储
		tokenServices.setTokenStore(tokenStore());
		// 开启支持refresh_token，此处如果之前没有配置，启动服务后再配置重启服务，可能会导致不返回token的问题，解决方式：清除redis对应token存储
		tokenServices.setSupportRefreshToken(true);
		// 复用refresh_token
		tokenServices.setReuseRefreshToken(true);
		// token有效期，设置12小时
		tokenServices.setAccessTokenValiditySeconds(60 * 60 * 24 * 30);
		// refresh_token有效期，设置一周
		tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 30 * 2);
		// token 索引到缓存中
		tokenServices.setRedisService(redisService);
		//设置客户端服务类
		tokenServices.setClientDetailsService(clientDetailsService);

		List<TokenEnhancer> delegates = new ArrayList<>();
		delegates.add(jwtAccessTokenConverter());

		TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
		enhancerChain.setTokenEnhancers(delegates);

		tokenServices.setTokenEnhancer(enhancerChain);
		tokenServices.setAuthenticationManager(authenticationManager);
		return tokenServices;
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//		jwtAccessTokenConverter.setSigningKey("C0SrjKMdYb1VTRFs");
		jwtAccessTokenConverter.setKeyPair(keyPair());
		jwtAccessTokenConverter.setAccessTokenConverter(defaultAccessTokenConverter());
		return jwtAccessTokenConverter;
	}

	@Bean
	public KeyPair keyPair() {
		// 密码
		char[] chars = "trax@123456".toCharArray();
		// 从classpath下的证书中获取秘钥对
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
				new ClassPathResource("jwt.jks"),
				chars
		);
		return keyStoreKeyFactory.getKeyPair("jwt", chars);
	}

	@Bean
	@Primary
	public AccessTokenConverter defaultAccessTokenConverter() {
		DefaultAccessTokenConverter converter = new DefaultAccessTokenConverter();
		converter.setUserTokenConverter(new CustomUserAuthenticationConverter());
		return converter;
	}

	@Bean
	@Primary
	public AccessTokenConverter customAccessTokenConverter() {
		CheckAccessTokenConverter converter = new CheckAccessTokenConverter();
		converter.setUserTokenConverter(new CustomUserAuthenticationConverter());
		return converter;
	}

	/**
	 * 授权模式 -组合器
	 *
	 * @return
	 */
	@Bean
	public TokenGranter tokenGranter() {
		return new TokenGranter() {
			private CompositeTokenGranter delegate;

			@Override
			public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
				if (delegate == null) {
					delegate = new CompositeTokenGranter(getTokenGranters());
				}
				return delegate.grant(grantType, tokenRequest);
			}
		};
	}

	/**
	 * 程序支持的授权类型
	 *
	 * @return
	 */
	private List<TokenGranter> getTokenGranters() {
		AuthorizationCodeServices authorizationCodeServices = authorizationCodeServices();
		OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);

		List<TokenGranter> tokenGranters = new ArrayList<>();
		// 添加授权码模式
		tokenGranters.add(new AuthorizationCodeTokenGranter(tokenService(), authorizationCodeServices, clientDetailsService, requestFactory));
		// 添加刷新令牌的模式
		tokenGranters.add(new RefreshTokenGranter(tokenService(), clientDetailsService, requestFactory));
		// 添加隐式授权模式
		tokenGranters.add(new ImplicitTokenGranter(tokenService(), clientDetailsService, requestFactory));
		// 添加客户端模式
		ClientCredentialsTokenGranter clientCredentialsTokenGranter = new ClientCredentialsTokenGranter(tokenService(), clientDetailsService, requestFactory);
		clientCredentialsTokenGranter.setAllowRefresh(false);
		tokenGranters.add(clientCredentialsTokenGranter);

		// 添加自定义客户端模式
		CustomClientTokenGranter customClientTokenGranter = new CustomClientTokenGranter(tokenService(), clientDetailsService, requestFactory);
		customClientTokenGranter.setAllowRefresh(false);
		tokenGranters.add(customClientTokenGranter);

		// 添加钉钉自定义授权模式（实际是密码模式的复制）
		tokenGranters.add(new DingTalkTokenGranter(authenticationManager, tokenService(), clientDetailsService, requestFactory));
		// 添加短信自定义授权模式（实际是密码模式的复制）
		tokenGranters.add(new SmsTokenGranter(authenticationManager, tokenService(), clientDetailsService, requestFactory));
		// 添加密码模式
		tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenService(), clientDetailsService, requestFactory));

		return tokenGranters;
	}


}
