package com.guilherme.edusmart.repository;

import com.guilherme.edusmart.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    List<Matricula> findByAlunoIdAluno(Integer idAluno);

    List<Matricula> findByDisciplinaIdDisciplina(Integer idDisciplina);

    boolean existsByAlunoIdAlunoAndDisciplinaIdDisciplinaAndAnoAndSemestre(
            Integer idAluno,
            Integer idDisciplina,
            Integer ano,
            Integer semestre
    );
}