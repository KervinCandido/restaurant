package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.model.Role;
import br.com.fiap.restaurant.restaurant.core.outbound.RoleOutput;

public class RolePresenter {
    private RolePresenter() {}

    public static RoleOutput toOutput(Role role) {
        return new RoleOutput(role.id(), role.name());
    }
}
