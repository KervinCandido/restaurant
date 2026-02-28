package br.com.fiap.restaurant.restaurant.core.domain.roles;

public enum RoleRoles implements ForGettingRoleName {
    VIEW_ROLE;

    @Override
    public String getRoleName() {
        return name();
    }
}
