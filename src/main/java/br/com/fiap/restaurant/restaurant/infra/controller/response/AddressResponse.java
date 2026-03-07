package br.com.fiap.restaurant.restaurant.infra.controller.response;

public record AddressResponse (
        String street,
        String number,
        String city,
        String state,
        String zipCode,
        String complement
) {}
