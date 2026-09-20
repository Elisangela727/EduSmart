package com.guilherme.edusmart.security;

import com.guilherme.edusmart.config.SecurityConfig;
import com.guilherme.edusmart.controller.AlunoController;
import com.guilherme.edusmart.service.AlunoService;
import com.guilherme.edusmart.service.DesempenhoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlunoController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlunoService alunoService;

    @MockitoBean
    private DesempenhoService desempenhoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveRetornar401QuandoNaoEstiverAutenticado() throws Exception {

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "aluno@edusmart.com",
            roles = "ALUNO"
    )
    void alunoNaoDeveAcessarListaDeAlunos() throws Exception {

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@edusmart.com",
            roles = "ADMIN"
    )
    void adminDeveAcessarListaDeAlunos() throws Exception {

        when(alunoService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk());
    }
}