package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.DisciplinaCadastroDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.model.Disciplina;
import com.guilherme.edusmart.repository.DisciplinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public Disciplina cadastrar(DisciplinaCadastroDTO dto) {
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(dto.nome());
        disciplina.setCargaHoraria(dto.cargaHoraria());

        return disciplinaRepository.save(disciplina);
    }

    @Transactional(readOnly = true)
    public List<Disciplina> listarTodas() {
        return disciplinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Disciplina buscarPorId(Integer id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Disciplina não encontrada")
                );
    }
}