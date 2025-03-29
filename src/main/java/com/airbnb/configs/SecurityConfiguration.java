package com.airbnb.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.airbnb.services.CustomUserDetailsService;
import com.airbnb.services.IUserService;

// @EnableWebSecurity
@Configuration
@EnableMethodSecurity // recommendation
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(IUserService userService) {
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        // DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        CustomDaoAuthenticationProvider provider = new CustomDaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        // config để hiển thị lỗi khi không tìm thấy user mà không dùng message lỗi mặc
        // định Bad Credentials của Spring
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    // @Bean
    // public AuthenticationManager
    // authenticationManager(AuthenticationConfiguration config,
    // List<AuthenticationProvider> providers) throws Exception {
    // return new CustomAuthenticationManager(providers);
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                // .csrf((config) -> config.disable())
                // .cors((config) -> config.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/users/**", "/users").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    // For MVC
    // @Bean
    // SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // http.authorizeHttpRequests((requests) ->
    // requests.anyRequest().authenticated())
    // .sessionManagement(session -> session // sessionManagementConfigurer
    // // Quản lý session
    // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    // // URL chuyển hướng khi session hết hạn hoặc không hợp lệ
    // .invalidSessionUrl("/invalidSession")
    // // Số lượng session tối đa cho mỗi người dùng ConcurrencyControlConfigurer
    // .maximumSessions(1)
    // // URL chuyển hướng khi session hết hạn do vượt quá số lượng tối đa
    // .expiredUrl("/sessionExpired"))
    // .formLogin((form) -> form.loginPage("/login")
    // // Sử dụng redirect, thay đổi URL trên trình duyệt.
    // .failureUrl("/your_link")
    // // Sử dụng khi bạn muốn chuyển hướng người dùng đến một trang mặc định sau
    // khi
    // // đăng nhập thành công.
    // .defaultSuccessUrl("/your_link")
    // // Sử dụng forward, giữ nguyên URL trên trình duyệt, controller nào xử lý URL
    // đó
    // // thì sẽ được gọi.
    // .failureForwardUrl("/your_link")
    // .permitAll());
    // return http.build();
    // }
}
