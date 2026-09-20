package com.guilherme.edusmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlunoCadastroCompletoDTO(

        @NotBlank
        @Size(max = 100)
        String nome,

        @NotBlank
        @Email
        @Size(max = 100)
        String email,

        @NotBlank
        @Size(min = 6, max = 100)
        String senha,

        @NotBlank
        @Size(max = 30)
        String matricula,

        @NotBlank
        @Size(max = 100)
        String curso,

        @NotNull
        @Min(1)
        @Max(20)
        Integer semestre

) {
}