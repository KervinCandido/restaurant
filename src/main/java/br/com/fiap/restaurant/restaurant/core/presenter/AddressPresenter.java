package br.com.fiap.restaurant.restaurant.core.presenter;


import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.outbound.AddressOutput;

public class AddressPresenter {

    private AddressPresenter() {}

    public static AddressOutput toOutput(Address address) {
        return new AddressOutput(address.getStreet(), address.getNumber(), address.getCity(), address.getState(), address.getZipCode(), address.getComplement());
    }
}
