package com.heycine.slash.auth.service.oauth.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 *
 * @author zzj
 */
public class CustomOauthException extends OAuth2Exception {

    private static final long serialVersionUID = 1L;

    private int code = 999;

    private String message;

    public CustomOauthException(Integer code,String msg) {
        super(msg);
        this.message = msg;
        this.code = code;
    }

    public CustomOauthException(String msg) {
        super(msg);
        this.message = msg;
    }

    public CustomOauthException(String msg, Exception e) {
        super(msg, e);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
