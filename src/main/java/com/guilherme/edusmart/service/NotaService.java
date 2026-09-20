package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.NotaCadastroDTO;
import com.guilherme.edusmart.dto.NotaRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.model.Matricula;
import com.guilherme.edusmart.model.Nota;
import com.guilherme.edusmart.repository.MatriculaRepository;
import com.guilherme.edusmart.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AnaliseRiscoService analiseRiscoService;

    public NotaService(
            NotaRepository notaRepository,
            MatriculaRepository matriculaRepository,
            AnaliseRiscoService analiseRiscoService) {

        this.notaRepository = notaRepository;
        this.matriculaRepository = matriculaRepository;
        this.analiseRiscoService = analiseRiscoService;
    }

    @Transactional
    public NotaRespostaDTO cadastrar(NotaCadastroDTO dto) {

        Matricula matricula = matriculaRepository.findById(dto.idMatricula())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Matrícula não encontrada"
                        )
                );

        Nota nota = new Nota();
        nota.setMatricula(matricula);
        nota.setNota(dto.nota());
        nota.setTipoAvaliacao(dto.tipoAvaliacao());

        Nota salva = notaRepository.save(nota);

        Integer idAluno =
                matricula.getAluno().getIdAluno();

        analiseRiscoService
                .analisarAutomaticamenteSePossivel(idAluno);

        return converterParaDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<NotaRespostaDTO> listarTodas() {

        return notaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotaRespostaDTO buscarPorId(Integer id) {

        Nota nota = notaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Nota não encontrada"
                        )
                );

        return converterParaDTO(nota);
    }

    @Transactional(readOnly = true)
    public List<NotaRespostaDTO> listarPorAluno(
            Integer idAluno) {

        return notaRepository
                .findByMatriculaAlunoIdAluno(idAluno)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    private NotaRespostaDTO converterParaDTO(
            Nota nota) {

        Matricula matricula =
                nota.getMatricula();

        return new NotaRespostaDTO(
                nota.getIdNota(),
                matricula.getIdMatricula(),
                matricula.getAluno().getIdAluno(),
                matricula.getAluno().getUsuario().getNome(),
                matricula.getDisciplina().getIdDisciplina(),
                matricula.getDisciplina().getNome(),
                nota.getNota(),
                nota.getTipoAvaliacao()
        );
    }
}