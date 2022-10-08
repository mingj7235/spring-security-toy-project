package com.mj.springsecuritytoyproject.security.metadatasource;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /**
     * LinkedHashMap
     *
     * key : RequestMatcher , 즉 URL 정보
     * value : List<ConfigAttribute>>, 즉 권한들의 리스트
     *
     */
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();


    @Override
    public Collection<ConfigAttribute> getAttributes(final Object object) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        // Test 로 Matcher 와 ConfigAttribute 를 줘본다. (후에 DB로 변경)
        requestMap.put(new AntPathRequestMatcher("/mypage"), List.of(new SecurityConfig("ROLE_USER")));

        if (requestMap != null) {
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
                RequestMatcher matcher = entry.getKey(); // URL 정보
                if (matcher.matches(request)) {
                    return entry.getValue(); // URL 정보에 매칭되는 권한 정보 (List)
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();

        for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }

        return allAttributes;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}
