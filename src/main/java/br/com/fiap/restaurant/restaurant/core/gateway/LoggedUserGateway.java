package br.com.fiap.restaurant.restaurant.core.gateway;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotAuthenticatedException;

import java.util.Optional;

public interface LoggedUserGateway {

    boolean hasRole(String roleName);

    Optional<User> getCurrentUser();

    default User requireCurrentUser() throws UserNotAuthenticatedException {
        return getCurrentUser().orElseThrow(UserNotAuthenticatedException::new);
    }
}
