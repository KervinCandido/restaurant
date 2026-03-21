package br.com.fiap.restaurant.restaurant.infra.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados do endereço do restaurante")
public class AddressRequest {

    @Schema(description = "Nome da rua", example = "Avenida Lins de Vasconcelos")
    @NotBlank
    @Size(min = 1, max = 255)
    private String street;

    @Schema(description = "Número do endereço", example = "1222")
    @NotBlank
    private String number;

    @Schema(description = "Cidade", example = "São Paulo")
    @NotBlank
    private String city;

    @Schema(description = "Estado", example = "SP")
    @NotBlank
    private String state;

    @Schema(description = "Código Postal (CEP)", example = "01538-001")
    @NotBlank
    private String zipCode;

    @Schema(description = "Complemento do endereço", example = "Torre A, Apto 101")
    private String complement;
}
