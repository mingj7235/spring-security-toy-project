package com.mj.springsecuritytoyproject.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증에 성공한 후에 작업하는 핸들러.
 */
@Component
public class FormAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /**
     * 이전에 담고 있던 정보를 가져오기 위해서 Cache 정보를 가져온다.
     */
    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     *
     * @param request
     * @param response
     * @param authentication : 인증이 완료된 Authentication 정보
     */
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        setDefaultTargetUrl("/"); // 상속받은 메소드. 기본 TargetUrl 설정

        SavedRequest savedRequest = requestCache.getRequest(request, response); // 요청했던 정보

        if (savedRequest != null) { // null 인 경우는, 로그인하기 전에 아무런 자원에 접근하지 않았다면 null 이다
            String targetUrl = savedRequest.getRedirectUrl();
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }

}
