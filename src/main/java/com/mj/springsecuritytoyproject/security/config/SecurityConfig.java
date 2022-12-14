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
     * ????????? ???????????? ????????? ??????????????? ????????? ????????? ???????????? ?????? ????????? ??????????????? ?????? ?????????
     * ????????? ????????? ????????? ????????? ????????? ????????? ??????????????? ????????? ?????? ???????????? ????????? ?????? ????????? ???????????? ????????? ???????????? ????????? ?????????
     * ????????? ?????? ????????? ????????? ?????????
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
     * FilterSecurityInterceptor ??? ????????? ?????? Bean ??????
     *  - FilterSecurityInterceptor ??? ????????? ???????????? ???????????? ???????????? Filter
     *  - ??????????????? ?????? ?????? UrlFilterInvocationSecurityMetadataSource ??? ????????? ???????????? ??????.
     *  - ??? ????????? ??????????????? ???????????? 3????????? set ???????????????.
     *      - setSecurityMetadataSource
     *      - setAccessDecisionManager ?????? ?????????
     *      - setAuthenticationManager ?????? ?????????
     */
    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {

        /**
         * PermitAllFilter ??? FilterSecurityInterceptor ??? ??????????????????,
         * ???????????? Filter??? ??????????????? ??????.
         */
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());
        return permitAllFilter;
    }

    private AccessDecisionManager affirmativeBased() { // 3?????? DecisionManager ?????? ?????? ????????? ??????
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(new IpAddressVoter(securityResourceService)); // Voter ??? ????????? ??? IP ???????????? Voter ??? ?????? ?????? ????????? !
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
     * DB ?????? ????????? ?????? ????????? ???????????? ?????? Bean ??? ????????????,
     * ??? Bean ??? ???????????? FilterInvocationSecurityMetadataSource ??? ????????????.
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
//                .loginProcessingUrl("/login_proc") // view ???????????? post form ????????? url -> Form ????????? ???????????? SpringSecurity ?????? ????????? ???
//                .authenticationDetailsSource(formWebAuthenticationDetailsSource) // ????????? ID, PW ???????????? ????????? detail ????????? ?????? ?????????!
//                .successHandler(formAuthenticationSuccessHandler)
//                .failureHandler(formAuthenticationFailureHandler)
//                .permitAll() // ?????? ?????? ?????? ???????????? ???????????????

//        .and()
                .exceptionHandling()
//                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .accessDeniedPage("/denied")
                .accessDeniedHandler(accessDeniedHandler())
        .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // ????????? ?????? ?????? ????????? ????????? ????????????.
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
