package br.com.fiap.restaurant.restaurant.core.domain;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.UserCannotBeRestaurantOwnerException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class Restaurant {

    public static final String VIEW_RESTAURANT = "VIEW_RESTAURANT";
    public static final String VIEW_RESTAURANT_MANAGEMENT = "VIEW_RESTAURANT_MANAGEMENT";
    public static final String CREATE_RESTAURANT = "CREATE_RESTAURANT";
    public static final String UPDATE_RESTAURANT = "UPDATE_RESTAURANT";
    public static final String DELETE_RESTAURANT = "DELETE_RESTAURANT";

    private final Long id;
    private final String name;
    private final String cuisineType;
    private final Address address;
    private final User owner;
    private final Set<OpeningHours> openingHours;
    private final Set<MenuItem> menu;
    private final Set<User> employees;

    public Restaurant(Long id, String name, Address address, String cuisineType, User owner) {
        Objects.requireNonNull(name, "O nome do restaurante não pode ser nulo.");
        Objects.requireNonNull(address, "O endereço não pode ser nulo.");
        Objects.requireNonNull(cuisineType, "O tipo de cozinha não pode ser nulo.");
        Objects.requireNonNull(owner, "O dono do restaurante não pode ser nulo.");

        if (name.isBlank()) {
            throw new BusinessException("O nome do restaurante não pode ser vazio.");
        }
        if (cuisineType.isBlank()) {
            throw new BusinessException("O tipo de cozinha não pode ser vazio.");
        }
        if (!owner.canOwnRestaurant()) {
            throw new UserCannotBeRestaurantOwnerException();
        }

        this.id = id;
        this.name = name.strip();
        this.address = address;
        this.cuisineType = cuisineType.strip();
        this.openingHours = new HashSet<>();
        this.menu = new HashSet<>();
        this.owner = owner;
        this.employees = new HashSet<>();
    }

    public void addOpeningHours(OpeningHours openingHour) {
        this.openingHours.add(openingHour);
    }

    public void addOpeningHours(Collection<OpeningHours> openingHours) {
        this.openingHours.addAll(openingHours);
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menu.add(menuItem);
    }

    public void addMenuItems(Collection<? extends MenuItem> menuItems) {
        this.menu.addAll(menuItems);
    }

    public void addEmployee(User employee) {
        this.employees.add(employee);
    }

    public void addEmployees(Collection<? extends User> employees) {
        this.employees.addAll(employees);
    }

    public Set<OpeningHours> getOpeningHours() {
        return new HashSet<>(this.openingHours);
    }

    public Set<MenuItem> getMenuItems() {
        return Set.copyOf(this.menu);
    }

    public Set<User> getEmployees() {
        return new HashSet<>(this.employees);
    }

    public boolean canBeManagedBy(User currentUser) {
        if (currentUser == null) return false;
        return currentUser.equals(owner) || employees.contains(currentUser);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public Address getAddress() {
        return address;
    }

    public User getOwner() {
        return owner;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant that)) return false;

        if (this.id != null || that.id != null) {
            return Objects.equals(this.id, that.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : super.hashCode();
    }
}