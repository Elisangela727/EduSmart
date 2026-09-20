package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.AnaliseRiscoRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.AnaliseRisco;
import com.guilherme.edusmart.model.Frequencia;
import com.guilherme.edusmart.model.Nota;
import com.guilherme.edusmart.repository.AlunoRepository;
import com.guilherme.edusmart.repository.AnaliseRiscoRepository;
import com.guilherme.edusmart.repository.FrequenciaRepository;
import com.guilherme.edusmart.repository.NotaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnaliseRiscoService {

    private static final Logger logger =
            LoggerFactory.getLogger(AnaliseRiscoService.class);

    private final AnaliseRiscoRepository analiseRiscoRepository;
    private final AlunoRepository alunoRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final MachineLearningClient machineLearningClient;
    private final AlertaService alertaService;

    public AnaliseRiscoService(
            AnaliseRiscoRepository analiseRiscoRepository,
            AlunoRepository alunoRepository,
            NotaRepository notaRepository,
            FrequenciaRepository frequenciaRepository,
            MachineLearningClient machineLearningClient,
            AlertaService alertaService) {

        this.analiseRiscoRepository = analiseRiscoRepository;
        this.alunoRepository = alunoRepository;
        this.notaRepository = notaRepository;
        this.frequenciaRepository = frequenciaRepository;
        this.machineLearningClient = machineLearningClient;
        this.alertaService = alertaService;
    }

    @Transactional
    public AnaliseRiscoRespostaDTO analisar(Integer idAluno) {

        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Aluno não encontrado"
                        )
                );

        List<Nota> notas =
                notaRepository.findByMatriculaAlunoIdAluno(idAluno);

        List<Frequencia> frequencias =
                frequenciaRepository.findByMatriculaAlunoIdAluno(idAluno);

        if (notas.isEmpty()) {
            throw new RegraNegocioException(
                    "Aluno não possui notas cadastradas"
            );
        }

        if (frequencias.isEmpty()) {
            throw new RegraNegocioException(
                    "Aluno não possui frequência cadastrada"
            );
        }

        return executarAnalise(
                aluno,
                notas,
                frequencias
        );
    }

    public void analisarAutomaticamenteSePossivel(
            Integer idAluno) {

        try {

            Aluno aluno = alunoRepository.findById(idAluno)
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Aluno não encontrado"
                            )
                    );

            List<Nota> notas =
                    notaRepository.findByMatriculaAlunoIdAluno(idAluno);

            List<Frequencia> frequencias =
                    frequenciaRepository.findByMatriculaAlunoIdAluno(idAluno);

            if (notas.isEmpty() || frequencias.isEmpty()) {
                return;
            }

            executarAnalise(
                    aluno,
                    notas,
                    frequencias
            );

        } catch (RegraNegocioException exception) {

            logger.warn(
                    "Não foi possível realizar a análise automática de risco do aluno {}: {}",
                    idAluno,
                    exception.getMessage()
            );

        } catch (Exception exception) {

            logger.error(
                    "Erro inesperado durante a análise automática de risco do aluno {}",
                    idAluno,
                    exception
            );
        }
    }

    private AnaliseRiscoRespostaDTO executarAnalise(
            Aluno aluno,
            List<Nota> notas,
            List<Frequencia> frequencias) {

        BigDecimal mediaNotas = notas.stream()
                .map(Nota::getNota)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(notas.size()),
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal mediaFrequencia = frequencias.stream()
                .map(Frequencia::getPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(frequencias.size()),
                        2,
                        RoundingMode.HALF_UP
                );

        String nivelRisco =
                machineLearningClient.preverRisco(
                        mediaNotas,
                        mediaFrequencia
                );

        AnaliseRisco analise =
                new AnaliseRisco();

        analise.setAluno(aluno);
        analise.setDataAnalise(LocalDate.now());
        analise.setNivelRisco(nivelRisco);
        analise.setProbabilidade(null);

        analise.setObservacao(
                "Análise realizada por Machine Learning"
                        + " | Média das notas: " + mediaNotas
                        + " | Frequência média: "
                        + mediaFrequencia + "%"
        );

        AnaliseRisco salva =
                analiseRiscoRepository.save(analise);

        alertaService.gerarParaAnalise(
                aluno,
                salva
        );

        return converterParaDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<AnaliseRiscoRespostaDTO> listarPorAluno(
            Integer idAluno) {

        if (!alunoRepository.existsById(idAluno)) {
            throw new RecursoNaoEncontradoException(
                    "Aluno não encontrado"
            );
        }

        return analiseRiscoRepository
                .findByAlunoIdAlunoOrderByDataAnaliseDesc(idAluno)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    private AnaliseRiscoRespostaDTO converterParaDTO(
            AnaliseRisco analise) {

        return new AnaliseRiscoRespostaDTO(
                analise.getIdAnalise(),
                analise.getAluno().getIdAluno(),
                analise.getAluno().getUsuario().getNome(),
                analise.getDataAnalise(),
                analise.getNivelRisco(),
                analise.getProbabilidade(),
                analise.getObservacao()
        );
    }
}