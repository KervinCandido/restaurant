package br.com.fiap.restaurant.restaurant.infra.service;

import br.com.fiap.restaurant.restaurant.core.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.restaurant.infra.controller.request.AuthRequest;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;
import br.com.fiap.restaurant.restaurant.infra.vo.JwtBearerToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public JwtBearerToken authentication(AuthRequest authRequest) throws InvalidCredentialsException {
        var user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(authRequest.password(), user.getPassword()))
            throw new InvalidCredentialsException();

        return jwtService.generateToken(user);
    }
}
