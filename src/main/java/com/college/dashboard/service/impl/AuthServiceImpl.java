package com.college.dashboard.service.impl;

import com.college.dashboard.config.JwtUtils;
import com.college.dashboard.dto.LoginRequest;
import com.college.dashboard.dto.LoginResponse;
import com.college.dashboard.entity.User;
import com.college.dashboard.exception.ResourceNotFoundException;
import com.college.dashboard.repository.UserRepository;
import com.college.dashboard.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Authenticate credentials against UserDetailsService & PasswordEncoder
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUsername()));

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return new LoginResponse(
                null,
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
