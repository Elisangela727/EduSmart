package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.LoginDTO;
import com.guilherme.edusmart.dto.LoginRespostaDTO;
import com.guilherme.edusmart.dto.UsuarioCadastroDTO;
import com.guilherme.edusmart.dto.UsuarioRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.UsuarioRepository;
import com.guilherme.edusmart.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    public LoginRespostaDTO login(LoginDTO dto) {

        String email =
                dto.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        dto.senha()
                )
        );

        Usuario usuario =
                usuarioRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Usuário não encontrado"
                                )
                        );

        String token =
                jwtService.gerarToken(usuario);

        return new LoginRespostaDTO(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );
    }

    public UsuarioRespostaDTO cadastrar(
            UsuarioCadastroDTO dto) {

        if (!"ALUNO".equalsIgnoreCase(dto.tipoUsuario())) {
            throw new RegraNegocioException(
                    "O cadastro público permite apenas usuários do tipo ALUNO"
            );
        }

        return usuarioService.cadastrar(dto);
    }
}