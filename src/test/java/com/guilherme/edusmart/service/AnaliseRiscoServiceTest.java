package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.AnaliseRiscoRespostaDTO;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.AnaliseRisco;
import com.guilherme.edusmart.model.Frequencia;
import com.guilherme.edusmart.model.Nota;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.AlunoRepository;
import com.guilherme.edusmart.repository.AnaliseRiscoRepository;
import com.guilherme.edusmart.repository.FrequenciaRepository;
import com.guilherme.edusmart.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnaliseRiscoServiceTest {

    @Mock
    private AnaliseRiscoRepository analiseRiscoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private FrequenciaRepository frequenciaRepository;

    @Mock
    private MachineLearningClient machineLearningClient;

    @Mock
    private AlertaService alertaService;

    @InjectMocks
    private AnaliseRiscoService analiseRiscoService;

    private Aluno aluno;

    @BeforeEach
    void prepararDados() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3);
        usuario.setNome("Aluno Teste");
        usuario.setEmail("aluno@edusmart.com");
        usuario.setSenha("senha");
        usuario.setTipoUsuario("ALUNO");

        aluno = new Aluno();
        aluno.setIdAluno(2);
        aluno.setUsuario(usuario);
        aluno.setMatricula("20260002");
        aluno.setCurso("Analise e Desenvolvimento de Sistemas");
        aluno.setSemestre(3);

        usuario.setAluno(aluno);
    }

    @Test
    void deveCalcularMediasExecutarMachineLearningSalvarAnaliseEGerarAlerta() {

        Nota nota1 = criarNota("8.00");
        Nota nota2 = criarNota("6.00");

        Frequencia frequencia1 = criarFrequencia("90.00");
        Frequencia frequencia2 = criarFrequencia("80.00");

        when(alunoRepository.findById(2))
                .thenReturn(Optional.of(aluno));

        when(notaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of(nota1, nota2));

        when(frequenciaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of(frequencia1, frequencia2));

        when(machineLearningClient.preverRisco(
                new BigDecimal("7.00"),
                new BigDecimal("85.00")
        )).thenReturn("BAIXO");

        when(analiseRiscoRepository.save(any(AnaliseRisco.class)))
                .thenAnswer(invocation -> {
                    AnaliseRisco analise = invocation.getArgument(0);
                    analise.setIdAnalise(10);
                    return analise;
                });

        AnaliseRiscoRespostaDTO resposta =
                analiseRiscoService.analisar(2);

        verify(machineLearningClient)
                .preverRisco(
                        new BigDecimal("7.00"),
                        new BigDecimal("85.00")
                );

        ArgumentCaptor<AnaliseRisco> captor =
                ArgumentCaptor.forClass(AnaliseRisco.class);

        verify(analiseRiscoRepository)
                .save(captor.capture());

        AnaliseRisco analiseSalva =
                captor.getValue();

        assertEquals(2, analiseSalva.getAluno().getIdAluno());
        assertEquals("BAIXO", analiseSalva.getNivelRisco());
        assertNull(analiseSalva.getProbabilidade());

        assertEquals(
                "Análise realizada por Machine Learning | Média das notas: 7.00 | Frequência média: 85.00%",
                analiseSalva.getObservacao()
        );

        assertEquals(10, resposta.idAnalise());
        assertEquals(2, resposta.idAluno());
        assertEquals("Aluno Teste", resposta.nomeAluno());
        assertEquals("BAIXO", resposta.nivelRisco());
        assertNull(resposta.probabilidade());

        verify(alertaService)
                .gerarParaAnalise(aluno, analiseSalva);
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoPossuirNotas() {

        when(alunoRepository.findById(2))
                .thenReturn(Optional.of(aluno));

        when(notaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of());

        when(frequenciaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of(criarFrequencia("90.00")));

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> analiseRiscoService.analisar(2)
                );

        assertEquals(
                "Aluno não possui notas cadastradas",
                exception.getMessage()
        );

        verify(machineLearningClient, never())
                .preverRisco(any(), any());

        verify(analiseRiscoRepository, never())
                .save(any());

        verify(alertaService, never())
                .gerarParaAnalise(any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoPossuirFrequencia() {

        when(alunoRepository.findById(2))
                .thenReturn(Optional.of(aluno));

        when(notaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of(criarNota("8.00")));

        when(frequenciaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of());

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> analiseRiscoService.analisar(2)
                );

        assertEquals(
                "Aluno não possui frequência cadastrada",
                exception.getMessage()
        );

        verify(machineLearningClient, never())
                .preverRisco(any(), any());

        verify(analiseRiscoRepository, never())
                .save(any());

        verify(alertaService, never())
                .gerarParaAnalise(any(), any());
    }

    @Test
    void analiseAutomaticaNaoDeveQuebrarQuandoMachineLearningEstiverIndisponivel() {

        when(alunoRepository.findById(2))
                .thenReturn(Optional.of(aluno));

        when(notaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of(criarNota("8.00")));

        when(frequenciaRepository.findByMatriculaAlunoIdAluno(2))
                .thenReturn(List.of(criarFrequencia("90.00")));

        when(machineLearningClient.preverRisco(
                any(),
                any()
        )).thenThrow(
                new RegraNegocioException(
                        "Serviço de Machine Learning indisponível"
                )
        );

        assertDoesNotThrow(
                () -> analiseRiscoService
                        .analisarAutomaticamenteSePossivel(2)
        );

        verify(machineLearningClient)
                .preverRisco(
                        new BigDecimal("8.00"),
                        new BigDecimal("90.00")
                );

        verify(analiseRiscoRepository, never())
                .save(any());

        verify(alertaService, never())
                .gerarParaAnalise(any(), any());
    }

    private Nota criarNota(String valor) {

        Nota nota = new Nota();
        nota.setNota(new BigDecimal(valor));

        return nota;
    }

    private Frequencia criarFrequencia(String percentual) {

        Frequencia frequencia = new Frequencia();
        frequencia.setPercentual(
                new BigDecimal(percentual)
        );

        return frequencia;
    }
}