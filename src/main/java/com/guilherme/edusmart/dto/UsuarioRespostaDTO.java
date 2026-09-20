package com.guilherme.edusmart.dto;

public record UsuarioRespostaDTO(
        Integer idUsuario,
        String nome,
        String email,
        String tipoUsuario
) {
}