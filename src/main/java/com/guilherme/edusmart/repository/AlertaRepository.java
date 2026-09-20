package com.guilherme.edusmart.repository;

import com.guilherme.edusmart.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertaRepository extends JpaRepository<Alerta, Integer> {

    List<Alerta> findByAlunoIdAlunoOrderByDataAlertaDesc(Integer idAluno);

    List<Alerta> findByAlunoIdAlunoAndVisualizadoFalse(Integer idAluno);

    Optional<Alerta> findFirstByAlunoIdAlunoOrderByIdAlertaDesc(
            Integer idAluno
    );
}