package br.com.fiap.restaurant.restaurant.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa um erro de validação em um campo específico")
public record FieldErrorResponse(
        @Schema(description = "Nome do campo que causou o erro", example = "name")
        String field,
        @Schema(description = "Mensagem de erro detalhando o problema", example = "O nome deve ter entre 2 e 100 caracteres")
        String message
) {}
