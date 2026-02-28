package br.com.fiap.restaurant.restaurant.core.domain.roles;

public enum UserRoles implements ForGettingRoleName {
    RESTAURANT_OWNER;

    @Override
    public String getRoleName() {
        return name();
    }
}
