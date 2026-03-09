package br.com.fiap.restaurant.restaurant.infra.service;

import br.com.fiap.restaurant.restaurant.core.exception.InvalidCredentialsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para JwtService")
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private JwtDecoder jwtDecoder;


    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtDecoder);
    }

    @Test
    @DisplayName("Deve retornar true para token válido")
    void deveRetornarTrueParaTokenValido() {
        // Given
        String token = "valid-token";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        boolean isValid = jwtService.isValidToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção para token expirado")
    void deveLancarExcecaoParaTokenExpirado() {
        // Given
        String token = "expired-token";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().minusSeconds(3600));
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When / Then
        assertThatThrownBy(() -> jwtService.isValidToken(token))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("The token has expired.");
    }

    @Test
    @DisplayName("Deve retornar false para token inválido (JwtException)")
    void deveRetornarFalseParaTokenInvalido() {
        // Given
        String token = "invalid-token";
        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Invalid token"));

        // When
        boolean isValid = jwtService.isValidToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Deve retornar ID do usuário do token")
    void deveRetornarIdDoUsuarioDoToken() {
        // Given
        String token = "valid-token";
        String userId = "user-123";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId);
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        String result = jwtService.getUserId(token);

        // Then
        assertThat(result).isEqualTo(userId);
    }

    @Test
    @DisplayName("Deve retornar authorities do token")
    void deveRetornarAuthoritiesDoToken() {
        // Given
        String token = "valid-token";
        String roles = "ROLE_USER ROLE_ADMIN";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("roles")).thenReturn(roles);
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        Collection<GrantedAuthority> authorities = jwtService.getAuthorities(token);

        // Then
        assertThat(authorities).hasSize(2);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}
