package br.com.fiap.restaurant.restaurant.infra.controller;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.infra.controller.request.AddressRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.request.OpeningHoursRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.request.RestaurantRequest;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.AddressEmbeddableEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.UserEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.RestaurantRepository;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureMockMvc
class RestaurantRestControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @MockitoBean
    private LoggedUserGateway loggedUserGateway;

    private RestaurantEntity restaurant;
    private Long restaurantId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        var ownerEntity = new UserEntity();
        ownerEntity.setUuid(UUID.randomUUID());
        ownerEntity.setRoles(Set.of(User.RESTAURANT_OWNER));
        ownerEntity = userRepository.save(ownerEntity);
        ownerId = ownerEntity.getUuid();

        var addressEntity = new AddressEmbeddableEntity();
        addressEntity.setStreet("Rua Ipanema");
        addressEntity.setNumber("1025");
        addressEntity.setCity("Rio grande do Leste");
        addressEntity.setState("RL");
        addressEntity.setZipCode("00000-000");
        addressEntity.setComplement("N/A");

        var newRestaurant = new RestaurantEntity();
        newRestaurant.setName("Owner's");
        newRestaurant.setCuisineType("Tradicional");
        newRestaurant.setAddress(addressEntity);
        newRestaurant.setOwner(ownerEntity);

        restaurant = restaurantRepo.save(newRestaurant);
        restaurantId = restaurant.getId();

        var loggedOwner = new User(ownerId, ownerEntity.getRoles());
        given(loggedUserGateway.requireCurrentUser()).willReturn(loggedOwner);
        given(loggedUserGateway.hasRole(anyString())).willReturn(true);
    }

    @AfterEach
    void tearDown() {
        restaurantRepo.deleteAll();
        userRepository.deleteById(ownerId);
    }

    @Test
    @WithMockUser(authorities = {"CREATE_RESTAURANT"})
    @DisplayName("Deve criar restaurante com sucesso")
    void shouldCreateRestaurantSuccessfully() throws Exception {
        var request = new RestaurantRequest();
        request.setName("New Restaurant");
        request.setCuisineType("Italian");
        var address = new AddressRequest();
        address.setStreet("Street");
        address.setNumber("123");
        address.setCity("City");
        address.setState("ST");
        address.setZipCode("12345-678");
        request.setAddress(address);
        request.setOwnerId(ownerId);
        request.setOpeningHours(Collections.singletonList(new OpeningHoursRequest(DayOfWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(23, 0))));

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.parseToString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("New Restaurant")))
                .andExpect(jsonPath("$.cuisineType", is("Italian")));
    }

    @Test
    @DisplayName("Deve buscar restaurante por id publico")
    void shouldGetPublicRestaurantById() throws Exception {
        mockMvc.perform(get("/restaurants/{id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(restaurantId.intValue())))
                .andExpect(jsonPath("$.name", is(restaurant.getName())))
                .andExpect(jsonPath("$.cuisineType", is(restaurant.getCuisineType())));
    }
    
    @Test
    @WithMockUser(authorities = {"VIEW_RESTAURANT"})
    @DisplayName("Deve buscar restaurante por id para gestao")
    void shouldGetManagementRestaurantById() throws Exception {
        mockMvc.perform(get("/restaurants/{id}/management", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(restaurantId.intValue())))
                .andExpect(jsonPath("$.name", is(restaurant.getName())))
                .andExpect(jsonPath("$.cuisineType", is(restaurant.getCuisineType())))
                .andExpect(jsonPath("$.owner", is(ownerId.toString())));
    }

    @Test
    @DisplayName("Deve listar restaurantes paginados")
    void shouldListRestaurantsPaged() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(restaurant.getName())));
    }

    @Test
    @DisplayName("Deve listar restaurantes por tipo de cozinha")
    void shouldListRestaurantsByCuisineType() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("cuisineType", "Tradicional")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(restaurant.getName())));
    }

    @Test
    @WithMockUser(authorities = {"UPDATE_RESTAURANT"})
    @DisplayName("Deve atualizar restaurante com sucesso")
    void shouldUpdateRestaurantSuccessfully() throws Exception {
        var request = new RestaurantRequest();
        request.setName("Updated Restaurant");
        request.setCuisineType("Japanese");
        var address = new AddressRequest();
        address.setStreet("New Street");
        address.setNumber("456");
        address.setCity("New City");
        address.setState("NS");
        address.setZipCode("98765-432");
        request.setAddress(address);
        request.setOwnerId(ownerId);
        request.setOpeningHours(Collections.singletonList(new OpeningHoursRequest(DayOfWeek.TUESDAY, LocalTime.of(19, 0), LocalTime.of(23, 30))));

        mockMvc.perform(put("/restaurants/{id}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.parseToString(request)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/restaurants/{id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Restaurant")))
                .andExpect(jsonPath("$.cuisineType", is("Japanese")));
    }

    @Test
    @WithMockUser(authorities = {"DELETE_RESTAURANT"})
    @DisplayName("Deve deletar restaurante com sucesso")
    void shouldDeleteRestaurantSuccessfully() throws Exception {
        mockMvc.perform(delete("/restaurants/{id}", restaurantId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/restaurants/{id}", restaurantId))
                .andExpect(status().isNotFound());
    }
}
