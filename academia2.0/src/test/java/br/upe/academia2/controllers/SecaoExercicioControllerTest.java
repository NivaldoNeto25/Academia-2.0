package br.upe.academia2.controllers;

import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.SecaoTreino;
import br.upe.academia2.ui.controllers.SecaoExercicioController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
public class SecaoExercicioControllerTest extends ApplicationTest {

    private SecaoExercicioController controller;
    private PlanoTreino planoTeste;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SecaoExercicio.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        setupDadosTeste();
        controller.setPlanoParaModificar(planoTeste);

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void setupDadosTeste() {
        planoTeste = new PlanoTreino();
        planoTeste.setNomePlano("Treino Full Body");

        SecaoTreino secaoA = new SecaoTreino();
        secaoA.setNomeTreino("Membros Superiores");

        Exercicio supino = new Exercicio();
        supino.setNome("Supino Reto");

        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(supino);
        item.setSeries(4);
        item.setRepeticoes(10);
        item.setCarga(30);

        List<ItemPlanoTreino> itens = new ArrayList<>();
        itens.add(item);
        secaoA.setItensPlano(itens);

        List<SecaoTreino> secoes = new ArrayList<>();
        secoes.add(secaoA);
        planoTeste.setSecoes(secoes);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void deveCarregarSecoesNoComboBox() {
        ComboBox<SecaoTreino> combo = lookup("#comboSecao").queryComboBox();
        assertEquals(1, combo.getItems().size());
        assertEquals("Membros Superiores", combo.getItems().get(0).getNomeTreino());
    }

    @Test
    public void deveCarregarExerciciosNaListaAoSelecionarSecao() {
        // Ação: Selecionar a seção no ComboBox
        clickOn("#comboSecao").clickOn("Membros Superiores");

        // Verificação: A ListView deve mostrar o exercício
        ListView<ItemPlanoTreino> lista = lookup("#listaExercicios").queryListView();
        assertEquals(1, lista.getItems().size());
        
        
        
        String textoEsperado = "Supino Reto (4 séries x 10 reps, Carga: 30kg)";
        
        Set<ListCell> celulas = lookup(".list-cell").queryAllAs(ListCell.class);
        
        boolean encontrou = celulas.stream()
                .anyMatch(cell -> cell.getText() != null && cell.getText().contains("Supino Reto"));
        
        assertTrue(encontrou, "Deveria ter encontrado uma célula com o texto do exercício");
    }

    @Test
    public void deveExibirAlertaSeTentarEscolherSemSelecao() {
        clickOn("#comboSecao").clickOn("Membros Superiores");
        
        // Clica no botão sem selecionar nada na lista
        clickOn("#btnEscolherExercicio");

        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Nenhum exercício foi selecionado da lista."));
        type(KeyCode.ENTER);
    }

    @Test
    public void deveLimparListaSeSecaoForNula() {
        clickOn("#comboSecao").clickOn("Membros Superiores");
        
        ListView<ItemPlanoTreino> lista = lookup("#listaExercicios").queryListView();
        assertFalse(lista.getItems().isEmpty());

        interact(() -> {
            ComboBox<SecaoTreino> combo = lookup("#comboSecao").queryComboBox();
            combo.getSelectionModel().clearSelection();
        });

        assertEquals(0, lista.getItems().size());
    }
}