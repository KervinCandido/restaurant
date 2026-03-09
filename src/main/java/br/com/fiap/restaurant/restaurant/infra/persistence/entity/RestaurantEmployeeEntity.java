package br.com.fiap.restaurant.restaurant.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurant_employees")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantEmployeeEntity {

    @EmbeddedId
    private RestaurantEmployeeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("restaurantId")
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_uuid", nullable = false)
    private UserEntity employee;

    public RestaurantEmployeeEntity(RestaurantEntity restaurant, UserEntity employee) {
        this.id = new RestaurantEmployeeId();
        this.id.setRestaurantId(restaurant.getId());
        this.id.setEmployeeId(employee.getUuid());
        this.restaurant = restaurant;
        this.employee = employee;
    }

    @Embeddable
    @Getter @Setter
    @NoArgsConstructor
    public static class RestaurantEmployeeId implements Serializable {
        @Column(name = "restaurant_id")
        private Long restaurantId;

        @Column(name = "employee_uuid")
        private UUID employeeId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RestaurantEmployeeId that)) return false;
            return Objects.equals(restaurantId, that.restaurantId)
                    && Objects.equals(employeeId, that.employeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(restaurantId, employeeId);
        }
    }
}
