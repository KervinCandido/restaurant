package br.com.fiap.restaurant.restaurant.infra.auth;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Testes Unitários para LoggedUserGatewayAdapter")
class LoggedUserGatewayAdapterTest {

    private LoggedUserGatewayAdapter adapter;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        adapter = new LoggedUserGatewayAdapter();
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        closeable.close();
    }

    @Test
    @DisplayName("Deve retornar true se usuário tem a role")
    void deveRetornarTrueSeUsuarioTemRole() {
        // Given
        String roleName = User.RESTAURANT_OWNER;
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        // Mocking getAuthorities with raw type to avoid generic issues in mockito
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        // When
        boolean hasRole = adapter.hasRole(roleName);

        // Then
        assertThat(hasRole).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se usuário não tem a role")
    void deveRetornarFalseSeUsuarioNaoTemRole() {
        // Given
        String roleName = User.RESTAURANT_OWNER;
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(Restaurant.VIEW_RESTAURANT));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        // When
        boolean hasRole = adapter.hasRole(roleName);

        // Then
        assertThat(hasRole).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se roleName for nulo")
    void deveRetornarFalseSeRoleNameForNulo() {
        // When
        boolean hasRole = adapter.hasRole(null);

        // Then
        assertThat(hasRole).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se autenticação for nula")
    void deveRetornarFalseSeAutenticacaoForNula() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        boolean hasRole = adapter.hasRole("ROLE_ANY");

        // Then
        assertThat(hasRole).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se não estiver autenticado")
    void deveRetornarFalseSeNaoEstiverAutenticado() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        boolean hasRole = adapter.hasRole("ROLE_ANY");

        // Then
        assertThat(hasRole).isFalse();
    }

    @Test
    @DisplayName("Deve retornar usuário atual reconstruído a partir da autenticação")
    void deveRetornarUsuarioAtualReconstruidoDaAutenticacao() {
        // Given
        User user = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT));


        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(user.getUuid().toString());
        when(authentication.getAuthorities()).thenAnswer(invocation -> user.getRoles().stream().map(SimpleGrantedAuthority::new).toList());

        // When
        Optional<User> currentUser = adapter.getCurrentUser();

        // Then
        assertThat(currentUser).isPresent();
        assertThat(currentUser.get().getUuid()).isEqualTo(user.getUuid());
        assertThat(currentUser.get().getRoles()).isNotNull().hasSize(1)
                .containsExactlyInAnyOrder(Restaurant.VIEW_RESTAURANT);
    }

    @Test
    @DisplayName("Deve retornar vazio se autenticação for nula ao buscar usuário")
    void deveRetornarVazioSeAutenticacaoForNulaAoBuscarUsuario() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        Optional<User> currentUser = adapter.getCurrentUser();

        // Then
        assertThat(currentUser).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar vazio se não estiver autenticado ao buscar usuário")
    void deveRetornarVazioSeNaoEstiverAutenticadoAoBuscarUsuario() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        Optional<User> currentUser = adapter.getCurrentUser();

        // Then
        assertThat(currentUser).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar vazio se o nome na autenticação não for um UUID válido")
    void deveRetornarVazioSeNomeNaAutenticacaoNaoForUUIDValido() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        // Mock para simular um caso onde o nome do usuário no token não é um UUID
        when(authentication.getName()).thenReturn("not-a-valid-uuid");

        // When
        Optional<User> currentUser = adapter.getCurrentUser();

        // Then
        assertThat(currentUser).isEmpty();
    }
}
