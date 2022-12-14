package com.mj.springsecuritytoyproject.security.config;

import com.mj.springsecuritytoyproject.security.common.FormAuthenticationDetailsSource;
import com.mj.springsecuritytoyproject.security.factory.UrlResourcesMapFactoryBean;
import com.mj.springsecuritytoyproject.security.filter.JwtAuthenticationFilter;
import com.mj.springsecuritytoyproject.security.filter.JwtAuthorizationFilter;
import com.mj.springsecuritytoyproject.security.filter.PermitAllFilter;
import com.mj.springsecuritytoyproject.security.handler.AjaxAuthenticationFailureHandler;
import com.mj.springsecuritytoyproject.security.handler.AjaxAuthenticationSuccessHandler;
import com.mj.springsecuritytoyproject.security.handler.FormAccessDeniedHandler;
import com.mj.springsecuritytoyproject.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.mj.springsecuritytoyproject.security.provider.AjaxAuthenticationProvider;
import com.mj.springsecuritytoyproject.security.provider.FormAuthenticationProvider;
import com.mj.springsecuritytoyproject.security.voter.IpAddressVoter;
import com.mj.springsecuritytoyproject.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final FormAuthenticationDetailsSource formWebAuthenticationDetailsSource;

    private final AuthenticationSuccessHandler formAuthenticationSuccessHandler;

    private final AuthenticationFailureHandler formAuthenticationFailureHandler;

    private final SecurityResourceService securityResourceService;

    private String [] permitAllResources = {"/", "/login", "/user/login/**"};

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
        auth.authenticationProvider(ajaxAuthenticationProvider());
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     *
     * 스프링 시큐리티 내에서 기본적으로 생성한 필터에 대해서는 정적 무시가 정상적으로 동작 했지만
     * 우리가 새롭게 생성한 필터는 여전히 서블릿 필터에서도 호출이 되는 필터이기 때문에 정적 파일을 무시하지 못하고 가로채어 버리기 때문에
     * 아래와 같은 별도의 설정이 필요함
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(customFilterSecurityInterceptor());
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new FormAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        FormAccessDeniedHandler accessDeniedHandler = new FormAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }

    /**
     * FilterSecurityInterceptor 를 커스텀 하여 Bean 등록
     *  - FilterSecurityInterceptor 는 권한을 체크하는 마지막에 위치하는 Filter
     *  - 인가처리를 내가 만든 UrlFilterInvocationSecurityMetadataSource 로 하도록 설정하는 것임.
     *  - 이 필터를 커스텀하기 위해서는 3가지를 set 해줘야한다.
     *      - setSecurityMetadataSource
     *      - setAccessDecisionManager 결정 매니저
     *      - setAuthenticationManager 인증 매니저
     */
    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {

        /**
         * PermitAllFilter 가 FilterSecurityInterceptor 를 구현했으므로,
         * 커스텀한 Filter를 등록해주는 것임.
         */
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());
        return permitAllFilter;
    }

    private AccessDecisionManager affirmativeBased() { // 3가지 DecisionManager 중에 가장 무난한 녀석
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(new IpAddressVoter(securityResourceService)); // Voter 를 심의할 때 IP 검사하는 Voter 가 가장 먼저 오도록 !
        accessDecisionVoters.add(roleVoter());

        return accessDecisionVoters;
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new RoleHierarchyImpl();
    }

    /**
     * DB 에서 자원과 권한 정보를 가져오기 위해 Bean 을 등록하고,
     * 그 Bean 을 커스텀한 FilterInvocationSecurityMetadataSource 에 등록한다.
     */
    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {

        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .anyRequest().authenticated()

        .and()
//                .formLogin()
//                .disable().headers().frameOptions().disable()

//                .loginPage("/login")
//                .loginProcessingUrl("/login_proc") // view 페이지의 post form 태그의 url -> Form 방식의 로그인을 SpringSecurity 에게 맡기는 것
//                .authenticationDetailsSource(formWebAuthenticationDetailsSource) // 인증시 ID, PW 제외하고 별개의 detail 정보를 담기 위해서!
//                .successHandler(formAuthenticationSuccessHandler)
//                .failureHandler(formAuthenticationFailureHandler)
//                .permitAll() // 인증 받지 않은 사용자도 접근하도록

//        .and()
                .exceptionHandling()
//                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .accessDeniedPage("/denied")
                .accessDeniedHandler(accessDeniedHandler())
        .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // 등록한 인가 처리 필터의 위치를 지정한다.
        .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManagerBean()))
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManagerBean(), userDetailsService()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        ;
        http.csrf().disable();

//        customConfigurer(http);
    }

    private void customConfigurer (HttpSecurity http) throws Exception {
        http
                .apply(new AjaxLoginConfigurer<>())
                .successHandlerAjax(ajaxAuthenticationSuccessHandler())
                .failureHandlerAjax(ajaxAuthenticationFailureHandler())
                .loginProcessingUrl("/api/login")
                .setAuthenticationManager(authenticationManagerBean());
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider(){
        return new AjaxAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler(){
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler(){
        return new AjaxAuthenticationFailureHandler();
    }


}
