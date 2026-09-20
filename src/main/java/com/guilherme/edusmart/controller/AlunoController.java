package com.guilherme.edusmart.controller;

import com.guilherme.edusmart.dto.AlunoCadastroCompletoDTO;
import com.guilherme.edusmart.dto.AlunoCadastroDTO;
import com.guilherme.edusmart.dto.AlunoRespostaDTO;
import com.guilherme.edusmart.dto.DesempenhoAlunoDTO;
import com.guilherme.edusmart.service.AlunoService;
import com.guilherme.edusmart.service.DesempenhoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final DesempenhoService desempenhoService;

    public AlunoController(
            AlunoService alunoService,
            DesempenhoService desempenhoService) {

        this.alunoService = alunoService;
        this.desempenhoService = desempenhoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlunoRespostaDTO> cadastrar(
            @Valid @RequestBody AlunoCadastroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(alunoService.cadastrar(dto));
    }

    @PostMapping("/cadastro-completo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlunoRespostaDTO> cadastrarCompleto(
            @Valid @RequestBody AlunoCadastroCompletoDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(alunoService.cadastrarCompleto(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlunoRespostaDTO>> listarTodos() {

        return ResponseEntity.ok(
                alunoService.listarTodos()
        );
    }

    @GetMapping("/meu-desempenho")
    @PreAuthorize("hasRole('ALUNO')")
    public ResponseEntity<DesempenhoAlunoDTO> buscarMeuDesempenho() {

        return ResponseEntity.ok(
                desempenhoService.buscarMeuDesempenho()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlunoRespostaDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                alunoService.buscarPorId(id)
        );
    }

    @GetMapping("/matricula/{matricula}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlunoRespostaDTO> buscarPorMatricula(
            @PathVariable String matricula) {

        return ResponseEntity.ok(
                alunoService.buscarPorMatricula(matricula)
        );
    }
}