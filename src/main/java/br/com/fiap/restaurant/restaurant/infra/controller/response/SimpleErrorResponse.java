package br.com.fiap.restaurant.restaurant.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa uma mensagem de erro genérica")
public record SimpleErrorResponse(
        @Schema(description = "Mensagem de erro", example = "Restaurante não encontrado.")
        String message
) {}
