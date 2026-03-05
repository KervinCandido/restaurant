package br.com.fiap.restaurant.restaurant.core.gateway;

import br.com.fiap.restaurant.restaurant.core.domain.User;

import java.util.*;

public interface UserGateway {
    Optional<User> findById(UUID uuid);
    List<User> findAllById(Collection<UUID> uuids);
}
