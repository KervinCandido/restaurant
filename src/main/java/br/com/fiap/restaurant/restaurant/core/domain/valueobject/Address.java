package br.com.fiap.restaurant.restaurant.core.domain.valueobject;

import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;

import java.util.Objects;

public class Address {

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;

    public Address(String street, String number, String city, String state, String zipCode, String complement) {
        if (street == null || street.trim().isEmpty()) {
            throw new BusinessException("Rua é obrigatória.");
        }
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.complement = complement;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Address address)) return false;
        return Objects.equals(street, address.street)
                && Objects.equals(number, address.number)
                && Objects.equals(city, address.city)
                && Objects.equals(state, address.state)
                && Objects.equals(zipCode, address.zipCode)
                && Objects.equals(complement, address.complement);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(street);
        result = 31 * result + Objects.hashCode(number);
        result = 31 * result + Objects.hashCode(city);
        result = 31 * result + Objects.hashCode(state);
        result = 31 * result + Objects.hashCode(zipCode);
        result = 31 * result + Objects.hashCode(complement);
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", complement='" + complement + '\'' +
                '}';
    }
}
