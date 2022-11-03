package com.papatriz.jsfdemo.config;

import com.papatriz.jsfdemo.security.LoginSuccessHandler;
import com.papatriz.jsfdemo.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

                httpSecurity
                .authorizeRequests()
                    .mvcMatchers("/manager", "/manager/**").hasAnyRole("MANAGER", "ADMIN")
                    .mvcMatchers("/driver").hasAnyRole("DRIVER", "ADMIN")
                    .mvcMatchers("/userlist").hasRole("ADMIN")
                    .antMatchers("/**").authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/error/accessDenied")
                .and()
                .formLogin()
                    .loginPage("/login.xhtml")
                    .successHandler(loginSuccessHandler)
                        .failureUrl("/login.xhtml?error=true")
                    .permitAll()
                .and()
                .logout()
                    .logoutUrl("/logout");

                httpSecurity.csrf().disable();

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoderNone()
    {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public PasswordEncoder passwordEncoderBCrypt() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoderBCrypt());
        provider.setUserDetailsService(userDetailsService);
        System.out.println("authProvider created");
        return provider;
    }

}
