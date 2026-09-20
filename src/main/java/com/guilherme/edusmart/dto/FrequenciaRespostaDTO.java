package com.guilherme.edusmart.dto;

import java.math.BigDecimal;

public record FrequenciaRespostaDTO(
        Integer idFrequencia,
        Integer idMatricula,
        Integer idAluno,
        String nomeAluno,
        Integer idDisciplina,
        String nomeDisciplina,
        BigDecimal percentual,
        String periodo
) {
}