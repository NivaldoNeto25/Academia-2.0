package br.upe.academia2.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.RelatoriosAlunoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RelatoriosAlunoControllerTest extends ApplicationTest {

    @Mock
    private IndicadorBiomedicoBusiness indicadorBusinessMock;

    private RelatoriosAlunoController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RelatorioAluno.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // Injeção de dependência via reflexão (campo 'indicadorBusiness')
        try {
            Field field = RelatoriosAlunoController.class.getDeclaredField("indicadorBusiness");
            field.setAccessible(true);
            field.set(controller, indicadorBusinessMock);
        } catch (NoSuchFieldException e) {
            // Fallback caso o nome seja antigo
            Field field = RelatoriosAlunoController.class.getDeclaredField("repo");
            field.setAccessible(true);
            field.set(controller, indicadorBusinessMock);
        }

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
    public void deveExibirErroSeUsuarioNaoEstiverLogado() {
        clickOn("Relatório Geral");
        
        // Espera a UI atualizar
        WaitForAsyncUtils.waitForFxEvents();

        TextArea txtSaida = lookup("#txtSaida").query();
        assertTrue(txtSaida.getText().contains("Usuário não definido"));
    }

    @Test
    public void deveExibirMensagemVaziaSeNaoHouverIndicadoresGeral() {
        Usuario usuarioMock = mock(Usuario.class);
        
        // Garante execução na Thread JavaFX
        interact(() -> controller.setUsuario(usuarioMock));
        
        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(Collections.emptyList());

        clickOn("Relatório Geral");
        
        WaitForAsyncUtils.waitForFxEvents();

        TextArea txtSaida = lookup("#txtSaida").query();
        assertTrue(txtSaida.getText().contains("Nenhum indicador encontrado"));
    }

    @Test
    public void deveGerarRelatorioGeralComDados() {
        Usuario usuarioMock = mock(Usuario.class);
        interact(() -> controller.setUsuario(usuarioMock));

        IndicadorBiomedico ind1 = new IndicadorBiomedico(70.0, 1.75, 15.0, 45.0, 22.8, new Date());
        IndicadorBiomedico ind2 = new IndicadorBiomedico(72.5, 1.75, 14.0, 46.0, 23.5, new Date());
        
        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(List.of(ind1, ind2));

        clickOn("Relatório Geral");
        
        WaitForAsyncUtils.waitForFxEvents();

        TextArea txtSaida = lookup("#txtSaida").query();
        String texto = txtSaida.getText();

        assertTrue(texto.contains("Relatório Geral gerado com sucesso!"));
        
        // CORREÇÃO: Usar String.format para garantir compatibilidade com Locale (Ponto ou Vírgula)
        String esperado1 = String.format("Peso: %.2f kg", 70.0);
        String esperado2 = String.format("Peso: %.2f kg", 72.5);

        assertTrue(texto.contains(esperado1), "Esperado encontrar: " + esperado1 + " | Texto real: " + texto);
        assertTrue(texto.contains(esperado2), "Esperado encontrar: " + esperado2 + " | Texto real: " + texto);
    }

    @Test
    public void deveExibirMensagemSePoucosDadosParaComparativo() {
        Usuario usuarioMock = mock(Usuario.class);
        interact(() -> controller.setUsuario(usuarioMock));
        
        IndicadorBiomedico ind1 = new IndicadorBiomedico(80.0, 1.80, 20.0, 40.0, 24.7, new Date());
        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(List.of(ind1));

        clickOn("Relatório Comparativo");
        
        WaitForAsyncUtils.waitForFxEvents();

        TextArea txtSaida = lookup("#txtSaida").query();
        String texto = txtSaida.getText();
        
        assertTrue(texto.contains("Apenas um registro encontrado"));
        
        // Correção Locale
        String esperado = String.format("Peso: %.2f kg", 80.0);
        assertTrue(texto.contains(esperado), "Esperado: " + esperado);
    }

    @Test
    public void deveGerarRelatorioComparativoPrimeiroEUltimo() {
        Usuario usuarioMock = mock(Usuario.class);
        interact(() -> controller.setUsuario(usuarioMock));

        IndicadorBiomedico primeiro = new IndicadorBiomedico(100.0, 1.80, 30.0, 40.0, 30.8, new Date());
        IndicadorBiomedico meio = new IndicadorBiomedico(95.0, 1.80, 28.0, 42.0, 29.3, new Date());
        IndicadorBiomedico ultimo = new IndicadorBiomedico(90.0, 1.80, 25.0, 45.0, 27.7, new Date());

        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(List.of(primeiro, meio, ultimo));

        clickOn("Relatório Comparativo");
        
        WaitForAsyncUtils.waitForFxEvents();

        TextArea txtSaida = lookup("#txtSaida").query();
        String texto = txtSaida.getText();

        assertTrue(texto.contains("Comparando o primeiro e o último"));
        assertTrue(texto.contains("Primeiro registro:"));
        assertTrue(texto.contains("Último registro:"));

        // Correção Locale
        String esperadoPrimeiro = String.format("Peso: %.2f kg", 100.0);
        String esperadoUltimo = String.format("Peso: %.2f kg", 90.0);

        assertTrue(texto.contains(esperadoPrimeiro), "Deveria conter dados do primeiro: " + esperadoPrimeiro); 
        assertTrue(texto.contains(esperadoUltimo), "Deveria conter dados do último: " + esperadoUltimo);
    }
}