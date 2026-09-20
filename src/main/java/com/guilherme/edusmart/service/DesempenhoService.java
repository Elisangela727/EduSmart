package com.guilherme.edusmart.service;

import com.guilherme.edusmart.dto.DesempenhoAlunoDTO;
import com.guilherme.edusmart.dto.DisciplinaDesempenhoDTO;
import com.guilherme.edusmart.model.Aluno;
import com.guilherme.edusmart.model.AnaliseRisco;
import com.guilherme.edusmart.model.Frequencia;
import com.guilherme.edusmart.model.Matricula;
import com.guilherme.edusmart.model.Nota;
import com.guilherme.edusmart.repository.AnaliseRiscoRepository;
import com.guilherme.edusmart.repository.FrequenciaRepository;
import com.guilherme.edusmart.repository.MatriculaRepository;
import com.guilherme.edusmart.repository.NotaRepository;
import com.guilherme.edusmart.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class DesempenhoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final MatriculaRepository matriculaRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AnaliseRiscoRepository analiseRiscoRepository;

    public DesempenhoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            MatriculaRepository matriculaRepository,
            NotaRepository notaRepository,
            FrequenciaRepository frequenciaRepository,
            AnaliseRiscoRepository analiseRiscoRepository) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.matriculaRepository = matriculaRepository;
        this.notaRepository = notaRepository;
        this.frequenciaRepository = frequenciaRepository;
        this.analiseRiscoRepository = analiseRiscoRepository;
    }

    @Transactional(readOnly = true)
    public DesempenhoAlunoDTO buscarMeuDesempenho() {

        Aluno aluno = usuarioAutenticadoService.getAluno();

        List<Matricula> matriculas =
                matriculaRepository.findByAlunoIdAluno(aluno.getIdAluno());

        List<DisciplinaDesempenhoDTO> disciplinas =
                new ArrayList<>();

        List<BigDecimal> todasNotas =
                new ArrayList<>();

        List<BigDecimal> todasFrequencias =
                new ArrayList<>();

        for (Matricula matricula : matriculas) {

            List<Nota> notas =
                    notaRepository.findByMatriculaIdMatricula(
                            matricula.getIdMatricula()
                    );

            List<Frequencia> frequencias =
                    frequenciaRepository.findByMatriculaIdMatricula(
                            matricula.getIdMatricula()
                    );

            BigDecimal mediaNotas =
                    calcularMediaNotas(notas);

            BigDecimal mediaFrequencia =
                    calcularMediaFrequencias(frequencias);

            notas.stream()
                    .map(Nota::getNota)
                    .forEach(todasNotas::add);

            frequencias.stream()
                    .map(Frequencia::getPercentual)
                    .forEach(todasFrequencias::add);

            disciplinas.add(
                    new DisciplinaDesempenhoDTO(
                            matricula.getDisciplina().getIdDisciplina(),
                            matricula.getDisciplina().getNome(),
                            mediaNotas,
                            mediaFrequencia
                    )
            );
        }

        BigDecimal mediaGeral =
                calcularMedia(todasNotas);

        BigDecimal frequenciaMedia =
                calcularMedia(todasFrequencias);

        String nivelRisco =
                buscarUltimoNivelRisco(aluno.getIdAluno());

        return new DesempenhoAlunoDTO(
                aluno.getIdAluno(),
                aluno.getUsuario().getNome(),
                aluno.getMatricula(),
                aluno.getCurso(),
                aluno.getSemestre(),
                mediaGeral,
                frequenciaMedia,
                nivelRisco,
                disciplinas
        );
    }

    private BigDecimal calcularMediaNotas(
            List<Nota> notas) {

        return calcularMedia(
                notas.stream()
                        .map(Nota::getNota)
                        .toList()
        );
    }

    private BigDecimal calcularMediaFrequencias(
            List<Frequencia> frequencias) {

        return calcularMedia(
                frequencias.stream()
                        .map(Frequencia::getPercentual)
                        .toList()
        );
    }

    private BigDecimal calcularMedia(
            List<BigDecimal> valores) {

        if (valores.isEmpty()) {
            return BigDecimal.ZERO
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal soma =
                valores.stream()
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return soma.divide(
                BigDecimal.valueOf(valores.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    private String buscarUltimoNivelRisco(
            Integer idAluno) {

        List<AnaliseRisco> analises =
                analiseRiscoRepository
                        .findByAlunoIdAlunoOrderByDataAnaliseDesc(
                                idAluno
                        );

        if (analises.isEmpty()) {
            return "NAO_ANALISADO";
        }

        return analises.get(0).getNivelRisco();
    }
}