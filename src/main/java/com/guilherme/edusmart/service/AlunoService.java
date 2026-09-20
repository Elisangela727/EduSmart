package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.AlunoCadastroCompletoDTO;
import com.guilherme.edusmart.dto.AlunoCadastroDTO;
import com.guilherme.edusmart.dto.AlunoRespostaDTO;
import com.guilherme.edusmart.dto.UsuarioCadastroDTO;
import com.guilherme.edusmart.dto.UsuarioRespostaDTO;
import com.guilherme.edusmart.exception.RecursoNaoEncontradoException;
import com.guilherme.edusmart.exception.RegraNegocioException;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.Usuario;
import com.guilherme.edusmart.repository.AlunoRepository;
import com.guilherme.edusmart.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AlunoService(
            AlunoRepository alunoRepository,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService) {

        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AlunoRespostaDTO cadastrarCompleto(
            AlunoCadastroCompletoDTO dto) {

        String email = dto.email()
                .trim()
                .toLowerCase();

        String matricula = dto.matricula().trim();

        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException(
                    "E-mail já cadastrado"
            );
        }

        if (alunoRepository.existsByMatricula(matricula)) {
            throw new RegraNegocioException(
                    "Matrícula já cadastrada"
            );
        }

        UsuarioCadastroDTO usuarioDTO =
                new UsuarioCadastroDTO(
                        dto.nome().trim(),
                        email,
                        dto.senha(),
                        "ALUNO"
                );

        UsuarioRespostaDTO usuarioCriado =
                usuarioService.cadastrar(usuarioDTO);

        AlunoCadastroDTO alunoDTO =
                new AlunoCadastroDTO(
                        usuarioCriado.idUsuario(),
                        matricula,
                        dto.curso().trim(),
                        dto.semestre()
                );

        return cadastrar(alunoDTO);
    }

    @Transactional
    public AlunoRespostaDTO cadastrar(AlunoCadastroDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        if (!"ALUNO".equalsIgnoreCase(usuario.getTipoUsuario())) {
            throw new RegraNegocioException(
                    "O usuário informado não possui perfil ALUNO"
            );
        }

        if (alunoRepository
                .findByUsuarioIdUsuario(dto.idUsuario())
                .isPresent()) {

            throw new RegraNegocioException(
                    "Este usuário já possui um cadastro de aluno"
            );
        }

        if (alunoRepository.existsByMatricula(dto.matricula())) {
            throw new RegraNegocioException(
                    "Matrícula já cadastrada"
            );
        }

        Aluno aluno = new Aluno();
        aluno.setUsuario(usuario);
        aluno.setMatricula(dto.matricula().trim());
        aluno.setCurso(dto.curso().trim());
        aluno.setSemestre(dto.semestre());

        Aluno salvo = alunoRepository.save(aluno);

        return converterParaDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<AlunoRespostaDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlunoRespostaDTO buscarPorId(Integer id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Aluno não encontrado"
                        )
                );

        return converterParaDTO(aluno);
    }

    @Transactional(readOnly = true)
    public AlunoRespostaDTO buscarPorMatricula(
            String matricula) {

        Aluno aluno = alunoRepository
                .findByMatricula(matricula)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Aluno não encontrado"
                        )
                );

        return converterParaDTO(aluno);
    }

    private AlunoRespostaDTO converterParaDTO(
            Aluno aluno) {

        Usuario usuario = aluno.getUsuario();

        return new AlunoRespostaDTO(
                aluno.getIdAluno(),
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                aluno.getMatricula(),
                aluno.getCurso(),
                aluno.getSemestre()
        );
    }
}