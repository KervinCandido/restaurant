package br.com.fiap.restaurant.restaurant.infra.service;

import br.com.fiap.restaurant.restaurant.core.exception.InvalidCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtDecoder jwtDecoder;

    public JwtService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public boolean isValidToken(String token) {
        try {
            Optional<Jwt> jwt = Optional.ofNullable(jwtDecoder.decode(token));
            var invalidTokenException = new InvalidCredentialsException("The token is invalid.");

            var expiresAt = Optional.ofNullable(jwt.orElseThrow(() -> invalidTokenException).getExpiresAt());
            if (expiresAt.orElseThrow(()-> invalidTokenException).isBefore(Instant.now())) {
                throw new InvalidCredentialsException("The token has expired.");
            }
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public Collection<GrantedAuthority> getAuthorities(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return Arrays.stream(jwt.getClaimAsString("roles").split("\\s")).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}
