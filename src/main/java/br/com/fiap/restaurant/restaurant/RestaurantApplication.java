package br.com.fiap.restaurant.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "br.com.fiap.restaurant.restaurant.infra")
@ConfigurationPropertiesScan
public class RestaurantApplication {

    private RestaurantApplication() {}

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }
}