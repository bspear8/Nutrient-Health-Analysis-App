package edu.gatech.ihi.nhaa.security;

import edu.gatech.ihi.nhaa.security.jwt.JWTConfigurer;
import edu.gatech.ihi.nhaa.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    public SecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .cors()
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                // .antMatchers(getSwaggerPaths()).permitAll()
                .antMatchers("/auth/*").permitAll()
                .anyRequest().authenticated()
                .and()
            .apply(new JWTConfigurer(this.tokenProvider));
    }

    private String[] getSwaggerPaths() {
        return new String[] {
                "/swagger-ui.html",
                "/v2/**",
                "/swagger-resources/**",
                "/documentation/**"
        };
    }
}