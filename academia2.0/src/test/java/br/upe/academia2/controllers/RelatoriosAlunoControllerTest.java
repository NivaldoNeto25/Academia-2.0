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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

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

        // --- INJEÇÃO DE DEPENDÊNCIA VIA REFLEXÃO ---
        // Necessário pois 'indicadorBusiness' é private final e instanciado internamente
        Field field = RelatoriosAlunoController.class.getDeclaredField("indicadorBusiness");
        field.setAccessible(true);
        field.set(controller, indicadorBusinessMock);
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
    public void deveExibirErroSeUsuarioNaoEstiverLogado() {
        // Ação: Clicar no botão sem definir usuário (controller.usuario é null)
        clickOn("Relatório Geral");

        // Verificação
        TextArea txtSaida = lookup("#txtSaida").query();
        assertTrue(txtSaida.getText().contains("Usuário não definido"));
    }

    @Test
    public void deveExibirMensagemVaziaSeNaoHouverIndicadoresGeral() {
        // Cenário
        Usuario usuarioMock = mock(Usuario.class);
        controller.setUsuario(usuarioMock);
        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(Collections.emptyList());

        // Ação
        clickOn("Relatório Geral");

        // Verificação
        TextArea txtSaida = lookup("#txtSaida").query();
        assertTrue(txtSaida.getText().contains("Nenhum indicador encontrado"));
    }

    @Test
    public void deveGerarRelatorioGeralComDados() {
        // Cenário
        Usuario usuarioMock = mock(Usuario.class);
        controller.setUsuario(usuarioMock);

        IndicadorBiomedico ind1 = new IndicadorBiomedico(70.0, 1.75, 15.0, 45.0, 22.8, new Date());
        IndicadorBiomedico ind2 = new IndicadorBiomedico(72.0, 1.75, 14.0, 46.0, 23.5, new Date());
        
        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(List.of(ind1, ind2));

        // Ação
        clickOn("Relatório Geral");

        // Verificação
        TextArea txtSaida = lookup("#txtSaida").query();
        String texto = txtSaida.getText();

        assertTrue(texto.contains("Relatório Geral gerado com sucesso!"));
        assertTrue(texto.contains("Peso: 70,00 kg")); // Verifica dado do 1º
        assertTrue(texto.contains("Peso: 72,00 kg")); // Verifica dado do 2º
    }

    @Test
    public void deveExibirMensagemSePoucosDadosParaComparativo() {
        // Cenário: Apenas 1 registro
        Usuario usuarioMock = mock(Usuario.class);
        controller.setUsuario(usuarioMock);
        
        IndicadorBiomedico ind1 = new IndicadorBiomedico(80.0, 1.80, 20.0, 40.0, 24.7, new Date());
        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(List.of(ind1));

        // Ação
        clickOn("Relatório Comparativo");

        // Verificação
        TextArea txtSaida = lookup("#txtSaida").query();
        assertTrue(txtSaida.getText().contains("Apenas um registro encontrado"));
        assertTrue(txtSaida.getText().contains("Peso: 80,00 kg"));
    }

    @Test
    public void deveGerarRelatorioComparativoPrimeiroEUltimo() {
        // Cenário: 3 registros, deve pegar o primeiro e o terceiro
        Usuario usuarioMock = mock(Usuario.class);
        controller.setUsuario(usuarioMock);

        IndicadorBiomedico primeiro = new IndicadorBiomedico(100.0, 1.80, 30.0, 40.0, 30.8, new Date());
        IndicadorBiomedico meio = new IndicadorBiomedico(95.0, 1.80, 28.0, 42.0, 29.3, new Date());
        IndicadorBiomedico ultimo = new IndicadorBiomedico(90.0, 1.80, 25.0, 45.0, 27.7, new Date());

        // A lista deve estar ordenada como viria do banco/business
        when(indicadorBusinessMock.listarIndicadores(usuarioMock)).thenReturn(List.of(primeiro, meio, ultimo));

        // Ação
        clickOn("Relatório Comparativo");

        // Verificação
        TextArea txtSaida = lookup("#txtSaida").query();
        String texto = txtSaida.getText();

        assertTrue(texto.contains("Comparando o primeiro e o último"));
        assertTrue(texto.contains("Primeiro registro:"));
        assertTrue(texto.contains("Peso: 100,00 kg")); // Dados do primeiro
        assertTrue(texto.contains("Último registro:"));
        assertTrue(texto.contains("Peso: 90,00 kg"));  // Dados do último
        
        // Garante que o do meio NÃO aparece explicitamente como bloco principal, 
        // mas como estamos testando String contains, basta garantir que os extremos estão lá.
    }
}