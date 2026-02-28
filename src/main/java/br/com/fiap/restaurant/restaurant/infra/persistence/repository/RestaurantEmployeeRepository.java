package br.com.fiap.restaurant.restaurant.infra.persistence.repository;

import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEmployeeEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEmployeeEntity.RestaurantEmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantEmployeeRepository extends JpaRepository<RestaurantEmployeeEntity, RestaurantEmployeeId> {
}
