package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.MachineLearningRequestDTO;
import com.guilherme.edusmart.dto.MachineLearningResponseDTO;
import com.guilherme.edusmart.exception.RegraNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

@Service
public class MachineLearningClient {

    private final RestClient restClient;

    public MachineLearningClient(
            @Value("${ml.service.url}") String mlServiceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(mlServiceUrl)
                .build();
    }

    public String preverRisco(
            BigDecimal mediaNotas,
            BigDecimal frequencia) {

        MachineLearningRequestDTO request =
                new MachineLearningRequestDTO(
                        mediaNotas,
                        frequencia
                );

        try {

            MachineLearningResponseDTO response =
                    restClient.post()
                            .uri("/predict")
                            .body(request)
                            .retrieve()
                            .body(MachineLearningResponseDTO.class);

            if (response == null
                    || response.nivelRisco() == null
                    || response.nivelRisco().isBlank()) {

                throw new RegraNegocioException(
                        "Serviço de Machine Learning retornou uma resposta inválida"
                );
            }

            String nivelRisco =
                    response.nivelRisco()
                            .trim()
                            .toUpperCase();

            if (!nivelRisco.equals("BAIXO")
                    && !nivelRisco.equals("MEDIO")
                    && !nivelRisco.equals("ALTO")) {

                throw new RegraNegocioException(
                        "Serviço de Machine Learning retornou um nível de risco inválido"
                );
            }

            return nivelRisco;

        } catch (RestClientException exception) {

            throw new RegraNegocioException(
                    "Serviço de Machine Learning indisponível"
            );
        }
    }
}