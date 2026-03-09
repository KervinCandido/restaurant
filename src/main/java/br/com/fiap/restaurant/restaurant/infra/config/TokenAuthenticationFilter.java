package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;
import br.com.fiap.restaurant.restaurant.infra.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TokenAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var optionalToken = this.extractRequestToken(request);
        if (optionalToken.isPresent() && this.isValidToken(optionalToken.get())) {
            authenticateUser(optionalToken.get());
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractRequestToken(HttpServletRequest request) {
        var optionalAuthorization = Optional.ofNullable(request.getHeader("Authorization"));

        if (optionalAuthorization.isPresent()) {
            String authorizationHeader = optionalAuthorization.get();
            String authHeader = authorizationHeader.trim();
            int indexSpace = authHeader.indexOf(' ');
            if (indexSpace > 0) {
                String type = authHeader.substring(0, indexSpace);
                String token = authHeader.substring(indexSpace + 1);
                if (type.equals("Bearer")) {
                    return Optional.of(token);
                }
            }
        }

        return Optional.empty();
    }

    private boolean isValidToken(String token) {
        return token != null &&
                !token.trim().isEmpty() &&
                jwtService.isValidToken(token);
    }

    private void authenticateUser(String token) {
        var userId = UUID.fromString(jwtService.getUserId(token));
        var authorities = jwtService.getAuthorities(token);
        User principal = new User(userId, authorities.parallelStream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
