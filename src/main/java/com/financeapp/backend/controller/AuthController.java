package com.financeapp.backend.controller;

import com.financeapp.backend.dto.JwtResponse;
import com.financeapp.backend.dto.LoginRequest;
import com.financeapp.backend.dto.RegisterRequest;
import com.financeapp.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600) // Allow all origins for simplicity, adjust as needed
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = authService.loginUser(loginRequest);
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            // Consider more specific exception handling and responses
            return ResponseEntity.status(401).body("Erro: Credenciais inválidas ou erro no servidor.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            return ResponseEntity.status(201).body("Usuário criado com sucesso!");
        } catch (RuntimeException e) {
            // Catch specific exceptions if defined in service
            return ResponseEntity.status(400).body(e.getMessage()); // Return specific error message
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno ao registrar usuário.");
        }
    }
}

