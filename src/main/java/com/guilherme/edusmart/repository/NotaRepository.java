package com.guilherme.edusmart.repository;

import com.guilherme.edusmart.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Integer> {

    List<Nota> findByMatriculaIdMatricula(Integer idMatricula);

    List<Nota> findByMatriculaAlunoIdAluno(Integer idAluno);
}