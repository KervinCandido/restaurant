package br.com.fiap.restaurant.restaurant.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do endereço de um restaurante")
public record AddressResponse (
        @Schema(description = "Nome da rua", example = "Avenida Lins de Vasconcelos")
        String street,
        @Schema(description = "Número do endereço", example = "1222")
        String number,
        @Schema(description = "Cidade", example = "São Paulo")
        String city,
        @Schema(description = "Estado", example = "SP")
        String state,
        @Schema(description = "Código Postal (CEP)", example = "01538-001")
        String zipCode,
        @Schema(description = "Complemento do endereço", example = "Torre A, Apto 101")
        String complement
) {}
