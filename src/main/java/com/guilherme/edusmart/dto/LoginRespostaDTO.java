package com.guilherme.edusmart.dto;

public record LoginRespostaDTO(
        String token,
        String tipo,
        Integer idUsuario,
        String nome,
        String email,
        String tipoUsuario
) {
}