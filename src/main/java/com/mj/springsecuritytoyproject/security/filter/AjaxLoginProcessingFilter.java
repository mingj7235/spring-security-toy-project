package com.mj.springsecuritytoyproject.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mj.springsecuritytoyproject.domain.AccountDto;
import com.mj.springsecuritytoyproject.security.token.AjaxAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        super(new AntPathRequestMatcher("/api/login")); // 이 url 로 요청이 왔을 때, 필터가 작동하도록 설정.
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (!isAjax(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);

        if (!StringUtils.hasText(accountDto.getUsername()) || !StringUtils.hasText(accountDto.getPassword())) {
            throw new IllegalStateException("Username or Password is empty");
        }

        /**
         * Ajax 용 Token 을 생성하고, 해당 토큰을 return 하여 AuthenticationManager 에게 전달을 한다.
         */
        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());
        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }


    /**
     * Ajax 인지 확인
     */
    private boolean isAjax(final HttpServletRequest request) {

        /**
         * Header 에 클라이언트 쪽에서 헤더에 정보를 넣기로 약속하여 검증하는 것임
         */
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        }

        return false;
    }

}
