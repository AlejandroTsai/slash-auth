package com.heycine.slash.auth.service.oauth.entrypoint;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heycine.slash.common.basic.http.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zzj
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,AuthenticationException authException) throws ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String msg = authException.getMessage();
        R result = R.fail(HttpServletResponse.SC_UNAUTHORIZED, msg);
        StringBuffer sb = new StringBuffer(request.getScheme());
        sb.append("://").append(request.getLocalAddr()).append(":").append(request.getLocalPort()).append(request.getServletPath());
        result.setPath(sb.toString());
        result.setTimestamp(System.currentTimeMillis());
        result.setTraceId(UUID.randomUUID().toString());

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",HttpServletResponse.SC_UNAUTHORIZED);
        resultMap.put("status",""+HttpServletResponse.SC_UNAUTHORIZED);
        resultMap.put("msg",msg);
        resultMap.put("status_desc",msg);
        resultMap.put("traceId",UUID.randomUUID().toString());
        resultMap.put("timestamp",System.currentTimeMillis());
        resultMap.put("path",sb.toString());
        resultMap.put("ext",new HashMap<>());

        try {
            response.setHeader("Content-Type", "application/json;charset=utf-8");
            response.getWriter().print(JSONUtil.toJsonStr(resultMap));
            response.getWriter().flush();
        } catch (Exception e) {
            throw new ServletException();
        }
    }

}
