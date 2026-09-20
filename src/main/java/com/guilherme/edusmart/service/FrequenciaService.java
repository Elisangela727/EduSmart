package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.FrequenciaCadastroDTO;
import com.guilherme.edusmart.dto.FrequenciaRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.model.Frequencia;
import com.guilherme.edusmart.model.Matricula;
import com.guilherme.edusmart.repository.FrequenciaRepository;
import com.guilherme.edusmart.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FrequenciaService {

    private final FrequenciaRepository frequenciaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AnaliseRiscoService analiseRiscoService;

    public FrequenciaService(
            FrequenciaRepository frequenciaRepository,
            MatriculaRepository matriculaRepository,
            AnaliseRiscoService analiseRiscoService) {

        this.frequenciaRepository = frequenciaRepository;
        this.matriculaRepository = matriculaRepository;
        this.analiseRiscoService = analiseRiscoService;
    }

    @Transactional
    public FrequenciaRespostaDTO cadastrar(
            FrequenciaCadastroDTO dto) {

        Matricula matricula =
                matriculaRepository.findById(dto.idMatricula())
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Matrícula não encontrada"
                                )
                        );

        Frequencia frequencia =
                new Frequencia();

        frequencia.setMatricula(matricula);
        frequencia.setPercentual(dto.percentual());
        frequencia.setPeriodo(dto.periodo());

        Frequencia salva =
                frequenciaRepository.save(frequencia);

        Integer idAluno =
                matricula.getAluno().getIdAluno();

        analiseRiscoService
                .analisarAutomaticamenteSePossivel(idAluno);

        return converterParaDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<FrequenciaRespostaDTO> listarTodas() {

        return frequenciaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FrequenciaRespostaDTO buscarPorId(
            Integer id) {

        Frequencia frequencia =
                frequenciaRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Frequência não encontrada"
                                )
                        );

        return converterParaDTO(frequencia);
    }

    @Transactional(readOnly = true)
    public List<FrequenciaRespostaDTO> listarPorAluno(
            Integer idAluno) {

        return frequenciaRepository
                .findByMatriculaAlunoIdAluno(idAluno)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    private FrequenciaRespostaDTO converterParaDTO(
            Frequencia frequencia) {

        Matricula matricula =
                frequencia.getMatricula();

        return new FrequenciaRespostaDTO(
                frequencia.getIdFrequencia(),
                matricula.getIdMatricula(),
                matricula.getAluno().getIdAluno(),
                matricula.getAluno().getUsuario().getNome(),
                matricula.getDisciplina().getIdDisciplina(),
                matricula.getDisciplina().getNome(),
                frequencia.getPercentual(),
                frequencia.getPeriodo()
        );
    }
}