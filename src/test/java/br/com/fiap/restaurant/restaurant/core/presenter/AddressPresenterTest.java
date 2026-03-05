package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.outbound.AddressOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para AddressPresenter")
class AddressPresenterTest {

    @DisplayName("Deve converter Address para AddressOutput com sucesso")
    @Test
    void deveConverterAddressParaAddressOutputComSucesso() {
        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        address.setComplement("Apto 101");

        AddressOutput output = AddressPresenter.toOutput(address);

        assertThat(output).isNotNull();
        assertThat(output.street()).isEqualTo(address.getStreet());
        assertThat(output.number()).isEqualTo(address.getNumber());
        assertThat(output.city()).isEqualTo(address.getCity());
        assertThat(output.state()).isEqualTo(address.getState());
        assertThat(output.zipCode()).isEqualTo(address.getZipCode());
        assertThat(output.complement()).isEqualTo(address.getComplement());
    }
}
