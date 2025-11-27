package br.upe.academia2.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.*;
import br.upe.academia2.ui.controllers.SecaoTreinoController;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@ExtendWith(MockitoExtension.class)
public class SecaoTreinoControllerTest extends ApplicationTest {

    @Mock
    private PlanoTreinoBusiness planoTreinoBusinessMock;

    private SecaoTreinoController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SecaoTreino.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        Field field = SecaoTreinoController.class.getDeclaredField("planoTreinoBusiness");
        field.setAccessible(true);
        field.set(controller, planoTreinoBusinessMock);

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
    public void deveIdentificarNomeDaSecaoNaTabela() {
        Usuario usuarioMock = mock(Usuario.class);
        
        // --- ARRANGE COM OBJETOS REAIS (BEANS) ---
        // Usamos objetos reais para garantir que o método .contains() da lista funcione corretamente
        // na lógica do controller (comparação de referências ou equals).
        
        Exercicio ex = new Exercicio();
        ex.setNome("Supino");
        
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(ex);
        item.setSeries(4);
        item.setRepeticoes(10);
        item.setCarga(50);
        
        // Lista única compartilhada
        List<ItemPlanoTreino> listaItens = new ArrayList<>();
        listaItens.add(item);
        
        SecaoTreino secao = new SecaoTreino();
        secao.setNomeTreino("Treino A - Peito");
        secao.setItensPlano(listaItens); // Seção contém o item
        
        PlanoTreino plano = new PlanoTreino();
        plano.setNomePlano("Meu Plano Completo");
        plano.setSecoes(List.of(secao));
        //plano.setItens(listaItens); // Plano contém o MESMO item na lista geral

        // Mock do business retornando esse plano complexo
        when(planoTreinoBusinessMock.listarPlanosPorUsuario(any())).thenReturn(List.of(plano));

        // --- ACT ---
        interact(() -> controller.setUsuario(usuarioMock));
        
        WaitForAsyncUtils.waitForFxEvents(); // Espera UI

        // Abre ComboBox e seleciona
        clickOn("#comboPlanos");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Meu Plano Completo");
        
        WaitForAsyncUtils.waitForFxEvents(); // Espera Tabela Atualizar

        // --- ASSERT ---
        // verifyThat procura na cena inteira. Se a string existir em alguma célula, passa.
        // O erro anterior "was Supino" indicava que ele pegava o primeiro Label que achava.
        // Usamos hasText para garantir que ALGUM nó visível tem esse texto.
        verifyThat("Treino A - Peito", (javafx.scene.Node node) -> node.isVisible());
    }
}