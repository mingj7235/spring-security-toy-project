package com.mj.springsecuritytoyproject.security.common;

import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Getter
public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private String secretKey;

    public FormWebAuthenticationDetails(final HttpServletRequest request) {
        super(request);
        secretKey = request.getParameter("secret_key"); // login view 에서 전달한 secret_key 가 매핑되어 저장
    }

}
