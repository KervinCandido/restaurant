package br.com.fiap.restaurant.restaurant.core.domain;

import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para MenuItem")
class MenuItemTest {

    @DisplayName("Deve criar menu item com sucesso")
    @Test
    void deveCriarMenuItemComSucesso() {
        Long id = 1L;
        String name = " Pizza ";
        String description = "  Delicious pizza  ";
        BigDecimal price = new BigDecimal("50.00");
        Boolean restaurantOnly = false;
        String photoPath = "/images/pizza.jpg";

        String expectedDescription = "Delicious pizza";
        String expectedName = "Pizza";
        MenuItem menuItem = new MenuItem(id, name, description, price, restaurantOnly, photoPath);

        assertThat(menuItem).isNotNull();
        assertThat(menuItem.getId()).isEqualTo(id);
        assertThat(menuItem.getName()).isEqualTo(expectedName);
        assertThat(menuItem.getDescription()).isEqualTo(expectedDescription);
        assertThat(menuItem.getPrice()).isEqualTo(price);
        assertThat(menuItem.getRestaurantOnly()).isEqualTo(restaurantOnly);
        assertThat(menuItem.getPhotoPath()).isEqualTo(photoPath);
    }

    @DisplayName("Deve criar menu item sem descrição com sucesso")
    @Test
    void deveCriarMenuItemSemDescricaoComSucesso() {
        Long id = 1L;
        String name = "Pizza";
        String description = null;
        BigDecimal price = new BigDecimal("50.00");
        Boolean restaurantOnly = false;
        String photoPath = "/images/pizza.jpg";

        MenuItem menuItem = new MenuItem(id, name, description, price, restaurantOnly, photoPath);

        assertThat(menuItem).isNotNull();
        assertThat(menuItem.getId()).isEqualTo(id);
        assertThat(menuItem.getName()).isEqualTo(name);
        assertThat(menuItem.getDescription()).isNull();
        assertThat(menuItem.getPrice()).isEqualTo(price);
        assertThat(menuItem.getRestaurantOnly()).isEqualTo(restaurantOnly);
        assertThat(menuItem.getPhotoPath()).isEqualTo(photoPath);
    }

