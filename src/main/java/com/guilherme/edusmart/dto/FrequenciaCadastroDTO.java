package com.guilherme.edusmart.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FrequenciaCadastroDTO(

        @NotNull
        Integer idMatricula,

        @NotNull
        @DecimalMin("0.00")
        @DecimalMax("100.00")
        BigDecimal percentual,

        @Size(max = 30)
        String periodo

) {
}