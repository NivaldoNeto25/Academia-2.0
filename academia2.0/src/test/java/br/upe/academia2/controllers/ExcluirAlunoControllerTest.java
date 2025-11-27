package br.upe.academia2.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.business.UsuarioBusiness.ResultadoExclusao;
import br.upe.academia2.ui.controllers.AdmMenuController;
import br.upe.academia2.ui.controllers.ExcluirAlunoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
class ExcluirAlunoControllerTest extends ApplicationTest {

    @Mock
    private UsuarioBusiness usuarioBusinessMock;

    @Mock
    private AdmMenuController admMenuControllerMock;

    private ExcluirAlunoController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExcluirAluno.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // INJEÇÃO DE DEPENDÊNCIA VIA REFLEXÃO
        // Necessário porque o campo usuarioBusiness é 'private final' e instanciado internamente
        Field field = ExcluirAlunoController.class.getDeclaredField("usuarioBusiness");
        field.setAccessible(true);
        field.set(controller, usuarioBusinessMock);

        // Configura o controller pai (mockado)
        controller.setAdmMenuController(admMenuControllerMock);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @AfterEach
    void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    void deveExibirErroQuandoEmailEstiverVazio() {
        // Ação: Clicar no botão excluir sem digitar nada
        clickOn("Excluir");

        // Verificação: Label deve mostrar mensagem de erro
        verifyThat("#mensagemLabel", hasText("Informe o e-mail."));
        
        // Garante que o business não foi chamado
        Mockito.verifyNoInteractions(usuarioBusinessMock);
    }

    @Test
    void deveExibirErroQuandoUsuarioNaoEncontrado() {
        // Cenário
        String emailInexistente = "fantasma@teste.com";
        when(usuarioBusinessMock.deletarUsuario(emailInexistente))
                .thenReturn(ResultadoExclusao.NAO_ENCONTRADO);

        // Ação
        clickOn("#emailField").write(emailInexistente);
        clickOn("Excluir");

        // Verificação
        verifyThat("#mensagemLabel", hasText("Usuário não encontrado."));
    }

    @Test
    void deveExibirErroQuandoTentarExcluirAdm() {
        // Cenário
        String emailAdm = "admin@teste.com";
        when(usuarioBusinessMock.deletarUsuario(emailAdm))
                .thenReturn(ResultadoExclusao.NAO_PERMITIDO_ADM);

        // Ação
        clickOn("#emailField").write(emailAdm);
        clickOn("Excluir");

        // Verificação
        verifyThat("#mensagemLabel", hasText("Não é possível excluir usuário ADM."));
    }

    @Test
    void deveExcluirComSucessoEAtualizarTabela() {
        // Cenário
        String emailValido = "aluno@teste.com";
        when(usuarioBusinessMock.deletarUsuario(emailValido))
                .thenReturn(ResultadoExclusao.SUCESSO);

        // Ação
        clickOn("#emailField").write(emailValido);
        clickOn("Excluir");

        // Verificação Visual (Label atualizada antes de fechar, embora seja rápido)
        // Nota: O teste pode ser rápido demais para pegar o label antes do close(), 
        // mas verificamos as chamadas de método.
        
        // Verificação Lógica:
        // 1. Verifica se o método de deletar foi chamado no Business
        verify(usuarioBusinessMock).deletarUsuario(emailValido);
        
        // 2. Verifica se a tabela do menu anterior foi atualizada
        verify(admMenuControllerMock).atualizarTabelaAlunos();
    }
}