package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.OpeningHoursEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class OpeningHoursMapperTest {

    @Test
    void shouldReturnNullWhenOpeningHoursDomainAndRestaurantEntityAreNull() {
        OpeningHoursEntity openingHoursEntity = OpeningHoursMapper.toEntity(null, null);
        assertThat(openingHoursEntity).isNull();
    }

    @Test
    void shouldConvertOpeningHoursDomainToOpeningHoursEntity() {
        OpeningHours openingHours = new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0));
        RestaurantEntity restaurantEntity = new RestaurantEntity();

        OpeningHoursEntity openingHoursEntity = OpeningHoursMapper.toEntity(openingHours, restaurantEntity);

        assertThat(openingHoursEntity).isNotNull();
        assertThat(openingHoursEntity.getId()).isEqualTo(openingHours.id());
        assertThat(openingHoursEntity.getDayOfWeek()).isEqualTo(openingHours.dayOfWeek());
        assertThat(openingHoursEntity.getOpenHour()).isEqualTo(openingHours.openHour());
        assertThat(openingHoursEntity.getCloseHour()).isEqualTo(openingHours.closeHour());
        assertThat(openingHoursEntity.getRestaurant()).isEqualTo(restaurantEntity);
    }

    @Test
    void shouldReturnNullWhenOpeningHoursEntityIsNull() {
        OpeningHours openingHours = OpeningHoursMapper.toDomain(null);
        assertThat(openingHours).isNull();
    }

    @Test
    void shouldConvertOpeningHoursEntityToOpeningHoursDomain() {
        OpeningHoursEntity openingHoursEntity = new OpeningHoursEntity();
        openingHoursEntity.setId(1L);
        openingHoursEntity.setDayOfWeek(DayOfWeek.MONDAY);
        openingHoursEntity.setOpenHour(LocalTime.of(8, 0));
        openingHoursEntity.setCloseHour(LocalTime.of(18, 0));

        OpeningHours openingHours = OpeningHoursMapper.toDomain(openingHoursEntity);

        assertThat(openingHours).isNotNull();
        assertThat(openingHours.id()).isEqualTo(openingHoursEntity.getId());
        assertThat(openingHours.dayOfWeek()).isEqualTo(openingHoursEntity.getDayOfWeek());
        assertThat(openingHours.openHour()).isEqualTo(openingHoursEntity.getOpenHour());
        assertThat(openingHours.closeHour()).isEqualTo(openingHoursEntity.getCloseHour());
    }
}
