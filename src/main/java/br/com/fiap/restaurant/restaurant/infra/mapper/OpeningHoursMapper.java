package br.com.fiap.restaurant.restaurant.infra.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.model.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.OpeningHoursEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpeningHoursMapper {

    @Mapping(target = "restaurant", ignore = true)
    OpeningHoursEntity toEntity(OpeningHours domain);

    OpeningHours toDomain(OpeningHoursEntity entity);
}
