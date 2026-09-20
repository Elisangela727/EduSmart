package com.guilherme.edusmart.repository;

import com.guilherme.edusmart.model.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Integer> {

    List<Frequencia> findByMatriculaIdMatricula(Integer idMatricula);

    List<Frequencia> findByMatriculaAlunoIdAluno(Integer idAluno);
}