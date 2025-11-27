package br.upe.academia2.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.*;
import br.upe.academia2.ui.controllers.PlanoTreinoAlunoController;
import javafx.collections.FXCollections;
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
public class PlanoTreinoControllerTest extends ApplicationTest {

    @Mock
    private PlanoTreinoBusiness planoTreinoBusinessMock;

    private PlanoTreinoAlunoController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlanoTreinoAluno.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // --- INJEÇÃO DE DEPENDÊNCIA VIA REFLEXÃO ---
        // Substitui a instância real criada no initialize() pelo Mock
        Field field = PlanoTreinoAlunoController.class.getDeclaredField("planoTreinoBusiness");
        field.setAccessible(true);
        field.set(controller, planoTreinoBusinessMock);
        // -------------------------------------------

        stage.setScene(new Scene(root));
        stage.show();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void deveCarregarPlanosNoComboBoxAoSetarUsuario() {
        // Cenário
        Usuario usuarioMock = mock(Usuario.class);
        PlanoTreino planoA = mock(PlanoTreino.class);
        when(planoA.getNomePlano()).thenReturn("Hipertrofia A");
        
        PlanoTreino planoB = mock(PlanoTreino.class);
        when(planoB.getNomePlano()).thenReturn("Cardio B");

        when(planoTreinoBusinessMock.listarPlanosPorUsuario(usuarioMock))
                .thenReturn(List.of(planoA, planoB));

        // Ação: Setar o usuário (que dispara o carregamento)
        // Usamos interact para garantir execução na Thread JavaFX
        interact(() -> controller.setUsuario(usuarioMock));

        // Verificação
        ComboBox<PlanoTreino> combo = lookup("#comboPlanos").queryComboBox();
        assertEquals(2, combo.getItems().size());
        assertEquals("Hipertrofia A", combo.getItems().get(0).getNomePlano());
        assertEquals("Cardio B", combo.getItems().get(1).getNomePlano());
    }

    @Test
    public void devePreencherTabelaAoSelecionarPlano() {
        // --- CONFIGURAÇÃO DE DADOS MOCKADOS ---
        Usuario usuarioMock = mock(Usuario.class);
        
        // Exercicio
        Exercicio exercicioMock = mock(Exercicio.class);
        when(exercicioMock.getNome()).thenReturn("Supino Reto");

        // Item do Plano
        ItemPlanoTreino itemMock = mock(ItemPlanoTreino.class);
        when(itemMock.getExercicio()).thenReturn(exercicioMock);
        when(itemMock.getSeries()).thenReturn(3);
        when(itemMock.getRepeticoes()).thenReturn(12);
        when(itemMock.getCarga()).thenReturn(20);

        // Seção (Necessário para a lógica da coluna 'Seção' no Controller)
        SecaoTreino secaoMock = mock(SecaoTreino.class);
        when(secaoMock.getNomeTreino()).thenReturn("Treino A - Peito");
        when(secaoMock.getItensPlano()).thenReturn(List.of(itemMock));

        // Plano
        PlanoTreino planoMock = mock(PlanoTreino.class);
        when(planoMock.getNomePlano()).thenReturn("Meu Plano");
        when(planoMock.getItens()).thenReturn(List.of(itemMock));
        when(planoMock.getSecoes()).thenReturn(List.of(secaoMock));

        // Configurar retorno do business
        when(planoTreinoBusinessMock.listarPlanosPorUsuario(usuarioMock))
                .thenReturn(List.of(planoMock));

        // --- AÇÃO ---
        interact(() -> controller.setUsuario(usuarioMock));
        
        // Seleciona o plano no ComboBox
        clickOn("#comboPlanos").clickOn("Meu Plano");

        // --- VERIFICAÇÃO ---
        TableView<ItemPlanoTreino> tabela = lookup("#tabelaExercicios").queryTableView();
        
        // Espera a UI atualizar
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, tabela.getItems().size());
        
        // Verifica se a célula da primeira linha contém os dados esperados
        // Nota: TestFX verifica o que está visível na tela (Cell)
        // Pode ser necessário mover o mouse se a tabela for grande, mas aqui é apenas 1 item.
        
        // Verifica nome do exercício
        verifyThat(".table-cell", (javafx.scene.Node node) -> 
            node instanceof javafx.scene.control.Labeled && 
            ((javafx.scene.control.Labeled) node).getText().equals("Supino Reto"));
            
        // Verifica a seção (Lógica complexa do controller)
        // O controller itera sobre seções para achar o nome
        // Se a mockagem da SecaoTreino falhar, aqui apareceria "N/A"
    }

    @Test
    void deveExibirAlertaAoTentarModificarSemSelecao() {
        // Ação: Clicar em Modificar sem selecionar nada no ComboBox
        clickOn("#btnModificar");

        // Verificação: O TestFX detecta a janela de Alerta modal
        // O título do alerta definido no controller é "Nenhum Plano Selecionado"
        verifyThat(".alert", isVisible());
        verifyThat(".dialog-pane .content", hasText("Por favor, selecione um plano de treino para modificar."));
        
        // Fecha o alerta para não travar outros testes (pressionando OK/Enter)
        type(KeyCode.ENTER);
    }
}