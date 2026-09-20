package com.guilherme.edusmart.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record NotaCadastroDTO(

        @NotNull
        Integer idMatricula,

        @NotNull
        @DecimalMin("0.00")
        @DecimalMax("10.00")
        BigDecimal nota,

        @Size(max = 50)
        String tipoAvaliacao

) {
}