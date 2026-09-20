package com.guilherme.edusmart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnaliseRiscoRespostaDTO(
        Integer idAnalise,
        Integer idAluno,
        String nomeAluno,
        LocalDate dataAnalise,
        String nivelRisco,
        BigDecimal probabilidade,
        String observacao
) {
}