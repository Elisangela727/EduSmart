package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.LoginDTO;
import com.guilherme.edusmart.dto.LoginRespostaDTO;
import com.guilherme.edusmart.dto.UsuarioCadastroDTO;
import com.guilherme.edusmart.dto.UsuarioRespostaDTO;
import com.guilherme.edusmart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRespostaDTO> login(
            @Valid @RequestBody LoginDTO dto) {

        return ResponseEntity.ok(
                authService.login(dto)
        );
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.cadastrar(dto));
    }
}