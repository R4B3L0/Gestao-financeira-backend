package com.financeapp.backend.service;

import com.financeapp.backend.dto.LoginRequest;
import com.financeapp.backend.dto.RegisterRequest;
import com.financeapp.backend.entity.Usuario;
import com.financeapp.backend.repository.UsuarioRepository;
import com.financeapp.backend.security.JwtUtils; // Assuming JwtUtils will be created later
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils; // Assuming JwtUtils will be created later

    public void registerUser(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            // Consider throwing a specific exception here
            throw new RuntimeException("Erro: Email já está em uso!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getNome());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setSenhaHash(passwordEncoder.encode(registerRequest.getSenha()));

        usuarioRepository.save(usuario);
    }

    public String loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return jwt;
    }
}

