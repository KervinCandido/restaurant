package br.com.fiap.restaurant.restaurant.infra.persistence.repository;

import br.com.fiap.restaurant.restaurant.infra.persistence.entity.OpeningHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpeningHoursRepository extends JpaRepository<OpeningHoursEntity, Long> {
}
