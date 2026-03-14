package br.com.fiap.restaurant.restaurant.infra.persistence.adapter;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.infra.persistence.mapper.UserMapper;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;

import java.util.*;

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
        if (uuids.isEmpty()) return List.of();
        return userRepository.findAllById(uuids).stream().map(UserMapper::toDomain).toList();
    }

    @Override
    public void delete(UUID uuid) {
        Objects.requireNonNull(uuid, "userUuid cannot be null");
        userRepository.deleteById(uuid);
    }

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "user cannot be null");
        var userEntity = UserMapper.toEntity(user);
        userRepository.save(userEntity);
        return UserMapper.toDomain(userEntity);
    }
}
