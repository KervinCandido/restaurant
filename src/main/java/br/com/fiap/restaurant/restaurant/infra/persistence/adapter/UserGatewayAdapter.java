package br.com.fiap.restaurant.restaurant.infra.persistence.adapter;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.infra.persistence.mapper.UserMapper;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserGatewayAdapter implements UserGateway {

    private final UserRepository userRepository;

    public UserGatewayAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findById(UUID uuid) {
        return userRepository.findById(uuid).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAllById(Collection<UUID> uuids) {
        return userRepository.findAllById(uuids).stream().map(UserMapper::toDomain).toList();
    }
}
