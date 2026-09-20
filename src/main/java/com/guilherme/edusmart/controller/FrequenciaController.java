package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.FrequenciaCadastroDTO;
import com.guilherme.edusmart.dto.FrequenciaRespostaDTO;
import com.guilherme.edusmart.security.UsuarioAutenticadoService;
import com.guilherme.edusmart.service.FrequenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/frequencias")
public class FrequenciaController {

    private final FrequenciaService frequenciaService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public FrequenciaController(
            FrequenciaService frequenciaService,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.frequenciaService = frequenciaService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FrequenciaRespostaDTO> cadastrar(
            @Valid @RequestBody FrequenciaCadastroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(frequenciaService.cadastrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FrequenciaRespostaDTO>> listarTodas() {

        return ResponseEntity.ok(
                frequenciaService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FrequenciaRespostaDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                frequenciaService.buscarPorId(id)
        );
    }

    @GetMapping("/aluno/{idAluno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FrequenciaRespostaDTO>> listarPorAluno(
            @PathVariable Integer idAluno) {

        return ResponseEntity.ok(
                frequenciaService.listarPorAluno(idAluno)
        );
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<List<FrequenciaRespostaDTO>> listarMinhas() {

        Integer idAluno =
                usuarioAutenticadoService.getIdAluno();

        return ResponseEntity.ok(
                frequenciaService.listarPorAluno(idAluno)
        );
    }
}