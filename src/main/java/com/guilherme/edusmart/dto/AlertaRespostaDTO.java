package com.guilherme.edusmart.dto;

import java.time.LocalDate;

public record AlertaRespostaDTO(
        Integer idAlerta,
        Integer idAluno,
        String nomeAluno,
        String tipoAlerta,
        String mensagem,
        LocalDate dataAlerta,
        Boolean visualizado
) {
}