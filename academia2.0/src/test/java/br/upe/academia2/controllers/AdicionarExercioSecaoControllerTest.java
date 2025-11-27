package br.upe.academia2.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.SecaoTreino;
import br.upe.academia2.ui.controllers.AdicionarExercicioSecaoController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class AdicionarExercicioSecaoControllerTest {

    @Mock
    private PlanoTreinoBusiness mockPlanoBusiness;
    @Mock
    private ExercicioBusiness mockExercicioBusiness;
    @Mock
    private PlanoTreino mockPlano;

    private AdicionarExercicioSecaoController controller;

    @Start
    private void start(Stage stage) throws Exception {
        // Inicializa mocks
        MockitoAnnotations.openMocks(this);

        // --- Configura Dados dos Mocks ---
        List<Exercicio> exerciciosFake = new ArrayList<>();
        // O texto na tela será "Supino Reto | Peitoral"
        exerciciosFake.add(new Exercicio("Supino Reto", "Peitoral", ""));
        // O texto na tela será "Agachamento | Perna"
        exerciciosFake.add(new Exercicio("Agachamento", "Perna", ""));

        lenient().when(mockExercicioBusiness.listarExercicios()).thenReturn(exerciciosFake);
        lenient().when(mockPlano.getSecoes()).thenReturn(new ArrayList<>());
        lenient().when(mockPlano.getOuCriarSecao(anyString())).thenReturn(new SecaoTreino("1", "Nova Secao", mockPlano));

        // --- Carrega FXML ---
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdicionarExercicioSecao.fxml"));

        loader.setControllerFactory(clazz -> {
            if (AdicionarExercicioSecaoController.class.isAssignableFrom(clazz)) {
                controller = new AdicionarExercicioSecaoController(mockPlanoBusiness, mockExercicioBusiness);
                return controller;
            }
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();

        Platform.runLater(() -> {
            if (controller != null) {
                controller.setPlanoParaModificar(mockPlano);
            }
        });

        stage.setScene(new Scene(root));
        stage.show();
    }

    @AfterEach
    void tearDown(FxRobot robot) throws Exception {
        FxToolkit.hideStage();
        robot.release(new KeyCode[]{});
        robot.release(new MouseButton[]{});
    }

    @Test
    @DisplayName("Deve popular a lista de exercícios corretamente ao iniciar")
    void testInicializacao_ListaExerciciosPopulada(FxRobot robot) {
        ListView<Exercicio> lista = robot.lookup("#exerciciosListView").queryListView();
        assertEquals(2, lista.getItems().size());
        assertEquals("Supino Reto", lista.getItems().get(0).getNome());
    }

    @Test
    @DisplayName("Deve exibir erro se tentar adicionar com campos vazios")
    void testAdicionarExercicio_CamposVazios_DeveFalhar(FxRobot robot) {
        robot.clickOn("#btnAdicionar");

        Label conteudoAlert = robot.lookup("Preencha todos os campos (seção, séries, repetições e carga).").queryAs(Label.class);
        assertNotNull(conteudoAlert);
        
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER); 
    }

    @Test
    @DisplayName("Deve adicionar exercício com sucesso")
    void testAdicionarExercicio_Sucesso(FxRobot robot) {
        SecaoTreino secaoMock = new SecaoTreino("id1", "Hipertrofia", mockPlano);
        when(mockPlano.getOuCriarSecao("Hipertrofia")).thenReturn(secaoMock);

        robot.clickOn("#secaoComboBox").write("Hipertrofia");
        
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("Supino Reto | Peitoral"); 

        robot.clickOn("#seriesField").write("4");
        robot.clickOn("#repeticoesField").write("12");
        robot.clickOn("#cargaField").write("30");
        
        robot.clickOn("#btnAdicionar");

        verify(mockPlanoBusiness, times(1)).modificarPlanoDeTreino(mockPlano);
        
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER); 
    }

    @Test
    @DisplayName("Deve exibir erro se dados inválidos")
    void testAdicionarExercicio_DadosInvalidos(FxRobot robot) {
        robot.clickOn("#secaoComboBox").write("Treino A");
        
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("Agachamento | Perna"); 
        
        robot.clickOn("#seriesField").write("abc");
        robot.clickOn("#repeticoesField").write("10");
        robot.clickOn("#cargaField").write("20");
        
        robot.clickOn("#btnAdicionar");

        Label mensagemErro = robot.lookup("Digite valores numéricos válidos para séries, repetições e carga.").queryAs(Label.class);
        assertNotNull(mensagemErro);
        
        verify(mockPlanoBusiness, never()).modificarPlanoDeTreino(any());
        
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
    }
}