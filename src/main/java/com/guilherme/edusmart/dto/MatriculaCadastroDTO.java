package com.guilherme.edusmart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MatriculaCadastroDTO(

        @NotNull
        Integer idAluno,

        @NotNull
        Integer idDisciplina,

        @NotNull
        @Min(2000)
        Integer ano,

        @NotNull
        @Min(1)
        @Max(2)
        Integer semestre

) {
}