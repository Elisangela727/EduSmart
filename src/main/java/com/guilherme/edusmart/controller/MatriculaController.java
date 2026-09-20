package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.MatriculaCadastroDTO;
import com.guilherme.edusmart.dto.MatriculaRespostaDTO;
import com.guilherme.edusmart.security.UsuarioAutenticadoService;
import com.guilherme.edusmart.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public MatriculaController(
            MatriculaService matriculaService,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.matriculaService = matriculaService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatriculaRespostaDTO> cadastrar(
            @Valid @RequestBody MatriculaCadastroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matriculaService.cadastrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatriculaRespostaDTO>> listarTodas() {

        return ResponseEntity.ok(
                matriculaService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatriculaRespostaDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                matriculaService.buscarPorId(id)
        );
    }

    @GetMapping("/aluno/{idAluno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatriculaRespostaDTO>> listarPorAluno(
            @PathVariable Integer idAluno) {

        return ResponseEntity.ok(
                matriculaService.listarPorAluno(idAluno)
        );
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<MatriculaRespostaDTO>> listarMinhas() {

        Integer idAluno =
                usuarioAutenticadoService.getIdAluno();

        return ResponseEntity.ok(
                matriculaService.listarPorAluno(idAluno)
        );
    }
}