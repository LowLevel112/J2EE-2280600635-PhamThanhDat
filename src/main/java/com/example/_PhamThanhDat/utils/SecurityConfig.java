package com.example._PhamThanhDat.utils;

import com.example._PhamThanhDat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.validation.constraints.NotNull;
import com.example._PhamThanhDat.services.OAuthService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // 1. Tự động Inject UserService
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final OAuthService oAuthService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                // THÊM DÒNG NÀY: Tạm thời tắt CSRF cho API để JavaScript gọi được
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/", "/register", "/error").permitAll()
                        .requestMatchers("/2fa/verify").permitAll()
                        .requestMatchers("/api/**")
                        .hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/books/edit/**", "/books/add", "/books/delete/**").hasRole("ADMIN")
                        .requestMatchers("/books/**", "/cart/**", "/2fa/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error=true")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                        .successHandler((request, response, authentication) -> {
                            Object principal = authentication.getPrincipal();
                            String email = "";
                            if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
                                email = oidcUser.getEmail();
                            } else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
                                email = oauth2User.getAttribute("email");
                            }
                            userService.saveOauthUser(email, email);
                            response.sendRedirect("/");
                        }))
                .exceptionHandling(ex -> ex.accessDeniedPage("/403")) // Chỉ giữ 1 đoạn này ở đây
                .rememberMe(rm -> rm
                        .key("hutech")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userDetailsService))
                .build();
    }
}