package com.mj.springsecuritytoyproject.security.filter;

import com.mj.springsecuritytoyproject.security.token.JwtTokenProvider;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = getJwtFromRequest(request);

            if (StringUtils.hasText(jwtToken) && JwtTokenProvider.validateToken(jwtToken)) {
                String userId = JwtTokenProvider.getUserIdFromJWT(jwtToken);


            }

        } catch (Exception e) {

        }
    }


    private String getJwtFromRequest (HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }

        return null;
    }

}
