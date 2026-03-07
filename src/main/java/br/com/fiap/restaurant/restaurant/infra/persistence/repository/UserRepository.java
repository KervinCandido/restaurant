package br.com.fiap.restaurant.restaurant.infra.persistence.repository;

import br.com.fiap.restaurant.restaurant.infra.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {}
