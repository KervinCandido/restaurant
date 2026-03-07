package br.com.fiap.restaurant.restaurant.core.usecase;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotFoundException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CreateRestaurantUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final RestaurantGateway restaurantGateway;
    private final UserGateway userGateway;
    private final List<PublisherGateway<Restaurant>> publisherGateways;

    public CreateRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway, UserGateway userGateway, List<PublisherGateway<Restaurant>> publisherGateways) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "loggedUserGateway cannot be null.");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "restaurantGateway cannot be null.");
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway cannot be null.");
        this.publisherGateways = Objects.requireNonNull(publisherGateways, "notifierGateways cannot be null.");
    }

    public Restaurant execute(CreateRestaurantInput input) {
        Objects.requireNonNull(input, "createRestaurantInput cannot be null.");

        if (!loggedUserGateway.hasRole(Restaurant.CREATE_RESTAURANT)) {
            throw new OperationNotAllowedException();
        }

        if (restaurantGateway.existsRestaurantWithName(input.name().strip())) {
            throw new RestaurantNameIsAlreadyInUseException();
        }

        Address address = input.address() != null ? new Address (
                input.address().street(),
                input.address().number(),
                input.address().city(),
                input.address().state(),
                input.address().zipCode(),
                input.address().complement()) : null;

        User owner = userGateway.findById(input.ownerId())
                .orElseThrow(() -> new UserNotFoundException(input.ownerId()));

        List<User> employees = userGateway.findAllById(input.employees());

        if (employees.size() != input.employees().size()) {
            var uuids = employees.stream().map(User::getUuid).toList();
            var notFoundEmployees = input.employees().stream().filter(e -> !uuids.contains(e)).toList();
            throw new UserNotFoundException("employee(s) not found " + notFoundEmployees);
        }

        Restaurant restaurant = new Restaurant(null, input.name(), address, input.cuisineType(), owner);

        var openingHoursInputs = Optional.ofNullable(input.openingHours());
        var menu = Optional.ofNullable(input.menu());

        employees.forEach(restaurant::addEmployee);

        openingHoursInputs.stream()
                .flatMap(o -> o.stream()
                .map(ohi -> new OpeningHours(null, ohi.dayOfWeek(), ohi.openHour(), ohi.closeHour())))
                .forEach(restaurant::addOpeningHours);

        menu.stream()
                .flatMap(m -> m.stream()
                .map(mi -> new MenuItem(null, mi.name(), mi.description(), mi.price(), mi.restaurantOnly(), mi.photoPath())))
                .forEach(restaurant::addMenuItem);

        var newRestaurant = restaurantGateway.save(restaurant);

        publisherGateways.forEach(n -> n.publish(newRestaurant));

        return newRestaurant;
    }

}
