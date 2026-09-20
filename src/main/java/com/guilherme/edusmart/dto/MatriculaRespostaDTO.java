package com.guilherme.edusmart.dto;

public record MatriculaRespostaDTO(
        Integer idMatricula,
        Integer idAluno,
        String nomeAluno,
        String matriculaAluno,
        Integer idDisciplina,
        String nomeDisciplina,
        Integer ano,
        Integer semestre
) {
}