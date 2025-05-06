package com.airbnb.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.airbnb.dto.req.LoginDTO;
import com.airbnb.dto.res.AccountInfoDTO;
import com.airbnb.dto.res.ResponseDTO;
import com.airbnb.entities.User;
import com.airbnb.exception.EntityNotFoundException;
import com.airbnb.services.IUserService;
import com.airbnb.services.JwtService;
import com.airbnb.utils.SecurityUtils;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final JwtService jwtService;
        private final IUserService userService;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, JwtService jwtService,
                        IUserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.jwtService = jwtService;
                this.userService = userService;
        }

        @PostMapping("/login")
        public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getEmail(), loginDTO.getPassword());
                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String accessToken = jwtService.createToken(authentication, JwtService.ACCESS_TOKEN);
                String refreshToken = jwtService.createToken(authentication, JwtService.REFRESH_TOKEN);
                // Map<String, String> tokens = Map.of(JwtService.ACCESS_TOKEN, accessToken,
                // JwtService.REFRESH_TOKEN, refreshToken);
                Map<String, String> tokens = new HashMap<>();
                tokens.put(JwtService.ACCESS_TOKEN, accessToken);
                // không nên trả refreshToken vào body vì dễ bị tấn công XSS
                // tokens.put(JwtService.REFRESH_TOKEN, refreshToken);
                ResponseDTO response = ResponseDTO.builder()
                                .message("Login successful")
                                .data(tokens)
                                .build();

                ResponseCookie cookie = createRefreshTokenCookie(refreshToken);
                // TODO: set token to redis
                // HttpHeaders httpHeaders = new HttpHeaders();
                // httpHeaders.setBearerAuth(accessToken);

                // return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);

        }

        // @PostMapping("/login")
        // public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO,
        // BindingResult validationResult) {
        // // Lấy kết quả validate từ BindingResult
        // // Nếu có lỗi validate thì trả về lỗi
        // if (validationResult.hasErrors()) {
        // return
        // ResponseEntity.badRequest().body(validationResult.getFieldErrors().stream()
        // .map(fieldError -> fieldError.getField() + ": " +
        // fieldError.getDefaultMessage())
        // .collect(Collectors.toList()));
        // }

        // // TODO: Process login
        // return ResponseEntity.ok().body("ok");

        // }

        @GetMapping("/account-info")
        public ResponseEntity<ResponseDTO> getAccountInfo() {
                Optional<String> email = SecurityUtils.getCurrentUserLogin();
                if (email.isEmpty()) {
                        throw new BadCredentialsException("Thông tin không hợp lệ");
                }

                User user = userService.getUserByEmail(email.get())
                                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

                AccountInfoDTO accountInfoDTO = AccountInfoDTO.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .build();
                ResponseDTO response = ResponseDTO.builder()
                                .message("Get account info success")
                                .data(accountInfoDTO)
                                .build();
                return ResponseEntity.ok().body(response);
        }

        @GetMapping("/refresh")
        public ResponseEntity<ResponseDTO> refreshAuth(
                        @CookieValue(value = JwtService.REFRESH_TOKEN) String refreshToken) {
                // TODO: check refreshToken in redis
                Jwt decodedRefreshToken = jwtService.getJwtFromRefreshToken(refreshToken);
                String subject = decodedRefreshToken.getSubject();

                String accessToken = jwtService.createToken(subject, JwtService.ACCESS_TOKEN);
                String newRefreshToken = jwtService.createToken(subject, JwtService.REFRESH_TOKEN);

                // TODO: reset newRefreshToken to redis
                // TODO: set blanklists refreshToken
                // Không nên trả refreshToken vào body vì dễ bị tấn công XSS
                ResponseCookie cookie = createRefreshTokenCookie(newRefreshToken);
                Map<String, String> tokens = Map.of(JwtService.ACCESS_TOKEN, accessToken);
                ResponseDTO response = ResponseDTO.builder()
                                .message("Refresh token successful")
                                .data(tokens)
                                .build();
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
        }

        private ResponseCookie createRefreshTokenCookie(String refreshToken) {
                return ResponseCookie.from(JwtService.REFRESH_TOKEN, refreshToken)
                                .httpOnly(true)
                                .secure(true) // cookie chỉ được sử dụng với https, còn http thì không,
                                // nên là nếu ở local thì true hay false thì cũng giống nhau
                                .path("/")
                                .sameSite("None") // ??
                                .maxAge(60 * 30) // 30 minutes
                                .build();
        }

        // TODO: logout
}
