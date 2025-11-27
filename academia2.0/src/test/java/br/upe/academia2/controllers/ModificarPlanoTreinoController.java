package br.upe.academia2.controllers;

import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.ui.controllers.ModificarPlanoTreinoController;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificarPlanoTreinoControllerTest {

    @Spy
    @InjectMocks
    private ModificarPlanoTreinoController controller;

    @Mock
    private PlanoTreino planoParaModificarMock;

    @Mock
    private Button btnVoltarMock;
    @Mock
    private Scene mockScene;
    @Mock
    private Stage mockStage;

    @BeforeAll
    public static void initJFX() {
        try {
            new javafx.embed.swing.JFXPanel();
        } catch (Exception ignored) {
            //o objetivo é apenas garantir a inicialização
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Injeta o mock do botão Voltar usando Reflection, já que o initialize() não é chamado pelo FXML
        Field btnVoltarField = ModificarPlanoTreinoController.class.getDeclaredField("btnVoltar");
        btnVoltarField.setAccessible(true);
        btnVoltarField.set(controller, btnVoltarMock);
    }


    @Test
    void setPlanoParaModificarTest() throws Exception {

        controller.setPlanoParaModificar(planoParaModificarMock);

        Field planoField = ModificarPlanoTreinoController.class.getDeclaredField("planoParaModificar");
        planoField.setAccessible(true);
        PlanoTreino value = (PlanoTreino) planoField.get(controller);

        assertEquals(planoParaModificarMock, value);
    }

    @Test
    void handleVoltarTest() {
        when(btnVoltarMock.getScene()).thenReturn(mockScene);
        when(mockScene.getWindow()).thenReturn(mockStage);

        controller.handleVoltar();

        verify(mockStage).close();
    }

    @Test
    void initializeTest() {
        Button btnAlterarNomeMock = mock(Button.class);
        Button btnAlterarDatasMock = mock(Button.class);
        Button btnAdicionarExercicioMock = mock(Button.class);
        Button btnRemoverExercicioMock = mock(Button.class);

        try {
            Field btnAlterarNomeField = ModificarPlanoTreinoController.class.getDeclaredField("btnAlterarNome");
            btnAlterarNomeField.setAccessible(true);
            btnAlterarNomeField.set(controller, btnAlterarNomeMock);

            Field btnAlterarDatasField = ModificarPlanoTreinoController.class.getDeclaredField("btnAlterarDatas");
            btnAlterarDatasField.setAccessible(true);
            btnAlterarDatasField.set(controller, btnAlterarDatasMock);

            Field btnAdicionarExercicioField = ModificarPlanoTreinoController.class.getDeclaredField("btnAdicionarExercicio");
            btnAdicionarExercicioField.setAccessible(true);
            btnAdicionarExercicioField.set(controller, btnAdicionarExercicioMock);

            Field btnRemoverExercicioField = ModificarPlanoTreinoController.class.getDeclaredField("btnRemoverExercicio");
            btnRemoverExercicioField.setAccessible(true);
            btnRemoverExercicioField.set(controller, btnRemoverExercicioMock);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao injetar campos mockados", e);
        }

        controller.initialize();

        verify(btnAlterarNomeMock).setOnAction(any());
        verify(btnAlterarDatasMock).setOnAction(any());
        verify(btnAdicionarExercicioMock).setOnAction(any());
        verify(btnRemoverExercicioMock).setOnAction(any());
    }

    @Test
    void abrirTelaAlterarNomePlanoTest() {
        doNothing().when(controller).abrirTela(anyString(), anyString());

        controller.abrirTelaAlterarNomePlano();

        verify(controller).abrirTela("/fxml/ModificarNomePlano.fxml", "Alterar Nome do Plano");
    }

    @Test
    void abrirTelaAlterarDatasPlanoTest() {
        doNothing().when(controller).abrirTela(anyString(), anyString());

        controller.abrirTelaAlterarDatasPlano();

        verify(controller).abrirTela("/fxml/AlterarDatas.fxml", "Alterar Datas do Plano");
    }

    @Test
    void abrirTelaAdicionarExercicioTest() {
        doNothing().when(controller).abrirTela(anyString(), anyString());

        controller.abrirTelaAdicionarExercicio();

        verify(controller).abrirTela("/fxml/AdicionarExercicioSecao.fxml", "Adicionar Exercício");
    }

    @Test
    void abrirTelaRemoverExercicioTest() {
        doNothing().when(controller).abrirTela(anyString(), anyString());

        controller.abrirTelaRemoverExercicio();
        verify(controller).abrirTela("/fxml/RemoverExercicioSecao.fxml", "Remover Exercício");
    }
}
