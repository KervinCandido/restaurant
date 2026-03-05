package br.com.fiap.restaurant.restaurant.core.domain.valueobject;

import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para Address")
class AddressTest {

    @DisplayName("Deve criar endereço com sucesso")
    @Test
    void deveCriarEnderecoComSucesso() {
        String street = "Rua Teste";
        String number = "123";
        String city = "Cidade Teste";
        String state = "Estado Teste";
        String zipCode = "12345-678";
        String complement = "Apto 101";

        Address address = new Address(street, number, city, state, zipCode, complement);

        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo(street);
        assertThat(address.getNumber()).isEqualTo(number);
        assertThat(address.getCity()).isEqualTo(city);
        assertThat(address.getState()).isEqualTo(state);
        assertThat(address.getZipCode()).isEqualTo(zipCode);
        assertThat(address.getComplement()).isEqualTo(complement);
    }

    @DisplayName("Deve lançar BusinessException se street for nulo")
    @Test
    void deveLancarBusinessExceptionSeStreetForNulo() {
        assertThatThrownBy(() -> new Address(null, "123", "Cidade", "Estado", "CEP", "Comp"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Rua é obrigatória");
    }

    @DisplayName("Deve lançar BusinessException se street for vazio")
    @Test
    void deveLancarBusinessExceptionSeStreetForVazio() {
        assertThatThrownBy(() -> new Address("   ", "123", "Cidade", "Estado", "CEP", "Comp"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Rua é obrigatória");
    }

    @DisplayName("Deve atualizar street com sucesso")
    @Test
    void deveAtualizarStreetComSucesso() {
        Address address = new Address("Rua Antiga", "123", "Cidade", "Estado", "CEP", "Comp");
        String newStreet = "Rua Nova";
        address.setStreet(newStreet);
        assertThat(address.getStreet()).isEqualTo(newStreet);
    }

    @DisplayName("Deve atualizar number com sucesso")
    @Test
    void deveAtualizarNumberComSucesso() {
        Address address = new Address("Rua", "123", "Cidade", "Estado", "CEP", "Comp");
        String newNumber = "456";
        address.setNumber(newNumber);
        assertThat(address.getNumber()).isEqualTo(newNumber);
    }

    @DisplayName("Deve atualizar city com sucesso")
    @Test
    void deveAtualizarCityComSucesso() {
        Address address = new Address("Rua", "123", "Cidade Antiga", "Estado", "CEP", "Comp");
        String newCity = "Cidade Nova";
        address.setCity(newCity);
        assertThat(address.getCity()).isEqualTo(newCity);
    }

    @DisplayName("Deve atualizar state com sucesso")
    @Test
    void deveAtualizarStateComSucesso() {
        Address address = new Address("Rua", "123", "Cidade", "Estado Antigo", "CEP", "Comp");
        String newState = "Estado Novo";
        address.setState(newState);
        assertThat(address.getState()).isEqualTo(newState);
    }

    @DisplayName("Deve atualizar zipCode com sucesso")
    @Test
    void deveAtualizarZipCodeComSucesso() {
        Address address = new Address("Rua", "123", "Cidade", "Estado", "CEP Antigo", "Comp");
        String newZipCode = "CEP Novo";
        address.setZipCode(newZipCode);
        assertThat(address.getZipCode()).isEqualTo(newZipCode);
    }

    @DisplayName("Deve atualizar complement com sucesso")
    @Test
    void deveAtualizarComplementComSucesso() {
        Address address = new Address("Rua", "123", "Cidade", "Estado", "CEP", "Comp Antigo");
        String newComplement = "Comp Novo";
        address.setComplement(newComplement);
        assertThat(address.getComplement()).isEqualTo(newComplement);
    }

    @DisplayName("Deve considerar iguais endereços com mesmos atributos")
    @Test
    void deveConsiderarIguaisEnderecosComMesmosAtributos() {
        Address address1 = new Address("Rua", "123", "Cidade", "Estado", "CEP", "Comp");
        Address address2 = new Address("Rua", "123", "Cidade", "Estado", "CEP", "Comp");

        assertThat(address1).isEqualTo(address2).hasSameHashCodeAs(address2);
    }

    @DisplayName("Deve considerar diferentes endereços com atributos diferentes")
    @Test
    void deveConsiderarDiferentesEnderecosComAtributosDiferentes() {
        Address address1 = new Address("Rua 1", "123", "Cidade", "SP", "00000-000", "Comp");
        Address address2 = new Address("Rua 2", "123", "Cidade", "SP", "00000-000", "Comp");
        Address address3 = new Address("Rua 1", "124", "Cidade", "SP", "00000-000", "Comp");
        Address address4 = new Address("Rua 1", "123", "CidadeZ", "SP", "00000-000", "Comp");
        Address address5 = new Address("Rua 1", "123", "Cidade", "RJ", "00000-000", "Comp");
        Address address6 = new Address("Rua 1", "123", "Cidade", "SP", "00000-001", "Comp");
        Address address7 = new Address("Rua 1", "123", "Cidade", "SP", "00000-000", "Complemento");

        assertThat(address1)
                .isNotEqualTo(address2).doesNotHaveSameHashCodeAs(address2)
                .isNotEqualTo(address3).doesNotHaveSameHashCodeAs(address3)
                .isNotEqualTo(address4).doesNotHaveSameHashCodeAs(address4)
                .isNotEqualTo(address5).doesNotHaveSameHashCodeAs(address5)
                .isNotEqualTo(address6).doesNotHaveSameHashCodeAs(address6)
                .isNotEqualTo(address7).doesNotHaveSameHashCodeAs(address7)
        ;
    }

    @DisplayName("Deve considerar diferente se não instancia de Address")
    @Test
    void deveConsiderarDiferentesSeForInstaciaDeOutroTipo() {
        Address address = new Address("Rua", "123", "Cidade", "Estado", "CEP", "Comp");
        Object otherObject = new Object();

        assertThat(address).isNotEqualTo(otherObject).doesNotHaveSameHashCodeAs(otherObject);
    }

    @DisplayName("Deve retornar string correta no toString")
    @Test
    void deveRetornarStringCorretaNoToString() {
        Address address = new Address("Rua", "123", "Cidade", "Estado", "CEP", "Comp");
        String expectedString = "Address{street='Rua', number='123', city='Cidade', state='Estado', zipCode='CEP', complement='Comp'}";

        assertThat(address.toString()).isNotNull().isEqualTo(expectedString);
    }
}
