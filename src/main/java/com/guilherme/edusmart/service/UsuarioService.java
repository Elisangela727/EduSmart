package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.UsuarioCadastroDTO;
import com.guilherme.edusmart.dto.UsuarioRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioRespostaDTO cadastrar(
            UsuarioCadastroDTO dto) {

        String email = dto.email()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException(
                    "E-mail já cadastrado"
            );
        }

        String tipoUsuario =
                validarTipoUsuario(dto.tipoUsuario());

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(
                passwordEncoder.encode(dto.senha())
        );
        usuario.setTipoUsuario(tipoUsuario);

        return converterParaDTO(
                usuarioRepository.save(usuario)
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioRespostaDTO> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioRespostaDTO buscarPorId(Integer id) {

        Usuario usuario =
                usuarioRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Usuário não encontrado"
                                )
                        );

        return converterParaDTO(usuario);
    }

    private String validarTipoUsuario(String tipo) {

        String tipoNormalizado =
                tipo.trim().toUpperCase();

        if (!tipoNormalizado.equals("ALUNO")
                && !tipoNormalizado.equals("PROFESSOR")
                && !tipoNormalizado.equals("ADMIN")) {

            throw new RegraNegocioException(
                    "Tipo de usuário inválido"
            );
        }

        return tipoNormalizado;
    }

    private UsuarioRespostaDTO converterParaDTO(
            Usuario usuario) {

        return new UsuarioRespostaDTO(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );
    }
}