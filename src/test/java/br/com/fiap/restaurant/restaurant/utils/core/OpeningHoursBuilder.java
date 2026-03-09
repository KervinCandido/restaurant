package br.com.fiap.restaurant.restaurant.utils.core;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.inbound.OpeningHoursInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateOpeningHoursInput;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class OpeningHoursBuilder {

    public static OpeningHoursBuilder builder() {
        return new OpeningHoursBuilder();
    }

    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime openHour;
    private LocalTime closeHour;

    private OpeningHoursBuilder() {
        this.dayOfWeek = DayOfWeek.MONDAY;
        this.openHour = LocalTime.of(17, 0);
        this.closeHour = LocalTime.of(23, 0);
    }

    public OpeningHoursBuilder clean() {
        this.dayOfWeek = DayOfWeek.MONDAY;
        this.openHour = LocalTime.of(17, 0);
        this.closeHour = LocalTime.of(23, 0);
        return this;
    }

    public OpeningHoursBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OpeningHoursBuilder withoutId() {
        this.id = null;
        return this;
    }

    public OpeningHoursBuilder withDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public OpeningHoursBuilder withOpenHour(LocalTime openHour) {
        this.openHour = openHour;
        return this;
    }

    public OpeningHoursBuilder withCloseHour(LocalTime closeHour) {
        this.closeHour = closeHour;
        return this;
    }

    public OpeningHours build() {
        return new OpeningHours(id, dayOfWeek, openHour, closeHour);
    }

    public OpeningHoursInput buildInput() {
        return new OpeningHoursInput(dayOfWeek, openHour, closeHour);
    }

    public UpdateOpeningHoursInput buildUpdateInput() {
        return new UpdateOpeningHoursInput(id, dayOfWeek, openHour, closeHour);
    }
}
