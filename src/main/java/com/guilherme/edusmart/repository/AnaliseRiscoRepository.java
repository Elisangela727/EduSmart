package com.guilherme.edusmart.repository;

import com.guilherme.edusmart.model.AnaliseRisco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnaliseRiscoRepository extends JpaRepository<AnaliseRisco, Integer> {

    List<AnaliseRisco> findByAlunoIdAlunoOrderByDataAnaliseDesc(Integer idAluno);
}