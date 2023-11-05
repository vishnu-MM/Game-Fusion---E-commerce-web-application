package com.example.gamefusion.Configuration.Security;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests( authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/","/user-registration/verify","/otp-validation","/otp-validation/verify",
                            "/sent-otp","/assets/**").permitAll()
                    .requestMatchers("/dashboard/**").hasAuthority("ADMIN")
                    .anyRequest().authenticated()
            )
            .formLogin( login ->
                login
                    .loginPage("/login-or-registration")
                    .loginProcessingUrl("/UserAuthentication")
                    .defaultSuccessUrl("/")
                    .permitAll()
            )
            .exceptionHandling( handler ->
                handler
                    .accessDeniedPage("/error-page")
            )
            .logout( logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
