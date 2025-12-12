package br.upe.academia2.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.*;
import br.upe.academia2.ui.controllers.PlanoTreinoAlunoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoControllerTest extends ApplicationTest {

    @Mock
    private PlanoTreinoBusiness planoTreinoBusinessMock;

    private PlanoTreinoAlunoController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlanoTreinoAluno.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        Field field = PlanoTreinoAlunoController.class.getDeclaredField("planoTreinoBusiness");
        field.setAccessible(true);
        field.set(controller, planoTreinoBusinessMock);

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
    void deveCarregarPlanosNoComboBoxAoSetarUsuario() {
        Usuario usuarioMock = mock(Usuario.class);
        PlanoTreino planoA = mock(PlanoTreino.class);
        when(planoA.getNomePlano()).thenReturn("Hipertrofia A");

        PlanoTreino planoB = mock(PlanoTreino.class);
        when(planoB.getNomePlano()).thenReturn("Cardio B");

        when(planoTreinoBusinessMock.listarPlanosPorUsuario(usuarioMock))
                .thenReturn(List.of(planoA, planoB));

        interact(() -> controller.setUsuario(usuarioMock));

        ComboBox<PlanoTreino> combo = lookup("#comboPlanos").queryComboBox();
        assertEquals(2, combo.getItems().size());
        assertEquals("Hipertrofia A", combo.getItems().get(0).getNomePlano());
        assertEquals("Cardio B", combo.getItems().get(1).getNomePlano());
    }

    @Test
    void devePreencherTabelaAoSelecionarPlano() {
        Usuario usuarioMock = mock(Usuario.class);

        Exercicio exercicioMock = mock(Exercicio.class);
        lenient().when(exercicioMock.getNome()).thenReturn("Supino Reto");

        ItemPlanoTreino itemMock = mock(ItemPlanoTreino.class);
        lenient().when(itemMock.getExercicio()).thenReturn(exercicioMock);
        // CORREÇÃO: lenient() para evitar erro se a tabela não renderizar essas colunas imediatamente
        lenient().when(itemMock.getSeries()).thenReturn(3);
        lenient().when(itemMock.getRepeticoes()).thenReturn(12);
        lenient().when(itemMock.getCarga()).thenReturn(20);

        SecaoTreino secaoMock = mock(SecaoTreino.class);
        lenient().when(secaoMock.getNomeTreino()).thenReturn("Treino A - Peito");
        lenient().when(secaoMock.getItensPlano()).thenReturn(List.of(itemMock));

        PlanoTreino planoMock = mock(PlanoTreino.class);
        when(planoMock.getNomePlano()).thenReturn("Meu Plano");
        when(planoMock.getItens()).thenReturn(List.of(itemMock));
        lenient().when(planoMock.getSecoes()).thenReturn(List.of(secaoMock));

        when(planoTreinoBusinessMock.listarPlanosPorUsuario(usuarioMock))
                .thenReturn(List.of(planoMock));

        interact(() -> controller.setUsuario(usuarioMock));

        clickOn("#comboPlanos").clickOn("Meu Plano");

        WaitForAsyncUtils.waitForFxEvents();

        TableView<ItemPlanoTreino> tabela = lookup("#tabelaExercicios").queryTableView();
        assertEquals(1, tabela.getItems().size());

        verifyThat(".table-cell", (javafx.scene.Node node) ->
                node instanceof javafx.scene.control.Labeled &&
                        ((javafx.scene.control.Labeled) node).getText().equals("Supino Reto"));
    }

    @Test
    void deveExibirAlertaAoTentarModificarSemSelecao() {
        clickOn("#btnModificar");

        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Por favor, selecione um plano de treino para modificar."));

        type(KeyCode.ENTER);
    }
}