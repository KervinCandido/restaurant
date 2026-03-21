package br.com.fiap.restaurant.restaurant.infra.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Dados de um item do cardápio")
public class MenuItemRequest {
    @Schema(description = "Nome do item do cardápio", example = "Prato Feito")
    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @Schema(description = "Descrição detalhada do item", example = "Arroz, feijão, bife e batata frita")
    @Size(max = 500)
    private String description;

    @Schema(description = "Preço do item", example = "25.50")
    @NotNull
    @Positive
    private BigDecimal price;

    @Schema(description = "Indica se o item é exclusivo para consumo no restaurante", example = "false")
    @NotNull
    private Boolean restaurantOnly;

    @Schema(description = "Caminho ou URL da foto do item", example = "/images/prato-feito.jpg")
    @NotBlank
    @Size(min = 3, max = 255)
    private String photoPath;
}
