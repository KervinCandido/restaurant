package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.model.User;
import br.com.fiap.restaurant.restaurant.core.domain.model.UserType;
import br.com.fiap.restaurant.restaurant.core.outbound.UserOutput;
import br.com.fiap.restaurant.restaurant.core.outbound.UserSummaryOutput;

public class UserPresenter {

    private UserPresenter() {}

    public static UserOutput toOutput(User user) {
        if (user == null) return null;

        UserType userType = user.getUserType();

        return new UserOutput(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getAddress() == null ? null : AddressPresenter.toOutput(user.getAddress()),
                UserTypePresenter.toOutput(userType)
        );
    }

    public static UserSummaryOutput toSummaryOutput(User user) {
        if (user == null) return null;
        return new UserSummaryOutput(user.getId(), user.getName());
    }
}
