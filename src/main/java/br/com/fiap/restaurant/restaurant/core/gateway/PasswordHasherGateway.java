package br.com.fiap.restaurant.restaurant.core.gateway;

public interface PasswordHasherGateway {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hashedPassword);
}
