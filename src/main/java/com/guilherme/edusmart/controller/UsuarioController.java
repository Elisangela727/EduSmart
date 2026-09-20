package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.UsuarioCadastroDTO;
import com.guilherme.edusmart.dto.UsuarioRespostaDTO;
import com.guilherme.edusmart.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRespostaDTO>> listarTodos() {

        return ResponseEntity.ok(
                usuarioService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
        );
    }
}