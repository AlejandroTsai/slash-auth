package com.heycine.slash.auth.service.oauth.exception;

import com.heycine.slash.common.basic.http.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zzj
 */
@Slf4j
@Component
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

	private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

	public static final String TRACE_ID = "traceId";

	@Override
	public ResponseEntity<R> translate(Exception e) {
		HttpServletRequest request = getRequestAttributes().getRequest();
		String traceId = null != request.getHeader(TRACE_ID) ? request.getHeader(TRACE_ID) : request.getParameter(TRACE_ID);
		//异常栈获取 OAuth2Exception 异常
		Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);

		Exception ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
		if (ase instanceof CustomOauthException) {
			return new ResponseEntity<>(R.fail(((CustomOauthException) ase).getCode(), e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		if (ase instanceof InvalidGrantException) {
			return new ResponseEntity<>(R.fail(400, e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		if (ase instanceof BadClientCredentialsException) {
			return new ResponseEntity<>(R.fail(400, e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		if (ase instanceof BadCredentialsException) {
			return new ResponseEntity<>(R.fail(400, e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		if (ase instanceof InvalidTokenException) {
			return new ResponseEntity<>(R.fail(401, e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
		if (ase != null) {
			return new ResponseEntity<>(R.fail(401, e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
		if (ase instanceof AccessDeniedException) {
			return new ResponseEntity<>(R.fail(403, e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		ase = (HttpRequestMethodNotSupportedException) throwableAnalyzer.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
		if (ase instanceof HttpRequestMethodNotSupportedException) {
			return new ResponseEntity<>(R.fail(405, e.getMessage()).traceId(traceId), HttpStatus.OK);
		}

		return new ResponseEntity<>(R.fail(500, e.getMessage()).traceId(traceId), HttpStatus.OK);
	}

	public static ServletRequestAttributes getRequestAttributes() {
		try {
			RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
			return (ServletRequestAttributes) attributes;
		} catch (Exception var1) {
			log.error(var1.getMessage(), var1);
			return null;
		}
	}

}
