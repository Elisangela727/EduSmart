package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.FrequenciaCadastroDTO;
import com.guilherme.edusmart.dto.FrequenciaRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.Disciplina;
import com.guilherme.edusmart.model.Frequencia;
import com.guilherme.edusmart.model.Matricula;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.FrequenciaRepository;
import com.guilherme.edusmart.repository.MatriculaRepository;
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
class FrequenciaServiceTest {

    @Mock
    private FrequenciaRepository frequenciaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AnaliseRiscoService analiseRiscoService;

    @InjectMocks
    private FrequenciaService frequenciaService;

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
    void deveCadastrarFrequenciaEExecutarAnaliseDeRisco() {

        FrequenciaCadastroDTO dto = new FrequenciaCadastroDTO(
                2,
                new BigDecimal("90.00"),
                "2026.2"
        );

        when(matriculaRepository.findById(2))
                .thenReturn(Optional.of(matricula));

        when(frequenciaRepository.save(any(Frequencia.class)))
                .thenAnswer(invocation -> {
                    Frequencia frequencia = invocation.getArgument(0);
                    frequencia.setIdFrequencia(10);
                    return frequencia;
                });

        FrequenciaRespostaDTO resposta =
                frequenciaService.cadastrar(dto);

        ArgumentCaptor<Frequencia> captor =
                ArgumentCaptor.forClass(Frequencia.class);

        verify(frequenciaRepository)
                .save(captor.capture());

        Frequencia frequenciaSalva =
                captor.getValue();

        assertEquals(
                2,
                frequenciaSalva.getMatricula().getIdMatricula()
        );

        assertEquals(
                new BigDecimal("90.00"),
                frequenciaSalva.getPercentual()
        );

        assertEquals(
                "2026.2",
                frequenciaSalva.getPeriodo()
        );

        assertEquals(10, resposta.idFrequencia());
        assertEquals(2, resposta.idMatricula());
        assertEquals(2, resposta.idAluno());
        assertEquals("Aluno Teste", resposta.nomeAluno());
        assertEquals(1, resposta.idDisciplina());
        assertEquals(
                "Tecnicas de Programacao",
                resposta.nomeDisciplina()
        );
        assertEquals(
                new BigDecimal("90.00"),
                resposta.percentual()
        );
        assertEquals("2026.2", resposta.periodo());

        verify(analiseRiscoService)
                .analisarAutomaticamenteSePossivel(2);
    }

    @Test
    void deveLancarExcecaoQuandoMatriculaNaoExistir() {

        FrequenciaCadastroDTO dto =
                new FrequenciaCadastroDTO(
                        999,
                        new BigDecimal("90.00"),
                        "2026.2"
                );

        when(matriculaRepository.findById(999))
                .thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception =
                assertThrows(
                        RecursoNaoEncontradoException.class,
                        () -> frequenciaService.cadastrar(dto)
                );

        assertEquals(
                "Matrícula não encontrada",
                exception.getMessage()
        );

        verify(frequenciaRepository, never())
                .save(any(Frequencia.class));

        verify(analiseRiscoService, never())
                .analisarAutomaticamenteSePossivel(any());
    }
}