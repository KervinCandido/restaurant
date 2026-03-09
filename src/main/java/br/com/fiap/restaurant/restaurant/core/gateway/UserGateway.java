package br.com.fiap.restaurant.restaurant.core.gateway;

import br.com.fiap.restaurant.restaurant.core.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
    Optional<User> findById(UUID uuid);
    List<User> findAllById(Collection<UUID> uuids);
}
