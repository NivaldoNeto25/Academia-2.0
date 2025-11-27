package br.upe.academia2.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.SecaoTreino;
import br.upe.academia2.ui.controllers.RemoverExercicioSecaoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
class RemoverExercicioSecaoControllerTest extends ApplicationTest {

    @Mock
    private PlanoTreinoBusiness planoTreinoBusinessMock;

    private RemoverExercicioSecaoController controller;
    private PlanoTreino planoReal;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RemoverExercicioSecao.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // --- INJEÇÃO DE DEPENDÊNCIA VIA REFLEXÃO ---
        Field field = RemoverExercicioSecaoController.class.getDeclaredField("planoTreinoBusiness");
        field.setAccessible(true);
        field.set(controller, planoTreinoBusinessMock);
        // -------------------------------------------

        // Configuração dos dados (Usando objetos reais para permitir manipulação de listas)
        setupDadosReais();
        
        // Passa o plano para o controlador
        controller.setPlanoParaModificar(planoReal);

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void setupDadosReais() {
        // Criação da hierarquia de dados: Plano -> Seção -> Item -> Exercício
        planoReal = new PlanoTreino();
        planoReal.setNomePlano("Hipertrofia Teste");

        SecaoTreino secao = new SecaoTreino();
        secao.setNomeTreino("Treino A - Peito");

        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Supino Reto");

        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(exercicio);

        // Importante: Inicializar listas como ArrayList mutáveis
        List<ItemPlanoTreino> itens = new ArrayList<>();
        itens.add(item);
        secao.setItensPlano(itens);

        List<SecaoTreino> secoes = new ArrayList<>();
        secoes.add(secao);
        planoReal.setSecoes(secoes);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void deveCarregarSecoesNoComboBox() {
        // Verifica se o ComboBox de seção foi preenchido corretamente pelo setPlanoParaModificar
        ComboBox<String> comboSecao = lookup("#secaoComboBox").queryComboBox();
        
        assertEquals(1, comboSecao.getItems().size());
        assertEquals("Treino A - Peito", comboSecao.getItems().get(0));
    }

    @Test
    public void deveCarregarExerciciosAoSelecionarSecao() {
        // Ação: Selecionar a seção no ComboBox
        clickOn("#secaoComboBox").clickOn("Treino A - Peito");

        // Verificação: O ComboBox de exercícios deve ser populado
        ComboBox<String> comboExercicios = lookup("#exerciciosComboBox").queryComboBox();
        
        assertEquals(1, comboExercicios.getItems().size());
        assertEquals("Supino Reto", comboExercicios.getItems().get(0));
    }

    @Test
    public void deveMostrarAlertaSeTentarRemoverSemSelecao() {
        // Ação: Clicar em Remover sem selecionar nada
        clickOn("#btnRemover");

        // Verificação: Alerta de Warning
        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Informe a seção e selecione um exercício para remover."));
        
        // Fecha alerta
        type(KeyCode.ENTER);
    }

    @Test
    public void deveRemoverExercicioComSucesso() {
        // Ação: Selecionar Seção e Exercício
        clickOn("#secaoComboBox").clickOn("Treino A - Peito");
        clickOn("#exerciciosComboBox").clickOn("Supino Reto");
        
        // Clicar em Remover
        clickOn("#btnRemover");

        // Verificação 1: Alerta de Sucesso
        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Exercício removido com sucesso!"));
        type(KeyCode.ENTER);

        // Verificação 2: Chamada ao Business para salvar
        verify(planoTreinoBusinessMock).modificarPlanoDeTreino(planoReal);

        // Verificação 3: O item foi removido da lista do objeto em memória?
        SecaoTreino secao = planoReal.getSecoes().get(0);
        assertTrue(secao.getItensPlano().isEmpty(), "A lista de itens deveria estar vazia após a remoção");
        
        // Verificação 4: UI atualizada (ComboBox de exercícios deve estar vazio agora)
        ComboBox<String> comboExercicios = lookup("#exerciciosComboBox").queryComboBox();
        assertTrue(comboExercicios.getItems().isEmpty(), "ComboBox deve esvaziar após remover o único item");
    }
}