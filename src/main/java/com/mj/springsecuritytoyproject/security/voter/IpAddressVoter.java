package com.mj.springsecuritytoyproject.security.voter;

import com.mj.springsecuritytoyproject.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class IpAddressVoter implements AccessDecisionVoter<Object> {

    private final SecurityResourceService securityResourceService;

    @Override
    public boolean supports(final ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return true;
    }

    /**
     *
     * @param authentication : 사용자의 권한 정보
     * @param object : 요청 정보
     * @param attributes : 매칭된 권한 정보
     * @return
     */
    @Override
    public int vote(final Authentication authentication, final Object object, final Collection<ConfigAttribute> attributes) {

        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        String remoteAddress = details.getRemoteAddress(); // 접속한 사용자의 IP 정보

        List<String> accessIpList = securityResourceService.getAccessIpList();

        int result = ACCESS_DENIED;

        for (String ipAddress : accessIpList) {
            if (remoteAddress.equals(ipAddress)) {
                return ACCESS_ABSTAIN; // 주의 ! granted 가 아니다. 이 심의를 통과한 후 다음 심의를 해야한다.
            }
        }

        if (result == ACCESS_DENIED) {
            throw new AccessDeniedException("Invalid IpAddress");
        }

        return result;
    }

}
