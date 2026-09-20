package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.AlertaRespostaDTO;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Alerta;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.AnaliseRisco;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.AlertaRepository;
import com.guilherme.edusmart.repository.AlunoRepository;
import com.guilherme.edusmart.repository.AnaliseRiscoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AnaliseRiscoRepository analiseRiscoRepository;

    @InjectMocks
    private AlertaService alertaService;

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
    void deveGerarAlertaDeRiscoAlto() {

        AnaliseRisco analise = new AnaliseRisco();
        analise.setIdAnalise(10);
        analise.setAluno(aluno);
        analise.setDataAnalise(LocalDate.now());
        analise.setNivelRisco("ALTO");

        when(
                alertaRepository
                        .findFirstByAlunoIdAlunoOrderByIdAlertaDesc(2)
        ).thenReturn(Optional.empty());

        when(alertaRepository.save(any(Alerta.class)))
                .thenAnswer(invocation -> {
                    Alerta alerta = invocation.getArgument(0);
                    alerta.setIdAlerta(20);
                    return alerta;
                });

        AlertaRespostaDTO resposta =
                alertaService.gerarParaAnalise(
                        aluno,
                        analise
                );

        assertEquals(20, resposta.idAlerta());
        assertEquals(2, resposta.idAluno());
        assertEquals("Aluno Teste", resposta.nomeAluno());
        assertEquals("RISCO_ALTO", resposta.tipoAlerta());
        assertFalse(resposta.visualizado());

        verify(alertaRepository)
                .save(any(Alerta.class));
    }

    @Test
    void naoDeveCriarAlertaDuplicadoNaoVisualizado() {

        AnaliseRisco analise = new AnaliseRisco();
        analise.setAluno(aluno);
        analise.setDataAnalise(LocalDate.now());
        analise.setNivelRisco("BAIXO");

        Alerta alertaExistente = new Alerta();
        alertaExistente.setIdAlerta(20);
        alertaExistente.setAluno(aluno);
        alertaExistente.setTipoAlerta("RISCO_BAIXO");
        alertaExistente.setMensagem(
                "Seu desempenho acadêmico está adequado no momento. " +
                        "Continue acompanhando suas notas e frequência."
        );
        alertaExistente.setDataAlerta(LocalDate.now());
        alertaExistente.setVisualizado(false);

        when(
                alertaRepository
                        .findFirstByAlunoIdAlunoOrderByIdAlertaDesc(2)
        ).thenReturn(Optional.of(alertaExistente));

        AlertaRespostaDTO resposta =
                alertaService.gerarParaAnalise(
                        aluno,
                        analise
                );

        assertEquals(20, resposta.idAlerta());
        assertEquals("RISCO_BAIXO", resposta.tipoAlerta());
        assertFalse(resposta.visualizado());

        verify(alertaRepository, never())
                .save(any(Alerta.class));
    }

    @Test
    void alunoNaoDeveVisualizarAlertaDeOutroAluno() {

        Aluno outroAluno = new Aluno();
        outroAluno.setIdAluno(3);

        Alerta alerta = new Alerta();
        alerta.setIdAlerta(30);
        alerta.setAluno(outroAluno);
        alerta.setTipoAlerta("RISCO_ALTO");
        alerta.setMensagem("Alerta de outro aluno");
        alerta.setDataAlerta(LocalDate.now());
        alerta.setVisualizado(false);

        when(alertaRepository.findById(30))
                .thenReturn(Optional.of(alerta));

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> alertaService
                                .marcarComoVisualizadoPeloAluno(
                                        30,
                                        2
                                )
                );

        assertEquals(
                "O aluno não possui permissão para alterar este alerta",
                exception.getMessage()
        );

        assertFalse(alerta.getVisualizado());

        verify(alertaRepository, never())
                .save(any(Alerta.class));
    }

    @Test
    void alunoDeveVisualizarSeuProprioAlerta() {

        Alerta alerta = new Alerta();
        alerta.setIdAlerta(20);
        alerta.setAluno(aluno);
        alerta.setTipoAlerta("RISCO_BAIXO");
        alerta.setMensagem("Desempenho adequado");
        alerta.setDataAlerta(LocalDate.now());
        alerta.setVisualizado(false);

        when(alertaRepository.findById(20))
                .thenReturn(Optional.of(alerta));

        when(alertaRepository.save(alerta))
                .thenReturn(alerta);

        AlertaRespostaDTO resposta =
                alertaService
                        .marcarComoVisualizadoPeloAluno(
                                20,
                                2
                        );

        assertEquals(true, resposta.visualizado());

        verify(alertaRepository)
                .save(alerta);
    }
}