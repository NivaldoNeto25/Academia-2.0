package br.upe.academia2.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.AdmMenuController;
import br.upe.academia2.ui.controllers.ModificarAlunoController;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
class ModificarAlunoControllerTest extends ApplicationTest {

    @Mock
    private UsuarioBusiness usuarioBusinessMock;

    @Mock
    private AdmMenuController admMenuControllerMock;

    private ModificarAlunoController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModificarAluno.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // --- INJEÇÃO DE DEPENDÊNCIA VIA REFLEXÃO ---
        // Necessário pois 'usuarioBusiness' é instanciado internamente e é final
        Field field = ModificarAlunoController.class.getDeclaredField("usuarioBusiness");
        field.setAccessible(true);
        field.set(controller, usuarioBusinessMock);
        // -------------------------------------------

        // Configura o controlador pai mockado
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
    void deveExibirErroQuandoUsuarioNaoEncontrado() {
        // Cenário: A lista de usuários retornada é vazia ou não contém o email
        when(usuarioBusinessMock.listarUsuarios()).thenReturn(Collections.emptyList());

        // Ação
        clickOn("#emailField").write("emailnaoexiste@teste.com");
        clickOn("Modificar");

        // Verificação
        verifyThat("#mensagemLabel", hasText("Aluno não encontrado."));
        verify(usuarioBusinessMock, never()).atualizarUsuario(any());
    }

    @Test
    void deveModificarNomeESenhaComSucesso() {
        // Cenário: Mockando um usuário existente
        String emailAlvo = "existente@teste.com";
        Usuario usuarioMock = mock(Usuario.class);
        
        // Configura o comportamento do usuário mockado para ser encontrado pelo filtro stream()
        when(usuarioMock.getEmail()).thenReturn(emailAlvo);
        
        // O controller chama listarUsuarios() e filtra na memória
        when(usuarioBusinessMock.listarUsuarios()).thenReturn(List.of(usuarioMock));

        // Ação: Preencher os campos
        clickOn("#emailField").write(emailAlvo);
        clickOn("#nomeField").write("Novo Nome Teste");
        clickOn("#senhaField").write("NovaSenha123");
        clickOn("Modificar");

        // Verificações Lógicas:
        // 1. Verifica se os setters foram chamados no objeto usuário
        verify(usuarioMock).setNome("Novo Nome Teste");
        verify(usuarioMock).setSenha("NovaSenha123");

        // 2. Verifica se o business foi chamado para atualizar
        verify(usuarioBusinessMock).atualizarUsuario(usuarioMock);

        // 3. Verifica se a tabela do menu principal foi atualizada
        verify(admMenuControllerMock).atualizarTabelaAlunos();
    }

    @Test
    void deveModificarApenasNomeSeSenhaEstiverVazia() {
        // Cenário
        String emailAlvo = "apenasnome@teste.com";
        Usuario usuarioMock = mock(Usuario.class);
        when(usuarioMock.getEmail()).thenReturn(emailAlvo);
        when(usuarioBusinessMock.listarUsuarios()).thenReturn(List.of(usuarioMock));

        // Ação: Preencher apenas o nome, deixar senha vazia
        clickOn("#emailField").write(emailAlvo);
        clickOn("#nomeField").write("Somente Nome");
        // Não escreve nada no senhaField
        clickOn("Modificar");

        // Verificações
        verify(usuarioMock).setNome("Somente Nome");
        verify(usuarioMock, never()).setSenha(anyString()); // Senha não deve ser alterada
        verify(usuarioBusinessMock).atualizarUsuario(usuarioMock);
    }
}
