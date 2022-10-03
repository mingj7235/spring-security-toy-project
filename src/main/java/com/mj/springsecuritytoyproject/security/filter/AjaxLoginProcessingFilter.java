package com.mj.springsecuritytoyproject.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mj.springsecuritytoyproject.domain.AccountDto;
import com.mj.springsecuritytoyproject.security.token.AjaxAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 해당 Filter 가 작동하는 조건
 *
 * 1. URL 이 "/api/login" 인지 확인
 *
 * 2. Ajax 요청인지 확인
 *
 * 이후에 AuthenticationManager 에게 사용자의 요청을 담은 Authentication 을 Manager 에게 전달달 */
public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    private static final String X_REQUESTED_WITH = "X-Requested-With";

    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        super(new AntPathRequestMatcher("/ajaxLogin", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        if (!isAjax(request)) {
            throw new IllegalArgumentException("Authentication method not supported");
        }

        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);

        if (StringUtils.isEmpty(accountDto.getUsername()) || StringUtils.isEmpty(accountDto.getPassword())) {
            throw new AuthenticationServiceException("Username or Password not provided");
        }
        AjaxAuthenticationToken token = new AjaxAuthenticationToken(accountDto.getUsername(),accountDto.getPassword());

        return this.getAuthenticationManager().authenticate(token);
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
