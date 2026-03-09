package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.OpeningHoursEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;

public class OpeningHoursMapper {

    private OpeningHoursMapper() {}
    
    public static OpeningHoursEntity toEntity(OpeningHours domain, RestaurantEntity restaurantEntity) {
        if (domain == null && restaurantEntity == null) return null;

        OpeningHoursEntity openingHoursEntity = new OpeningHoursEntity();
        if (domain != null) {
            openingHoursEntity.setId(domain.id());
            openingHoursEntity.setDayOfWeek(domain.dayOfWeek());
            openingHoursEntity.setOpenHour(domain.openHour());
            openingHoursEntity.setCloseHour(domain.closeHour());
        }
        if (restaurantEntity != null) {
            openingHoursEntity.setRestaurant(restaurantEntity);
        }
        return openingHoursEntity;
    }

    public static OpeningHours toDomain(OpeningHoursEntity entity) {
        if (entity == null) return null;

        return new OpeningHours (
            entity.getId(),
            entity.getDayOfWeek(),
            entity.getOpenHour(),
            entity.getCloseHour()
        );
    }
}
