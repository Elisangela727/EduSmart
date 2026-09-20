package com.guilherme.edusmart.security;

import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.AlunoRepository;
import com.guilherme.edusmart.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;

    public UsuarioAutenticadoService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository) {

        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional(readOnly = true)
    public Usuario getUsuario() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new RegraNegocioException(
                    "Usuário não autenticado"
            );
        }

        return usuarioRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário autenticado não encontrado"
                        )
                );
    }

    @Transactional(readOnly = true)
    public Aluno getAluno() {

        Usuario usuario = getUsuario();

        if (!"ALUNO".equalsIgnoreCase(usuario.getTipoUsuario())) {
            throw new RegraNegocioException(
                    "O usuário autenticado não possui perfil ALUNO"
            );
        }

        return alunoRepository
                .findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Cadastro de aluno não encontrado para o usuário autenticado"
                        )
                );
    }

    @Transactional(readOnly = true)
    public Integer getIdAluno() {
        return getAluno().getIdAluno();
    }
}