package br.com.fiap.restaurant.restaurant.infra.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Schema(description = "Dados do horário de funcionamento para um dia da semana")
public class OpeningHoursRequest {
    @Schema(description = "Dia da semana", example = "MONDAY")
    @NotNull
    @DateTimeFormat(pattern = "EEEE")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private final DayOfWeek dayOfWeek;

    @Schema(description = "Horário de abertura no formato HH:mm", example = "09:00")
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private final LocalTime openHour;

    @Schema(description = "Horário de fechamento no formato HH:mm", example = "18:00")
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private final LocalTime closeHour;
}
