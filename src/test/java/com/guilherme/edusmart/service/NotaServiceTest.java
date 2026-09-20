package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.NotaCadastroDTO;
import com.guilherme.edusmart.dto.NotaRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.Disciplina;
import com.guilherme.edusmart.model.Matricula;
import com.guilherme.edusmart.model.Nota;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.MatriculaRepository;
import com.guilherme.edusmart.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AnaliseRiscoService analiseRiscoService;

    @InjectMocks
    private NotaService notaService;

    private Matricula matricula;

    @BeforeEach
    void prepararDados() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3);
        usuario.setNome("Aluno Teste");
        usuario.setEmail("aluno@edusmart.com");
        usuario.setSenha("senha");
        usuario.setTipoUsuario("ALUNO");

        Aluno aluno = new Aluno();
        aluno.setIdAluno(2);
        aluno.setUsuario(usuario);
        aluno.setMatricula("20260002");
        aluno.setCurso("Analise e Desenvolvimento de Sistemas");
        aluno.setSemestre(3);

        usuario.setAluno(aluno);

        Disciplina disciplina = new Disciplina();
        disciplina.setIdDisciplina(1);
        disciplina.setNome("Tecnicas de Programacao");
        disciplina.setCargaHoraria(80);

        matricula = new Matricula();
        matricula.setIdMatricula(2);
        matricula.setAluno(aluno);
        matricula.setDisciplina(disciplina);
        matricula.setAno(2026);
        matricula.setSemestre(2);
    }

    @Test
    void deveCadastrarNotaEExecutarAnaliseDeRisco() {

        NotaCadastroDTO dto = new NotaCadastroDTO(
                2,
                new BigDecimal("8.50"),
                "P1"
        );

        when(matriculaRepository.findById(2))
                .thenReturn(Optional.of(matricula));

        when(notaRepository.save(any(Nota.class)))
                .thenAnswer(invocation -> {
                    Nota nota = invocation.getArgument(0);
                    nota.setIdNota(10);
                    return nota;
                });

        NotaRespostaDTO resposta = notaService.cadastrar(dto);

        ArgumentCaptor<Nota> captor =
                ArgumentCaptor.forClass(Nota.class);

        verify(notaRepository).save(captor.capture());

        Nota notaSalva = captor.getValue();

        assertEquals(2, notaSalva.getMatricula().getIdMatricula());
        assertEquals(new BigDecimal("8.50"), notaSalva.getNota());
        assertEquals("P1", notaSalva.getTipoAvaliacao());

        assertEquals(10, resposta.idNota());
        assertEquals(2, resposta.idMatricula());
        assertEquals(2, resposta.idAluno());
        assertEquals("Aluno Teste", resposta.nomeAluno());
        assertEquals(1, resposta.idDisciplina());
        assertEquals("Tecnicas de Programacao", resposta.nomeDisciplina());
        assertEquals(new BigDecimal("8.50"), resposta.nota());
        assertEquals("P1", resposta.tipoAvaliacao());

        verify(analiseRiscoService)
                .analisarAutomaticamenteSePossivel(2);
    }

    @Test
    void deveLancarExcecaoQuandoMatriculaNaoExistir() {

        NotaCadastroDTO dto = new NotaCadastroDTO(
                999,
                new BigDecimal("8.50"),
                "P1"
        );

        when(matriculaRepository.findById(999))
                .thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception =
                assertThrows(
                        RecursoNaoEncontradoException.class,
                        () -> notaService.cadastrar(dto)
                );

        assertEquals(
                "Matrícula não encontrada",
                exception.getMessage()
        );

        verify(notaRepository, never())
                .save(any(Nota.class));

        verify(analiseRiscoService, never())
                .analisarAutomaticamenteSePossivel(any());
    }
}