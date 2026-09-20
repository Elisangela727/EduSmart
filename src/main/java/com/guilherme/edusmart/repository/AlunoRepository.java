package com.guilherme.edusmart.repository;

import com.guilherme.edusmart.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    Optional<Aluno> findByMatricula(String matricula);

    Optional<Aluno> findByUsuarioIdUsuario(Integer idUsuario);

    boolean existsByMatricula(String matricula);
}