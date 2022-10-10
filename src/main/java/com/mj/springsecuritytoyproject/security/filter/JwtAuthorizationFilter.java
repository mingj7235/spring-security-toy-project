package com.mj.springsecuritytoyproject.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mj.springsecuritytoyproject.repository.UserRepository;
import com.mj.springsecuritytoyproject.security.service.AccountContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {


    private UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(final AuthenticationManager authenticationManager,
                                  UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 url이 요청됨");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader : " + jwtHeader);

        // header가 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request,response);
            return;
        }

        String jwtToken = request.getHeader("authorization").replace("Bearer ", "");

        String username = JWT.require(Algorithm.HMAC512("joshua")).build().verify(jwtToken).getClaim("username").asString();

        // 서명이 정상적으로 되었다는 것임, 즉 인증이되었다는 것
        if (username != null) {
            AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(username);


            //JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());

            // 강제로 시큐리티 세션에 접근하여 Authentication 객체를 저장한 것
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        chain.doFilter(request, response);
    }


}
