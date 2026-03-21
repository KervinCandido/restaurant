package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.infra.controller.response.SimpleErrorResponse;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;
import br.com.fiap.restaurant.restaurant.infra.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // Habilita segurança em métodos
public class RouteSecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public RouteSecurityConfig(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        final var restaurantUrl = "/restaurants";
        final var menuItemBaseUrl = restaurantUrl + "/{restaurant-id}/menu";
        final var menuWithIdUrl = restaurantUrl + "/{restaurant-id}/menu/{id}";
        final var restaurantWithIdUrl = restaurantUrl + "/{id}";

        http.httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> req
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator", "/actuator/**").permitAll()
                .requestMatchers(HttpMethod.POST, restaurantUrl).hasAuthority(Restaurant.CREATE_RESTAURANT)
                .requestMatchers(HttpMethod.PUT, restaurantUrl).hasAuthority(Restaurant.UPDATE_RESTAURANT)
                .requestMatchers(HttpMethod.DELETE, restaurantUrl).hasAuthority(Restaurant.DELETE_RESTAURANT)
                .requestMatchers(HttpMethod.GET, restaurantUrl).permitAll()
                .requestMatchers(HttpMethod.GET, restaurantUrl).permitAll()
                .requestMatchers(HttpMethod.GET, restaurantWithIdUrl).permitAll()
                .requestMatchers(HttpMethod.GET, restaurantWithIdUrl + "/management").hasAuthority(Restaurant.VIEW_RESTAURANT_MANAGEMENT)

                .requestMatchers(HttpMethod.POST, menuItemBaseUrl).hasAuthority(MenuItem.CREATE_MENU_ITEM)
                .requestMatchers(HttpMethod.PUT, menuWithIdUrl).hasAuthority(MenuItem.UPDATE_MENU_ITEM)
                .requestMatchers(HttpMethod.DELETE, menuWithIdUrl).hasAuthority(MenuItem.DELETE_MENU_ITEM)
                .requestMatchers(HttpMethod.GET, menuItemBaseUrl).permitAll()
                .requestMatchers(HttpMethod.GET, menuWithIdUrl).permitAll()

                .anyRequest().authenticated() // Boa prática: fechar com uma regra padrão
            )
            .addFilterBefore(new TokenAuthenticationFilter(jwtService, userRepository), UsernamePasswordAuthenticationFilter.class)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
            http.exceptionHandling(customizer ->
            customizer.accessDeniedHandler(accessDeniedHandler())
                    .authenticationEntryPoint(authenticationEntryPoint())
        );
        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            var writer = response.getWriter();
            response.setStatus(403);
            response.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(new SimpleErrorResponse("The current user does not have permission.")));
        };
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            var writer = response.getWriter();
            response.setStatus(401);
            response.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(new SimpleErrorResponse("User not authenticated. Authentication is required to access this resource.")));
        };
    }
}