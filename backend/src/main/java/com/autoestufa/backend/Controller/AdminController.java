package com.autoestufa.backend.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoestufa.backend.Model.AdminModel;
import com.autoestufa.backend.Repository.AdminRepository;

import lombok.RequiredArgsConstructor;

// Disponibiliza o acesso do administrador ao sistema.
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminRepository adminRepository;

    // Valida as credenciais informadas pelo frontend.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AdminModel admin = adminRepository.findByEmail(request.email()).orElse(null);

        if (admin == null || !admin.getSenha().equals(request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "E-mail ou senha inválidos."));
        }

        return ResponseEntity.ok(Map.of(
                "mensagem", "Login realizado com sucesso.",
                "email", admin.getEmail()));
    }

    // Formato esperado pelo endpoint de login.
    public record LoginRequest(String email, String senha) {
    }
}