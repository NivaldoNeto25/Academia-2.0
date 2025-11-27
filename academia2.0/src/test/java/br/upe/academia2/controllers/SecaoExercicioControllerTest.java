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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
public class SecaoExercicioControllerTest extends ApplicationTest {

    private SecaoExercicioController controller;
    private PlanoTreino planoTeste;

    @Override
    public void start(Stage stage) throws Exception {
        // Assume que o arquivo FXML está em /fxml/SecaoExercicio.fxml
        // Ajuste o caminho se o nome do seu arquivo FXML for diferente
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SecaoExercicio.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // Configura os dados REAIS para teste (Beans)
        setupDadosTeste();

        // Injeta o plano no controlador (simulando a passagem da tela anterior)
        controller.setPlanoParaModificar(planoTeste);

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Cria uma estrutura de dados completa:
     * Plano -> Seção -> Item -> Exercício
     */
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

        // Associações
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
        // Verifica se o ComboBox foi preenchido com a seção criada no setup
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
        
        // Verifica se a célula renderizou o texto corretamente (formatado no controller)
        // O formato esperado é: "Nome (Séries x Reps, Carga: Xkg)"
        String textoEsperado = "Supino Reto (4 séries x 10 reps, Carga: 30kg)";
        
        // Nota: O TestFX verifica o texto visível na célula
        // Pode ser necessário mover o mouse para garantir renderização, mas geralmente funciona direto
        verifyThat(".list-cell", hasText(textoEsperado));
    }

    @Test
    public void deveExibirAlertaSeTentarEscolherSemSelecao() {
        // Ação 1: Selecionar Seção (para habilitar a lista)
        clickOn("#comboSecao").clickOn("Membros Superiores");

        // Ação 2: Clicar no botão "Escolher Exercício" SEM selecionar nada na lista
        clickOn("#btnEscolherExercicio");

        // Verificação: Alerta deve aparecer
        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Nenhum exercício foi selecionado da lista."));
        
        // Fecha o alerta
        type(KeyCode.ENTER);
    }

    @Test
    public void deveLimparListaSeSecaoForNula() {
        // Ação: Selecionar seção e depois limpar seleção (simulado programaticamente ou via UI se possível)
        clickOn("#comboSecao").clickOn("Membros Superiores");
        
        // Verifica que tem item
        ListView<ItemPlanoTreino> lista = lookup("#listaExercicios").queryListView();
        assertFalse(lista.getItems().isEmpty());

        // Ação: Simular mudança para nulo no ComboBox (difícil via UI pura sem botão de limpar, então fazemos via API do controle)
        interact(() -> {
            ComboBox<SecaoTreino> combo = lookup("#comboSecao").queryComboBox();
            combo.getSelectionModel().clearSelection();
        });

        // Verificação: Lista deve estar vazia
        assertEquals(0, lista.getItems().size());
    }
    
    // NOTA: Testar o clique de sucesso ("#btnEscolherExercicio" com item selecionado)
    // requer que o arquivo "RegistrarPerformance.fxml" exista e seja carregável.
    // Se o arquivo não existir no ambiente de teste, o teste falharia com IOException.
    // Abaixo está como seria esse teste se o ambiente estiver completo:
    
    /*
    @Test
    public void deveAbrirModalAoEscolherExercicio() {
        clickOn("#comboSecao").clickOn("Membros Superiores");
        clickOn("Supino Reto (4 séries x 10 reps, Carga: 30kg)"); // Clica na célula da lista
        clickOn("#btnEscolherExercicio");
        
        // Verifica se uma nova janela (Modal) abriu verificando o título ou um elemento dela
        // Exemplo: verifyThat("Registrar Performance", isVisible());
    }
    */
}