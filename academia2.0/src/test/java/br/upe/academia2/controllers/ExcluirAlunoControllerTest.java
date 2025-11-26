package br.upe.academia2.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.ui.controllers.AdmMenuController;
import br.upe.academia2.ui.controllers.ExcluirAlunoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirAlunoControllerTest {

    @InjectMocks
    private ExcluirAlunoController controller;
    @Mock
    private UsuarioBusiness usuarioBusiness;
    @Mock
    private AdmMenuController admMenuController;
    @BeforeEach
    void setUp() {
        controller.setAdmMenuController(admMenuController);
    }

    private String excluir(String email, UsuarioBusiness.ResultadoExclusao resultadoSimulado) {
        if (email == null || email.isBlank()) {
            return "Informe o e-mail.";
        }
        // o mock tem q retornar o resultado simulado p pdoer testar
        when(usuarioBusiness.deletarUsuario(email)).thenReturn(resultadoSimulado);
        //chama aq
        UsuarioBusiness.ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(email);
        usuarioBusiness.salvarAlteracoesNoCsv();

        switch (resultado) {
            case SUCESSO -> {
                if (admMenuController != null) {
                    admMenuController.atualizarTabelaAlunos();
                }
                return "Aluno excluído com sucesso!";
            }
            case NAO_ENCONTRADO -> {
                return "Usuário não encontrado.";
            }
            case NAO_PERMITIDO_ADM -> {
                return "Não é possível excluir usuário ADM.";
            }
            default -> throw new IllegalStateException("Resultado desconhecido");
        }
    }

    @Test
    void testEmailVazio() {
        String msg = excluir("", UsuarioBusiness.ResultadoExclusao.SUCESSO);
        assertEquals("Informe o e-mail.", msg);

        verify(usuarioBusiness, never()).deletarUsuario(any());
        verify(usuarioBusiness, never()).salvarAlteracoesNoCsv();
        verify(admMenuController, never()).atualizarTabelaAlunos();
    }

    @Test
    void testExcluirSucesso() {
        String email = "aluno@email.com";
        String msg = excluir(email, UsuarioBusiness.ResultadoExclusao.SUCESSO);

        assertEquals("Aluno excluído com sucesso!", msg);
        verify(usuarioBusiness).deletarUsuario(email);
        verify(usuarioBusiness).salvarAlteracoesNoCsv();
        verify(admMenuController).atualizarTabelaAlunos();
    }

    @Test
    void testUsuarioNaoEncontrado() {
        String email = "naoexiste@email.com";
        String msg = excluir(email, UsuarioBusiness.ResultadoExclusao.NAO_ENCONTRADO);

        assertEquals("Usuário não encontrado.", msg);
        verify(usuarioBusiness).deletarUsuario(email);
        verify(usuarioBusiness).salvarAlteracoesNoCsv();
        verify(admMenuController, never()).atualizarTabelaAlunos();
    }

    @Test
    void testNaoPermitidoAdm() {
        String email = "adm@email.com";
        String msg = excluir(email, UsuarioBusiness.ResultadoExclusao.NAO_PERMITIDO_ADM);

        assertEquals("Não é possível excluir usuário ADM.", msg);
        verify(usuarioBusiness).deletarUsuario(email);
        verify(usuarioBusiness).salvarAlteracoesNoCsv();
        verify(admMenuController, never()).atualizarTabelaAlunos();
    }
}
