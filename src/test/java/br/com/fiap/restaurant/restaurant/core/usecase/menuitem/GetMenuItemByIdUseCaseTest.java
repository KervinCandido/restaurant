package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetMenuItemByIdUseCase (UseCaseBase)")
class GetMenuItemByIdUseCaseTest {

    @Mock private MenuItemGateway menuItemGateway;

    @InjectMocks
    private GetMenuItemByIdUseCase useCase;

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Arrange
        // (nada)

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id cannot be null");

        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando item não existir")
    void shouldThrowBusinessExceptionWhenMenuItemNotFound() {
        // Arrange
        Long itemId = 10L;

        given(menuItemGateway.findById(itemId)).willReturn(Optional.empty());

        // Act / Assert
        var optionalResult = useCase.execute(itemId);

        assertThat(optionalResult).isEmpty();

        then(menuItemGateway).should().findById(itemId);

        then(menuItemGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Deve retornar item quando existir e usuário tiver permissão")
    void shouldReturnMenuItemWhenFoundAndUserHasRole() {
        // Arrange
        Long itemId = 10L;

        MenuItem expected = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza Margherita")
                .build();

        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(expected));

        // Act
        var optionalResult = useCase.execute(itemId);

        // Assert
        assertThat(optionalResult).isNotEmpty();
        var result = optionalResult.get();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Pizza Margherita");

        then(menuItemGateway).should().findById(itemId);

        then(menuItemGateway).shouldHaveNoMoreInteractions();
    }
}
