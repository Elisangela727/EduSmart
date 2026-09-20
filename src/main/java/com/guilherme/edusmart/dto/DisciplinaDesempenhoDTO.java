package com.guilherme.edusmart.dto;

import java.math.BigDecimal;

public record DisciplinaDesempenhoDTO(
        Integer idDisciplina,
        String nomeDisciplina,
        BigDecimal mediaNotas,
        BigDecimal frequencia
) {
}