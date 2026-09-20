package com.guilherme.edusmart.security;

import com.guilherme.edusmart.config.SecurityConfig;
import com.guilherme.edusmart.controller.NotaController;
import com.guilherme.edusmart.service.NotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotaController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
class NotaSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotaService notaService;

    @MockitoBean
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    @WithMockUser(
            username = "aluno@edusmart.com",
            roles = "ALUNO"
    )
    void alunoDeveAcessarSuasPropriasNotas() throws Exception {

        when(usuarioAutenticadoService.getIdAluno())
                .thenReturn(2);

        when(notaService.listarPorAluno(2))
                .thenReturn(List.of());

        mockMvc.perform(get("/notas/minhas"))
                .andExpect(status().isOk());

        verify(usuarioAutenticadoService)
                .getIdAluno();

        verify(notaService)
                .listarPorAluno(2);
    }

    @Test
    @WithMockUser(
            username = "aluno@edusmart.com",
            roles = "ALUNO"
    )
    void alunoNaoDeveConsultarNotasDeOutroAlunoPorId() throws Exception {

        mockMvc.perform(get("/notas/aluno/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@edusmart.com",
            roles = "ADMIN"
    )
    void adminDeveConsultarNotasDeAlunoPorId() throws Exception {

        when(notaService.listarPorAluno(2))
                .thenReturn(List.of());

        mockMvc.perform(get("/notas/aluno/2"))
                .andExpect(status().isOk());

        verify(notaService)
                .listarPorAluno(2);
    }
}