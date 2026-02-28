package br.com.fiap.restaurant.restaurant.infra.auth;


import br.com.fiap.restaurant.restaurant.core.domain.model.User;

public class FakeLoggedUserContext {
    private final User user;

    public FakeLoggedUserContext(User user) {
        this.user = user;
    }

    public User get() { return user; }
}
