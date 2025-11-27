package br.upe.academia2.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.SecaoTreino;
import br.upe.academia2.ui.controllers.AdicionarExercicioSecaoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
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
        // 1. Instancia o controller injetando os mocks
        controller = new AdicionarExercicioSecaoController(mockPlanoBusiness, mockExercicioBusiness);

        // 2. Configura mocks necessários para a inicialização da tela
        // O controller chama listarExercicios no initialize()
        List<Exercicio> exerciciosFake = new ArrayList<>();
        exerciciosFake.add(new Exercicio("Supino Reto", "Peitoral", ""));
        exerciciosFake.add(new Exercicio("Agachamento", "Perna", ""));
        
        // Configura o comportamento do mock ANTES de carregar o FXML
        lenient().when(mockExercicioBusiness.listarExercicios()).thenReturn(exerciciosFake);

        // 3. Carrega o FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdicionarExercicioSecao.fxml"));
        loader.setController(controller);
        Parent root = loader.load();

        // 4. Passa o plano para o controller (simulando a navegação vinda da tela anterior)
        // Precisamos simular que o plano tem seções vazias ou preenchidas
        lenient().when(mockPlano.getSecoes()).thenReturn(new ArrayList<>());
        
        // Importante: Simula o método getOuCriarSecao do PlanoTreino, pois o controller o usa
        lenient().when(mockPlano.getOuCriarSecao(anyString())).thenReturn(new SecaoTreino("1", "Nova Secao", mockPlano));

        // Injeta o plano no controller
        controller.setPlanoParaModificar(mockPlano);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    @DisplayName("Deve popular a lista de exercícios corretamente ao iniciar")
    void testInicializacao_ListaExerciciosPopulada(FxRobot robot) {
        // Verifica se a ListView contém os exercícios mockados
        ListView<Exercicio> lista = robot.lookup("#exerciciosListView").queryListView();
        
        assertEquals(2, lista.getItems().size());
        assertEquals("Supino Reto", lista.getItems().get(0).getNome());
    }

    @Test
    @DisplayName("Deve exibir erro se tentar adicionar com campos vazios")
    void testAdicionarExercicio_CamposVazios_DeveFalhar(FxRobot robot) {
        // --- Act ---
        // Clica em Adicionar sem preencher nada
        robot.clickOn("#btnAdicionar");

        // --- Assert ---
        // Verifica se o Alerta apareceu (busca pelo título ou conteúdo do alerta)
        // O TestFX busca janelas modais automaticamente
        Label conteudoAlert = robot.lookup("Preencha todos os campos (seção, séries, repetições e carga).").queryAs(Label.class);
        assertNotNull(conteudoAlert);
        
        // Garante que o business NÃO foi chamado para salvar
        verify(mockPlanoBusiness, never()).modificarPlanoDeTreino(any());
        
        // Fecha o alerta para não travar outros testes (se rodar em suite)
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER); 
    }

    @Test
    @DisplayName("Deve adicionar exercício com sucesso quando tudo estiver preenchido")
    void testAdicionarExercicio_Sucesso(FxRobot robot) {
        // --- Arrange ---
        // Mock do getOuCriarSecao para garantir que retorna uma seção válida
        SecaoTreino secaoMock = new SecaoTreino("id1", "Hipertrofia", mockPlano);
        when(mockPlano.getOuCriarSecao("Hipertrofia")).thenReturn(secaoMock);

        // --- Act ---
        
        // 1. Preenche a Seção (ComboBox editável)
        robot.clickOn("#secaoComboBox").write("Hipertrofia");
        
        // 2. Seleciona um exercício na lista
        robot.clickOn("Supino Reto | Peitoral"); // Clica pelo texto da célula
        
        // 3. Preenche os campos numéricos
        robot.clickOn("#seriesField").write("4");
        robot.clickOn("#repeticoesField").write("12");
        robot.clickOn("#cargaField").write("30");
        
        // 4. Clica em Adicionar
        robot.clickOn("#btnAdicionar");

        // --- Assert ---
        
        // Verifica se o método de modificar o plano foi chamado
        verify(mockPlanoBusiness, times(1)).modificarPlanoDeTreino(mockPlano);
        
        // Verifica se apareceu a mensagem de sucesso
        Label mensagemSucesso = robot.lookup("Exercício adicionado à seção com sucesso!").queryAs(Label.class);
        assertNotNull(mensagemSucesso);
        
        // Fecha o alerta
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER); 
    }

    @Test
    @DisplayName("Deve exibir erro se selecionar exercício mas preencher dados inválidos")
    void testAdicionarExercicio_DadosInvalidos(FxRobot robot) {
        // --- Act ---
        robot.clickOn("#secaoComboBox").write("Treino A");
        robot.clickOn("Agachamento | Perna");
        
        robot.clickOn("#seriesField").write("abc"); // Texto em campo numérico
        robot.clickOn("#repeticoesField").write("10");
        robot.clickOn("#cargaField").write("20");
        
        robot.clickOn("#btnAdicionar");

        // --- Assert ---
        Label mensagemErro = robot.lookup("Digite valores numéricos válidos para séries, repetições e carga.").queryAs(Label.class);
        assertNotNull(mensagemErro);
        
        verify(mockPlanoBusiness, never()).modificarPlanoDeTreino(any());
        
        robot.press(KeyCode.ENTER).release(KeyCode.ENTER);
    }
}
