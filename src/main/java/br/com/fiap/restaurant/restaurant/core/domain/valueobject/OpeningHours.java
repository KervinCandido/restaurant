package br.com.fiap.restaurant.restaurant.core.domain.valueobject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public record OpeningHours(Long id, DayOfWeek dayOfWeek, LocalTime openHour, LocalTime closeHour) {

    public OpeningHours(Long id, DayOfWeek dayOfWeek, LocalTime openHour, LocalTime closeHour) {
        this.id = id;
        this.dayOfWeek = Objects.requireNonNull(dayOfWeek, "dayOfWeek não pode ser nulo.");
        this.openHour = Objects.requireNonNull(openHour, "openHour não pode ser nulo.");
        this.closeHour = Objects.requireNonNull(closeHour, "closeHour não pode ser nulo.");
    }
}
