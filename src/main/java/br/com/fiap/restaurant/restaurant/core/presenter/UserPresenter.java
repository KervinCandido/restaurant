package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.outbound.UserOutput;

public class UserPresenter {
    private UserPresenter() {}

    public static UserOutput toOutput(User user) {
        if (user == null) {
            return null;
        }
        return new UserOutput(
            user.getUuid(),
            user.getRoles()
        );
    }
}
