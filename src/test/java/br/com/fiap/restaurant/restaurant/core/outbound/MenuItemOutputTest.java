package br.com.fiap.restaurant.restaurant.core.outbound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para MenuItemOutput")
class MenuItemOutputTest {

    @DisplayName("Deve criar MenuItemOutput com sucesso")
    @Test
    void deveCriarMenuItemOutputComSucesso() {
        Long id = 1L;
        String name = "Pizza";
        String description = "Delicious pizza";
        BigDecimal price = new BigDecimal("50.00");
        Boolean restaurantOnly = false;
        String photoPath = "/images/pizza.jpg";

        MenuItemOutput output = new MenuItemOutput(id, name, description, price, restaurantOnly, photoPath);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(name);
        assertThat(output.description()).isEqualTo(description);
        assertThat(output.price()).isEqualTo(price);
        assertThat(output.restaurantOnly()).isEqualTo(restaurantOnly);
        assertThat(output.photoPath()).isEqualTo(photoPath);
    }
}
