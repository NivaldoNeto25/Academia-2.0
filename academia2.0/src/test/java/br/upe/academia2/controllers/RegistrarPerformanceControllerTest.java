package br.upe.academia2.controllers;

import br.upe.academia2.business.SecaoTreinoBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.ui.controllers.RegistrarPerformanceController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
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
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
public class RegistrarPerformanceControllerTest extends ApplicationTest {

    @Mock
    private SecaoTreinoBusiness secaoTreinoBusinessMock;

    private RegistrarPerformanceController controller;
    private ItemPlanoTreino itemMock;
    private PlanoTreino planoMock;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegistrarPerformance.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        Field field = RegistrarPerformanceController.class.getDeclaredField("secaoTreinoBusiness");
        field.setAccessible(true);
        field.set(controller, secaoTreinoBusinessMock);
        
        setupDadosMocks();
        
        controller.setPlanoPai(planoMock);
        controller.setItemParaRegistrar(itemMock);

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void setupDadosMocks() {
        itemMock = mock(ItemPlanoTreino.class);
        Exercicio exercicioMock = mock(Exercicio.class);
        planoMock = mock(PlanoTreino.class);

        // Configura comportamento dos mocks
        when(exercicioMock.getNome()).thenReturn("Supino Reto");
        when(itemMock.getExercicio()).thenReturn(exercicioMock);
        when(itemMock.getSeries()).thenReturn(3);
        when(itemMock.getRepeticoes()).thenReturn(10);
        when(itemMock.getCarga()).thenReturn(50);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void devePreencherCamposAoInicializar() {
        verifyThat("#exercicioLabel", hasText("Exercício: Supino Reto"));
        verifyThat("#seriesPlanoLabel", hasText("3"));
        verifyThat("#repsPlanoLabel", hasText("10"));
        verifyThat("#cargaPlanoLabel", hasText("50"));

        // Verifica se os TextFields foram preenchidos com os valores padrão
        assertEquals("3", lookup("#seriesField").queryTextInputControl().getText());
        assertEquals("10", lookup("#repsField").queryTextInputControl().getText());
        assertEquals("50", lookup("#cargaField").queryTextInputControl().getText());
    }

    @Test
    public void deveSalvarSemAlteracoes() {
        // Ação: Clicar em salvar sem mudar nada
        clickOn("#btnSalvar");

        // Verificação: Deve mostrar alerta de sucesso simples (sem confirmação)
        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Performance registrada com sucesso!"));
        
        // Fecha o alerta
        type(KeyCode.ENTER);

        // Garante que o método de registrar performance (que atualiza o plano) NÃO foi chamado
        verify(secaoTreinoBusinessMock, never()).registrarPerformance(any(), any(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void deveSalvarComAlteracoesConfirmadas() {
        doubleClickOn("#seriesField").write("4");
        doubleClickOn("#repsField").write("12");
        doubleClickOn("#cargaField").write("60");

        clickOn("#btnSalvar");

        
        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Deseja atualizar o seu plano de treino com estes novos valores?"));
        
        
        type(KeyCode.ENTER);


        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Plano de treino atualizado com a nova performance!"));
        type(KeyCode.ENTER);

        // 3. Verifica chamada ao Business com os NOVOS valores
        verify(secaoTreinoBusinessMock).registrarPerformance(planoMock, itemMock, 60, 12, 4);
    }

    @Test
    public void deveNaoSalvarSeCancelarConfirmacao() {
        // Ação: Modificar valor
        doubleClickOn("#cargaField").write("100");

        clickOn("#btnSalvar");

        // Verifica Alerta de Confirmação
        verifyThat(".alert", isVisible());
        
        // Cancela (Pressiona ESC para fechar o diálogo sem confirmar)
        type(KeyCode.ESCAPE); 

        // Verifica que o business NUNCA foi chamado
        verify(secaoTreinoBusinessMock, never()).registrarPerformance(any(), any(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void deveMostrarErroSeEntradaInvalida() {
        // Ação: Digitar texto inválido
        doubleClickOn("#seriesField").write("abc");
        clickOn("#btnSalvar");

        // Verificação: Alerta de Erro
        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Por favor, insira apenas números válidos para séries, repetições e carga."));
        
        // Fecha alerta
        type(KeyCode.ENTER);
        
        // Garante que nada foi salvo
        verifyNoInteractions(secaoTreinoBusinessMock);
    }
}