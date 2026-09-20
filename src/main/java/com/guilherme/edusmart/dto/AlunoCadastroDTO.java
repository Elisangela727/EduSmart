package com.guilherme.edusmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlunoCadastroDTO(

        @NotNull
        Integer idUsuario,

        @NotBlank
        @Size(max = 30)
        String matricula,

        @NotBlank
        @Size(max = 100)
        String curso,

        @NotNull
        Integer semestre

) {
}