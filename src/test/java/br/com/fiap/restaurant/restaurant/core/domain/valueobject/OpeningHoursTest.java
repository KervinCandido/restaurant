package br.com.fiap.restaurant.restaurant.core.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para OpeningHours")
class OpeningHoursTest {

    @DisplayName("Deve criar opening hours com sucesso")
    @Test
    void deveCriarOpeningHoursComSucesso() {
        Long id = 1L;
        DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;
        LocalTime openHour = LocalTime.of(8, 0);
        LocalTime closeHour = LocalTime.of(18, 0);
        OpeningHours openingHours = new OpeningHours(id, dayOfWeek, openHour, closeHour);

        assertThat(openingHours).isNotNull();
        assertThat(openingHours.id()).isNotNull().isEqualTo(id);
        assertThat(openingHours.dayOfWeek()).isNotNull().isEqualTo(dayOfWeek);
        assertThat(openingHours.openHour()).isNotNull().isEqualTo(openHour);
        assertThat(openingHours.closeHour()).isNotNull().isEqualTo(closeHour);
    }

    @DisplayName("Deve lançar nullpointer se day of week for nulo")
    @Test
    void deveLancarNullPointerSeDayOfWeekForNulo() {
        Long id = 1L;
        DayOfWeek dayOfWeek = null;
        LocalTime openHour = LocalTime.of(8, 0);
        LocalTime closeHour = LocalTime.of(18, 0);
        assertThatThrownBy(() -> new OpeningHours(id, dayOfWeek, openHour, closeHour))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("dayOfWeek não pode ser nulo");
    }

    @DisplayName("Deve lançar nullpointer se openHour for nulo")
    @Test
    void deveLancarNullPointerSeOpenHourForNulo() {
        Long id = 1L;
        DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;
        LocalTime openHour = null;
        LocalTime closeHour = LocalTime.of(18, 0);
        assertThatThrownBy(() -> new OpeningHours(id, dayOfWeek, openHour, closeHour))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("openHour não pode ser nulo");
    }

    @DisplayName("Deve lançar nullpointer se closeHour for nulo")
    @Test
    void deveLancarNullPointerSeCloseHourForNulo() {
        Long id = 1L;
        DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;
        LocalTime openHour = LocalTime.of(8, 0);
        LocalTime closeHour = null;
        assertThatThrownBy(() -> new OpeningHours(id, dayOfWeek, openHour, closeHour))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("closeHour não pode ser nulo");
    }
}