package com.heycine.slash.auth.service.oauth.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 客户端认证过滤器
 *
 * @author alikes
 */
@Slf4j
public class CustomClientCredentialsTokenEndpointFilter extends ClientCredentialsTokenEndpointFilter {
	
	private AuthorizationServerSecurityConfigurer configurer;
	
	private AuthenticationEntryPoint authenticationEntryPoint;
	
	public CustomClientCredentialsTokenEndpointFilter(AuthorizationServerSecurityConfigurer configurer) {
		this.configurer = configurer;
	}
	
	public CustomClientCredentialsTokenEndpointFilter(AuthorizationServerSecurityConfigurer configurer, String path) {
		super(path);
		setRequiresAuthenticationRequestMatcher(new ClientCredentialsRequestMatcher(path));
		this.configurer = configurer;
	}
	
	@Override
	public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
		// 把父类的干掉
		super.setAuthenticationEntryPoint(authenticationEntryPoint);
		this.authenticationEntryPoint = authenticationEntryPoint;
	}
//    @Override
//    protected AuthenticationManager getAuthenticationManager() {
//        return configurer.and().getSharedObject(AuthenticationManager.class);
//    }
	
	
	@Override
	public void afterPropertiesSet() {
		setAuthenticationFailureHandler((httpServletRequest, httpServletResponse, e) -> {
			log.error(e.getMessage(), e);
			authenticationEntryPoint.commence(httpServletRequest, httpServletResponse, e);
		});
		setAuthenticationSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
			// 无操作-仅允许过滤器链继续到令牌端点
		});
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		if (!"POST".equalsIgnoreCase(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[]{"POST"});
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return authentication;
		}
		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");
		if (clientId == null) {
			String body = getRequestPostStr(request);
			if (StringUtils.isNotBlank(body) && JSONValidator.from(body).validate()) {
				Map<String, Object> map = JSONUtil.toBean(body, Map.class);
				if (map == null || map.get("client_id") == null) {
					throw new BadCredentialsException("No client credentials presented");
				}
			}
		}
		if (clientSecret == null) {
			String body = getRequestPostStr(request);
			if (StringUtils.isNotBlank(body) && JSONValidator.from(body).validate()) {
				Map<String, Object> map = JSONUtil.toBean(body, Map.class);
				if (map == null || map.get("client_secret") == null) {
					clientSecret = "";
				}
			}
		}
		clientId = clientId.trim();
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId, clientSecret);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	/**
	 * 描述:获取 post 请求内容
	 * <pre>
	 * 举例：
	 * </pre>
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getRequestPostStr(HttpServletRequest request)
			throws IOException {
		byte buffer[] = getRequestPostBytes(request);
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
			charEncoding = "UTF-8";
		}
		return new String(buffer, charEncoding);
	}
	
	/**
	 * 描述:获取 post 请求的 byte[] 数组
	 * <pre>
	 * 举例：
	 * </pre>
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static byte[] getRequestPostBytes(HttpServletRequest request)
			throws IOException {
		int contentLength = request.getContentLength();
		if (contentLength < 0) {
			return null;
		}
		byte buffer[] = new byte[contentLength];
		for (int i = 0; i < contentLength; ) {
			
			int readlen = request.getInputStream().read(buffer, i,
					contentLength - i);
			if (readlen == -1) {
				break;
			}
			i += readlen;
		}
		return buffer;
	}
	
	protected static class ClientCredentialsRequestMatcher implements RequestMatcher {
		
		private String path;
		
		public ClientCredentialsRequestMatcher(String path) {
			this.path = path;
			
		}
		
		@Override
		public boolean matches(HttpServletRequest request) {
			String uri = request.getRequestURI();
			int pathParamIndex = uri.indexOf(';');
			if (pathParamIndex > 0) {
				uri = uri.substring(0, pathParamIndex);
			}
			String clientId = request.getParameter("client_id");
			if (clientId == null) {
				return false;
			}
			if ("".equals(request.getContextPath())) {
				return uri.endsWith(path);
			}
			return uri.endsWith(request.getContextPath() + path);
		}
		
	}

}
