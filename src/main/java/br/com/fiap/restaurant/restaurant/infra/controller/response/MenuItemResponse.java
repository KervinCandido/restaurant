package br.com.fiap.restaurant.restaurant.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Dados de resposta de um item do cardápio")
public record MenuItemResponse (
    @Schema(description = "ID único do item do cardápio", example = "1")
    Long id,
    @Schema(description = "Nome do item", example = "Prato Feito")
    String name,
    @Schema(description = "Descrição detalhada do item", example = "Arroz, feijão, bife e batata frita")
    String description,
    @Schema(description = "Preço do item", example = "25.50")
    BigDecimal price,
    @Schema(description = "Indica se o item é exclusivo para consumo no restaurante", example = "false")
    Boolean restaurantOnly,
    @Schema(description = "Caminho ou URL da foto do item", example = "/images/prato-feito.jpg")
    String photoPath
){}
