package com.airbnb.services;

import static com.airbnb.utils.SecurityUtils.AUTHORITIES_KEY;
import static com.airbnb.utils.SecurityUtils.JWT_ALGORITHM;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    @Value("${vinh.jwt.access-token-validity-in-seconds}")
    private long jwtAccessTokenValidityInSeconds;

    @Value("${vinh.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshTokenValidityInSeconds;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String createToken(Authentication authentication, String tokenType) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        // long validityInSeconds = tokenType.equals(ACCESS_TOKEN)
        // ? this.jwtAccessTokenValidityInSeconds
        // : this.jwtRefreshTokenValidityInSeconds;

        long validityInSeconds = switch (tokenType) {
            case ACCESS_TOKEN -> this.jwtAccessTokenValidityInSeconds;
            case REFRESH_TOKEN -> this.jwtRefreshTokenValidityInSeconds;
            default -> throw new IllegalArgumentException(
                    "Unknown token type: " + tokenType);
        };

        Instant validity = now.plus(validityInSeconds, ChronoUnit.SECONDS);

        // claim là các thông tin muốn lưu trữ trong token theo định dạng key-value
        // Ví dụ: các claim có thể là: iss, sub, iat, exp, authorities, userId,
        // department,...
        // {
        // "iss": "your-auth-server",
        // "sub": "john.doe",
        // "iat": 1682000000,
        // "exp": 1682003600,
        // "authorities": ["ROLE_USER", "VIEW_PROJECTS"],
        // "userId": 123,
        // "department": "Engineering",
        // "permissions": {
        // "projects": ["create", "read", "update"],
        // "tasks": ["create", "read", "delete"],
        // "reports": ["read"]
        // }
        // }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                // TODO: add more claims
                // .claim(USER_ID_CLAIM, userId)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createToken(String subject, List<String> permissions, String tokenType) {

        String authorities = permissions.stream()
                .collect(Collectors.joining(" "));
        Instant now = Instant.now();
        long validityInSeconds = tokenType.equals(ACCESS_TOKEN)
                ? this.jwtAccessTokenValidityInSeconds
                : this.jwtRefreshTokenValidityInSeconds;

        Instant validity = now.plus(validityInSeconds, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(subject)
                .claim(AUTHORITIES_KEY, authorities)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createToken(String subject, String tokenType) {

        Instant now = Instant.now();
        long validityInSeconds = tokenType.equals(ACCESS_TOKEN)
                ? this.jwtAccessTokenValidityInSeconds
                : this.jwtRefreshTokenValidityInSeconds;

        Instant validity = now.plus(validityInSeconds, ChronoUnit.SECONDS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(subject)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public Jwt getJwtFromRefreshToken(String refreshToken) {
        try {
            return jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            System.out.println(">>> JWT error " + e.getMessage());
            throw e;
        }
    }
}
