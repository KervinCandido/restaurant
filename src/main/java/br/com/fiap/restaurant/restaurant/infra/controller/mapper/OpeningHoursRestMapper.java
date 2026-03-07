package br.com.fiap.restaurant.restaurant.infra.controller.mapper;

import br.com.fiap.restaurant.restaurant.core.inbound.OpeningHoursInput;
import br.com.fiap.restaurant.restaurant.infra.controller.resquest.OpeningHoursRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OpeningHoursRestMapper {
    OpeningHoursInput toInput(OpeningHoursRequest request);
}
