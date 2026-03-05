package br.com.fiap.restaurant.restaurant.core.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {

    public static final String RESTAURANT_OWNER = "RESTAURANT_OWNER";

    private final UUID uuid;
    private final Set<String> roles;

    /**
     *
     * @param uuid user uuid do usuário
     * @param roles user roles do usuário
     * @throws NullPointerException se uuid ou roles for nulo
     */
    public User(UUID uuid, Set<String> roles) throws NullPointerException {
        Objects.requireNonNull(uuid, "uuid não pode ser nulo");
        Objects.requireNonNull(roles, "roles não pode ser nulo");
        this.uuid = uuid;
        this.roles = new HashSet<>(roles);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<String> getRoles() {
        return Set.copyOf(roles);
    }

    public boolean canOwnRestaurant() {
        return roles.contains(RESTAURANT_OWNER);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof User user)) return false;

        return uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
