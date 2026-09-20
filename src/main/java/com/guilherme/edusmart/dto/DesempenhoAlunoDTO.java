package com.guilherme.edusmart.dto;

import java.math.BigDecimal;
import java.util.List;

public record DesempenhoAlunoDTO(
        Integer idAluno,
        String nome,
        String matricula,
        String curso,
        Integer semestre,
        BigDecimal mediaGeral,
        BigDecimal frequenciaMedia,
        String nivelRisco,
        List<DisciplinaDesempenhoDTO> disciplinas
) {
}