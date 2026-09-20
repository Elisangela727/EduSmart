package com.guilherme.edusmart.dto;

public record AlunoRespostaDTO(
        Integer idAluno,
        Integer idUsuario,
        String nome,
        String email,
        String matricula,
        String curso,
        Integer semestre
) {
}