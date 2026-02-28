package br.com.fiap.restaurant.restaurant.core.inbound;

public record AddressInput(String street, String number, String city, String state, String zipCode, String complement) {}
