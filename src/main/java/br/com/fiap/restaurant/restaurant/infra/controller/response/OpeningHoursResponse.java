package br.com.fiap.restaurant.restaurant.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Schema(description = "Dados de resposta do horário de funcionamento para um dia da semana")
public record OpeningHoursResponse (
    @Schema(description = "Dia da semana", example = "MONDAY")
    @DateTimeFormat(pattern = "EEEE") DayOfWeek dayOfWeek,
    @Schema(description = "Horário de abertura no formato HH:mm", example = "09:00")
    @DateTimeFormat(pattern = "HH:mm") LocalTime openHour,
    @Schema(description = "Horário de fechamento no formato HH:mm", example = "18:00")
    @DateTimeFormat(pattern = "HH:mm")LocalTime closeHour
){}