    @DisplayName("Deve lançar NullPointerException se name for nulo")
    @Test
    void deveLancarNullPointerSeNameForNulo() {
        assertThatThrownBy(() -> new MenuItem(1L, null, "desc", BigDecimal.TEN, false, "path"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name não pode ser nulo");
    }

    @DisplayName("Deve lançar NullPointerException se price for nulo")
    @Test
    void deveLancarNullPointerSePriceForNulo() {
        assertThatThrownBy(() -> new MenuItem(1L, "name", "desc", null, false, "path"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("price não pode ser nulo");
    }

    @DisplayName("Deve lançar NullPointerException se restaurantOnly for nulo")
    @Test
    void deveLancarNullPointerSeRestaurantOnlyForNulo() {
        assertThatThrownBy(() -> new MenuItem(1L, "name", "desc", BigDecimal.TEN, null, "path"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("restaurantOnly não pode ser nula");
    }


    @DisplayName("Deve lançar BusinessException se name for vazio")
    @Test
    void deveLancarBusinessExceptionSeNameForVazio() {
        assertThatThrownBy(() -> new MenuItem(1L, "   ", "desc", BigDecimal.TEN, false, "path"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O nome do item não pode ser vazio");
    }

    @DisplayName("Deve lançar BusinessException se price for menor ou igual a zero")
    @Test
    void deveLancarBusinessExceptionSePriceForMenorOuIgualAZero() {
        assertThatThrownBy(() -> new MenuItem(1L, "name", "desc", BigDecimal.ZERO, false, "path"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O preço deve ser maior que zero");

        BigDecimal negativePrice = new BigDecimal("-1");
        assertThatThrownBy(() -> new MenuItem(1L, "name", "desc", negativePrice, false, "path"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O preço deve ser maior que zero");
    }

    @DisplayName("Deve considerar iguais menu items com mesmo id")
    @Test
    void deveConsiderarIguaisMenuItemsComMesmoId() {
        Long id = 1L;
        MenuItem firstMenuItem = new MenuItem(id, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        MenuItem secondMenuItem = new MenuItem(id, "Burger", "Delicious burger", new BigDecimal("30.00"), true, "/images/burger.jpg");

        assertThat(firstMenuItem).isEqualTo(secondMenuItem).hasSameHashCodeAs(secondMenuItem);
    }

    @DisplayName("Deve considerar diferentes menu items com ids diferentes")
    @Test
    void deveConsiderarDiferentesMenuItemsComIdsDiferentes() {
        MenuItem firstMenuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        MenuItem secondMenuItem = new MenuItem(2L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");

        assertThat(firstMenuItem).isNotEqualTo(secondMenuItem).doesNotHaveSameHashCodeAs(secondMenuItem);
    }

    @DisplayName("Deve considerar diferentes menu items se sem ids e atributos diferentes")
    @Test
    void deveConsiderarDiferentesMenuItemsSeSemIds() {
        MenuItem firstMenuItem = new MenuItem(null, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        MenuItem secondMenuItem = new MenuItem(null, "Pizza Margerita", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        MenuItem thirdMenuItem = new MenuItem(null, "Pizza", "Delicious pizza Margerita", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        MenuItem fourthMenuItem = new MenuItem(null, "Pizza", "Delicious pizza", new BigDecimal("52.00"), false, "/images/pizza.jpg");
        MenuItem fifthMenuItem = new MenuItem(null, "Pizza", "Delicious pizza", new BigDecimal("50.00"), true, "/images/pizza.jpg");
        MenuItem sixthMenuItem = new MenuItem(null, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza-2.jpg");

        assertThat(firstMenuItem)
                .isNotEqualTo(secondMenuItem).doesNotHaveSameHashCodeAs(secondMenuItem)
                .isNotEqualTo(thirdMenuItem).doesNotHaveSameHashCodeAs(thirdMenuItem)
                .isNotEqualTo(fourthMenuItem).doesNotHaveSameHashCodeAs(fourthMenuItem)
                .isNotEqualTo(fifthMenuItem).doesNotHaveSameHashCodeAs(fifthMenuItem)
                .isNotEqualTo(sixthMenuItem).doesNotHaveSameHashCodeAs(sixthMenuItem);
    }

    @DisplayName("Deve considerar iguais menu items se sem ids e atributos iguais")
    @Test
    void deveConsiderarIguaisMenuItemsSeSemIdsEAtributosIguais() {
        MenuItem firstMenuItem = new MenuItem(null, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        MenuItem secondMenuItem = new MenuItem(null, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");

        assertThat(firstMenuItem).isEqualTo(secondMenuItem).hasSameHashCodeAs(secondMenuItem);
    }

    @DisplayName("Deve considerar diferentes menu items se pelo menos um estiver com id nulo")
    @Test
    void deveConsiderarDiferentesMenuItemsSePeloMenoUmIdForNulo() {
        MenuItem firstMenuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        MenuItem secondMenuItem = new MenuItem(null, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");

        assertThat(firstMenuItem).isNotEqualTo(secondMenuItem).doesNotHaveSameHashCodeAs(secondMenuItem);
        assertThat(secondMenuItem).isNotEqualTo(firstMenuItem).doesNotHaveSameHashCodeAs(firstMenuItem);

    }

    @DisplayName("Deve considerar diferente se não instancia de MenuItem")
    @Test
    void deveConsiderarDiferentesSeForInstaciaDeOutroTipo() {
        MenuItem menuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        Object otherObject = new Object();

        assertThat(menuItem).isNotEqualTo(otherObject).doesNotHaveSameHashCodeAs(otherObject);
    }
}
