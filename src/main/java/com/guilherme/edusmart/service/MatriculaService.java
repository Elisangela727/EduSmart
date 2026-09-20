package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.MatriculaCadastroDTO;
import com.guilherme.edusmart.dto.MatriculaRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.Disciplina;
import com.guilherme.edusmart.model.Matricula;
import com.guilherme.edusmart.repository.AlunoRepository;
import com.guilherme.edusmart.repository.DisciplinaRepository;
import com.guilherme.edusmart.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            AlunoRepository alunoRepository,
            DisciplinaRepository disciplinaRepository) {

        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public MatriculaRespostaDTO cadastrar(MatriculaCadastroDTO dto) {

        Aluno aluno = alunoRepository.findById(dto.idAluno())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Aluno não encontrado")
                );

        Disciplina disciplina = disciplinaRepository.findById(dto.idDisciplina())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Disciplina não encontrada")
                );

        boolean existente =
                matriculaRepository
                        .existsByAlunoIdAlunoAndDisciplinaIdDisciplinaAndAnoAndSemestre(
                                dto.idAluno(),
                                dto.idDisciplina(),
                                dto.ano(),
                                dto.semestre()
                        );

        if (existente) {
            throw new RegraNegocioException(
                    "Aluno já matriculado nesta disciplina no período informado"
            );
        }

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setDisciplina(disciplina);
        matricula.setAno(dto.ano());
        matricula.setSemestre(dto.semestre());

        Matricula salva = matriculaRepository.save(matricula);

        return converterParaDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<MatriculaRespostaDTO> listarTodas() {
        return matriculaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatriculaRespostaDTO buscarPorId(Integer id) {

        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Matrícula não encontrada")
                );

        return converterParaDTO(matricula);
    }

    @Transactional(readOnly = true)
    public List<MatriculaRespostaDTO> listarPorAluno(Integer idAluno) {

        if (!alunoRepository.existsById(idAluno)) {
            throw new RecursoNaoEncontradoException("Aluno não encontrado");
        }

        return matriculaRepository.findByAlunoIdAluno(idAluno)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    private MatriculaRespostaDTO converterParaDTO(Matricula matricula) {

        return new MatriculaRespostaDTO(
                matricula.getIdMatricula(),
                matricula.getAluno().getIdAluno(),
                matricula.getAluno().getUsuario().getNome(),
                matricula.getAluno().getMatricula(),
                matricula.getDisciplina().getIdDisciplina(),
                matricula.getDisciplina().getNome(),
                matricula.getAno(),
                matricula.getSemestre()
        );
    }
}