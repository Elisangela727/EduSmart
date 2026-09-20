package com.guilherme.edusmart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record MachineLearningRequestDTO(
        @JsonProperty("media_notas")
        BigDecimal mediaNotas,

        @JsonProperty("frequencia")
        BigDecimal frequencia
) {
}