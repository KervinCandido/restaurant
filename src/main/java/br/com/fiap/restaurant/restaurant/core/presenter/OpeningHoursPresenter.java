package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.model.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.outbound.OpeningHoursOutput;

public class OpeningHoursPresenter {
    private OpeningHoursPresenter() {}

    public static OpeningHoursOutput toOutput(OpeningHours openingHours) {
        return new OpeningHoursOutput(openingHours.getId(), openingHours.getDayOfWeek(), openingHours.getOpenHour(), openingHours.getCloseHour());
    }
}
