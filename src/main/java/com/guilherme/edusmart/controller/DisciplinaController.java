package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.DisciplinaCadastroDTO;
import com.guilherme.edusmart.model.Disciplina;
import com.guilherme.edusmart.service.DisciplinaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    public DisciplinaController(
            DisciplinaService disciplinaService) {

        this.disciplinaService = disciplinaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Disciplina> cadastrar(
            @Valid @RequestBody DisciplinaCadastroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(disciplinaService.cadastrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUNO')")
    public ResponseEntity<List<Disciplina>> listarTodas() {

        return ResponseEntity.ok(
                disciplinaService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALUNO')")
    public ResponseEntity<Disciplina> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                disciplinaService.buscarPorId(id)
        );
    }
}