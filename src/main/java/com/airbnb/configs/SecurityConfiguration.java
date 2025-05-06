package com.airbnb.configs;

import static com.airbnb.utils.SecurityUtils.AUTHORITIES_KEY;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import com.airbnb.configs.security.CustomAuthenticationEntryPoint;

// @EnableWebSecurity
@Configuration
@EnableMethodSecurity // recommendation
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // https://stackoverflow.com/a/53758500
    // Sử dụng @Component("userDetailsService") để lấy bean
    // @Bean
    // public UserDetailsService userDetailsService(IUserService userService) {
    // return new CustomUserDetailsService(userService);
    // }

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

    // Sau khi login thành công -> lấy token từ header -> giải mã token
    // -> convert token sang Authentication (gồm permissions và các claims khác)
    // -> nạp vào SecurityContext
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            CustomAuthenticationEntryPoint authenticationEntryPoint)
            throws Exception {
        http
                .csrf((config) -> config.disable())
                // .cors((config) -> config.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/users/**", "/users").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                        .anyRequest().authenticated())
                // .anyRequest().permitAll())
                // sau khi thêm config ở dưới, spring sẽ tự động thêm 1 filter
                // BearerTokenAuthenticationFilter
                // vào trước filter UsernamePasswordAuthenticationFilter
                // để xử lý token. Tự động lấy token từ header -> Cần cấu hình decoder
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint)) // setup khi lỗi jwt
                .exceptionHandling(exceptions -> exceptions
                        // .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
                        // nếu như để mặc định body sẽ không có gì cả. chỉ có status là thay đổi nên
                        // phải custom
                        .authenticationEntryPoint(authenticationEntryPoint) // 401
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
                .formLogin(form -> form.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // setup để có thể dùng JWT
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
