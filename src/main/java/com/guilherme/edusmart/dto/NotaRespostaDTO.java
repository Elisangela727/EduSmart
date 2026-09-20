package com.guilherme.edusmart.dto;

import java.math.BigDecimal;

public record NotaRespostaDTO(
        Integer idNota,
        Integer idMatricula,
        Integer idAluno,
        String nomeAluno,
        Integer idDisciplina,
        String nomeDisciplina,
        BigDecimal nota,
        String tipoAvaliacao
) {
}