package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.NotaCadastroDTO;
import com.guilherme.edusmart.dto.NotaRespostaDTO;
import com.guilherme.edusmart.security.UsuarioAutenticadoService;
import com.guilherme.edusmart.service.NotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notas")
public class NotaController {

    private final NotaService notaService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public NotaController(
            NotaService notaService,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.notaService = notaService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotaRespostaDTO> cadastrar(
            @Valid @RequestBody NotaCadastroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notaService.cadastrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotaRespostaDTO>> listarTodas() {

        return ResponseEntity.ok(
                notaService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotaRespostaDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                notaService.buscarPorId(id)
        );
    }

    @GetMapping("/aluno/{idAluno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotaRespostaDTO>> listarPorAluno(
            @PathVariable Integer idAluno) {

        return ResponseEntity.ok(
                notaService.listarPorAluno(idAluno)
        );
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<NotaRespostaDTO>> listarMinhas() {

        Integer idAluno =
                usuarioAutenticadoService.getIdAluno();

        return ResponseEntity.ok(
                notaService.listarPorAluno(idAluno)
        );
    }
}