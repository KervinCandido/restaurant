package br.com.fiap.restaurant.restaurant.infra.auth;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoggedUserGatewayAdapter implements LoggedUserGateway {

    @Override
    public boolean hasRole(String roleName) {
        if (roleName == null) return false;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleName::equals);
    }

    @Override
    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();

        if (auth.getPrincipal() instanceof User u) {
            return Optional.of(u);
        }
        return Optional.empty();
    }
}
