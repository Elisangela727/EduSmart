package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.AnaliseRiscoRespostaDTO;
import com.guilherme.edusmart.security.UsuarioAutenticadoService;
import com.guilherme.edusmart.service.AnaliseRiscoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analises-risco")
public class AnaliseRiscoController {

    private final AnaliseRiscoService analiseRiscoService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AnaliseRiscoController(
            AnaliseRiscoService analiseRiscoService,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.analiseRiscoService = analiseRiscoService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @PostMapping("/aluno/{idAluno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnaliseRiscoRespostaDTO> analisar(
            @PathVariable Integer idAluno) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(analiseRiscoService.analisar(idAluno));
    }

    @GetMapping("/aluno/{idAluno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AnaliseRiscoRespostaDTO>> listarPorAluno(
            @PathVariable Integer idAluno) {

        return ResponseEntity.ok(
                analiseRiscoService.listarPorAluno(idAluno)
        );
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<AnaliseRiscoRespostaDTO>> listarMinhas() {

        Integer idAluno =
                usuarioAutenticadoService.getIdAluno();

        return ResponseEntity.ok(
                analiseRiscoService.listarPorAluno(idAluno)
        );
    }
}