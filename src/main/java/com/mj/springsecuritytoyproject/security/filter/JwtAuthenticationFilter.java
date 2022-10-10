package com.mj.springsecuritytoyproject.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mj.springsecuritytoyproject.domain.Account;
import com.mj.springsecuritytoyproject.security.service.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        //1. username, password 받아서
        try {
            ObjectMapper om = new ObjectMapper();
            Account member = om.readValue(request.getInputStream(), Account.class);
            System.out.println(member);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());

            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행되는 것임
            // 함수가 실행이 정상이면 authentication이 리턴된다.
            //즉, DB에 있는 username과 password가 일치한다는 것이다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            AccountContext principal = (AccountContext) authentication.getPrincipal();
            System.out.println("로그인 완료됨 : " + principal.getAccount().getUsername()); // 로그인이 정상적으로 되었다는 뜻이다.
            // authentication 객체가 session 영역에 저장을 해야하는데, 저장을 위해 return 해준다. 이러면 session에 저장이된다.
            //return 의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는 것이다.
            //굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없다. 근데 단지 권한 처리 때문에 session에 넣어 주는 것이다.
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
        AccountContext accountContext = (AccountContext) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("joshuaToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10))) // 10분
                .withClaim("id", accountContext.getAccount().getId())
                .withClaim("username", accountContext.getAccount().getUsername())
                .sign(Algorithm.HMAC512("joshua"));

        System.out.println("successfulAuthentication 실행됨 : 인증이 완료됨을 의미함");
        response.addHeader("Authorization", "Bearer " + jwtToken);

    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }

}
