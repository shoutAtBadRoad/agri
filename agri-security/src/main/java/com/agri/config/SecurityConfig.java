package com.agri.config;


import com.agri.filter.JwtAuthenticationTokenFilter;
import com.agri.handler.AccessDeniedHandlerImpl;
import com.agri.handler.AuthenticationEntryPointImpl;
import com.agri.security.login.PhonePassDetailsServiceImpl;
import com.agri.security.login.VerifyCodeUserDetailsServiceImpl;
import com.agri.security.login.UserDetailsServiceImpl;
import com.agri.utils.passencoder.DirectPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtAuthenticationTokenFilter filter;

    @Resource
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Resource
    private AccessDeniedHandlerImpl accessDeniedHandler;

    @Resource
    private AuthenticationSuccessHandler authenticationSuccessHandler;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/user/login", "/user/sms/login", "/user/phone/login").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/*").permitAll()
                .antMatchers("/csrf").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/auth/apiAuth").permitAll()
                .antMatchers("/sysUser/aes").permitAll()
                .antMatchers("/sms/**").permitAll()
                .anyRequest().authenticated();

        //???????????????
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        //?????????????????????
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint) // ?????????????????????
                .accessDeniedHandler(accessDeniedHandler); // ?????????????????????

        //????????????
        http.cors();

        //?????????????????????
        http.formLogin().successHandler(authenticationSuccessHandler);
    }

    @Resource
    UserDetailsServiceImpl userDetailsService;

    @Resource
    VerifyCodeUserDetailsServiceImpl smsUserDetailsService;

    @Resource
    PhonePassDetailsServiceImpl phonePassDetailsService;

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * ????????????Manager???manager??????????????????Provider
     * Provider?????????????????????????????????
     * @return ???????????????
     * @throws Exception
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // ????????????Provider
        // ?????????????????????
        DaoAuthenticationProvider provider1 = new DaoAuthenticationProvider();
        provider1.setUserDetailsService(userDetailsService);
        provider1.setPasswordEncoder(bCryptPasswordEncoder);

        // ???????????????
        DaoAuthenticationProvider provider2 = new DaoAuthenticationProvider();
        provider2.setUserDetailsService(smsUserDetailsService);
        provider2.setPasswordEncoder(new DirectPasswordEncoder());

        // ?????????????????????
        DaoAuthenticationProvider provider3 = new DaoAuthenticationProvider();
        provider3.setUserDetailsService(phonePassDetailsService);
        provider3.setPasswordEncoder(bCryptPasswordEncoder);

        return new ProviderManager(provider1, provider2, provider3);
    }

}
