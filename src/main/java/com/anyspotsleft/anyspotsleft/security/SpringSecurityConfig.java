package com.anyspotsleft.anyspotsleft.security;

import com.anyspotsleft.anyspotsleft.user.Role;
import com.anyspotsleft.anyspotsleft.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
spring security config file, required for all auth systems.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SpringSecurityConfig {

    @Autowired
    private final LogoutSuccessPage logoutSuccessPage;

    @Autowired
    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Component
    public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            // Custom logout success logic
            response.sendRedirect("/custom-logout-success");
        }
    }

        @Bean
        public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
            return http

                    .csrf(AbstractHttpConfigurer::disable)

                    .formLogin(httpForm -> {
                        httpForm
                                .loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/")
                                .failureHandler((request, response, exception) -> {
                                    System.out.println("Login failed: " + exception.getMessage());
                                    response.sendRedirect("/login?error");
                                })
                                .permitAll();
                    })
                    .logout((logout) -> {
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessHandler(logoutSuccessPage)
                                .invalidateHttpSession(true);
                    })

                    .authorizeHttpRequests(registry -> {
                        registry.requestMatchers("/signup","/signup/host", "/css/**", "/js/**", "/images/**", "/", "/discover", "/discover/**", "discover", "/discover/").permitAll();

                        // Role-based access
                        registry.requestMatchers("/admin", "/testsave").hasRole(Role.ADMIN.name());
                        registry.requestMatchers("/host").hasRole(Role.HOST.name());
                        registry.anyRequest().authenticated();
                    })

                    .build();
        }
    }
