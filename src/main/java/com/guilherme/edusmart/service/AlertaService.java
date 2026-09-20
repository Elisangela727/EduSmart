package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.AlertaRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Alerta;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.AnaliseRisco;
import com.guilherme.edusmart.repository.AlertaRepository;
import com.guilherme.edusmart.repository.AlunoRepository;
import com.guilherme.edusmart.repository.AnaliseRiscoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final AlunoRepository alunoRepository;
    private final AnaliseRiscoRepository analiseRiscoRepository;

    public AlertaService(
            AlertaRepository alertaRepository,
            AlunoRepository alunoRepository,
            AnaliseRiscoRepository analiseRiscoRepository) {

        this.alertaRepository = alertaRepository;
        this.alunoRepository = alunoRepository;
        this.analiseRiscoRepository = analiseRiscoRepository;
    }

    @Transactional
    public AlertaRespostaDTO gerar(Integer idAluno) {

        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Aluno não encontrado"
                        )
                );

        List<AnaliseRisco> analises =
                analiseRiscoRepository
                        .findByAlunoIdAlunoOrderByDataAnaliseDesc(idAluno);

        if (analises.isEmpty()) {
            throw new RegraNegocioException(
                    "Aluno ainda não possui análise de risco"
            );
        }

        return gerarParaAnalise(
                aluno,
                analises.get(0)
        );
    }

    @Transactional
    public AlertaRespostaDTO gerarParaAnalise(
            Aluno aluno,
            AnaliseRisco analise) {

        String tipoAlerta;
        String mensagem;

        switch (analise.getNivelRisco().toUpperCase()) {

            case "ALTO" -> {
                tipoAlerta = "RISCO_ALTO";
                mensagem =
                        "Atenção: foi identificado alto risco acadêmico. " +
                                "Recomenda-se acompanhamento do desempenho e da frequência.";
            }

            case "MEDIO" -> {
                tipoAlerta = "RISCO_MEDIO";
                mensagem =
                        "Atenção: seu desempenho acadêmico requer acompanhamento. " +
                                "Verifique suas notas e frequência.";
            }

            case "BAIXO" -> {
                tipoAlerta = "RISCO_BAIXO";
                mensagem =
                        "Seu desempenho acadêmico está adequado no momento. " +
                                "Continue acompanhando suas notas e frequência.";
            }

            default -> throw new RegraNegocioException(
                    "Nível de risco inválido"
            );
        }

        Optional<Alerta> ultimoAlerta =
                alertaRepository
                        .findFirstByAlunoIdAlunoOrderByIdAlertaDesc(
                                aluno.getIdAluno()
                        );

        if (ultimoAlerta.isPresent()
                && tipoAlerta.equals(
                ultimoAlerta.get().getTipoAlerta()
        )
                && !ultimoAlerta.get().getVisualizado()) {

            return converterParaDTO(
                    ultimoAlerta.get()
            );
        }

        Alerta alerta = new Alerta();

        alerta.setAluno(aluno);
        alerta.setTipoAlerta(tipoAlerta);
        alerta.setMensagem(mensagem);
        alerta.setDataAlerta(LocalDate.now());
        alerta.setVisualizado(false);

        Alerta salvo =
                alertaRepository.save(alerta);

        return converterParaDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<AlertaRespostaDTO> listarPorAluno(
            Integer idAluno) {

        if (!alunoRepository.existsById(idAluno)) {
            throw new RecursoNaoEncontradoException(
                    "Aluno não encontrado"
            );
        }

        return alertaRepository
                .findByAlunoIdAlunoOrderByDataAlertaDesc(idAluno)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlertaRespostaDTO> listarNaoVisualizados(
            Integer idAluno) {

        if (!alunoRepository.existsById(idAluno)) {
            throw new RecursoNaoEncontradoException(
                    "Aluno não encontrado"
            );
        }

        return alertaRepository
                .findByAlunoIdAlunoAndVisualizadoFalse(idAluno)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional
    public AlertaRespostaDTO marcarComoVisualizado(
            Integer idAlerta) {

        Alerta alerta = buscarAlerta(idAlerta);

        alerta.setVisualizado(true);

        return converterParaDTO(
                alertaRepository.save(alerta)
        );
    }

    @Transactional
    public AlertaRespostaDTO marcarComoVisualizadoPeloAluno(
            Integer idAlerta,
            Integer idAlunoAutenticado) {

        Alerta alerta = buscarAlerta(idAlerta);

        Integer idDonoAlerta =
                alerta.getAluno().getIdAluno();

        if (!idDonoAlerta.equals(idAlunoAutenticado)) {
            throw new RegraNegocioException(
                    "O aluno não possui permissão para alterar este alerta"
            );
        }

        alerta.setVisualizado(true);

        return converterParaDTO(
                alertaRepository.save(alerta)
        );
    }

    private Alerta buscarAlerta(Integer idAlerta) {

        return alertaRepository.findById(idAlerta)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Alerta não encontrado"
                        )
                );
    }

    private AlertaRespostaDTO converterParaDTO(
            Alerta alerta) {

        return new AlertaRespostaDTO(
                alerta.getIdAlerta(),
                alerta.getAluno().getIdAluno(),
                alerta.getAluno().getUsuario().getNome(),
                alerta.getTipoAlerta(),
                alerta.getMensagem(),
                alerta.getDataAlerta(),
                alerta.getVisualizado()
        );
    }
}