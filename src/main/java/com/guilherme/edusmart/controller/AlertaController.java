package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.AlertaRespostaDTO;
import com.guilherme.edusmart.security.UsuarioAutenticadoService;
import com.guilherme.edusmart.service.AlertaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AlertaController(
            AlertaService alertaService,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.alertaService = alertaService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @PostMapping("/aluno/{idAluno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertaRespostaDTO> gerar(
            @PathVariable Integer idAluno) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(alertaService.gerar(idAluno));
    }

    @GetMapping("/aluno/{idAluno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertaRespostaDTO>> listarPorAluno(
            @PathVariable Integer idAluno) {

        return ResponseEntity.ok(
                alertaService.listarPorAluno(idAluno)
        );
    }

    @GetMapping("/aluno/{idAluno}/nao-visualizados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertaRespostaDTO>> listarNaoVisualizados(
            @PathVariable Integer idAluno) {

        return ResponseEntity.ok(
                alertaService.listarNaoVisualizados(idAluno)
        );
    }

    @PatchMapping("/{idAlerta}/visualizar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertaRespostaDTO> marcarComoVisualizado(
            @PathVariable Integer idAlerta) {

        return ResponseEntity.ok(
                alertaService.marcarComoVisualizado(idAlerta)
        );
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<AlertaRespostaDTO>> listarMeus() {

        Integer idAluno =
                usuarioAutenticadoService.getIdAluno();

        return ResponseEntity.ok(
                alertaService.listarPorAluno(idAluno)
        );
    }

    @GetMapping("/meus/nao-visualizados")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<AlertaRespostaDTO>> listarMeusNaoVisualizados() {

        Integer idAluno =
                usuarioAutenticadoService.getIdAluno();

        return ResponseEntity.ok(
                alertaService.listarNaoVisualizados(idAluno)
        );
    }

    @PatchMapping("/meus/{idAlerta}/visualizar")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<AlertaRespostaDTO> marcarMeuComoVisualizado(
            @PathVariable Integer idAlerta) {

        Integer idAluno =
                usuarioAutenticadoService.getIdAluno();

        return ResponseEntity.ok(
                alertaService.marcarComoVisualizadoPeloAluno(
                        idAlerta,
                        idAluno
                )
        );
    }
}