package com.guilherme.edusmart.repository;

import com.guilherme.edusmart.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {
}