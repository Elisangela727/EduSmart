package com.guilherme.edusmart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MachineLearningResponseDTO(
        @JsonProperty("nivel_risco")
        String nivelRisco
) {
}