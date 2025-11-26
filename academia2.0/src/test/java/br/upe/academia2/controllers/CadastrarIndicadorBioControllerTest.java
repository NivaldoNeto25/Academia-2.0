package br.upe.academia2.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.CadastrarIndicadorBioController;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarIndicadorBioControllerTest {

    @Spy
    @InjectMocks
    private CadastrarIndicadorBioController controller;

    @Mock
    private IndicadorBiomedicoBusiness indicadorBiomedicoBusinessMock;

    @Mock
    private Usuario usuarioLogadoMock;

    @Mock private TextField alturaField;
    @Mock private TextField pesoField;
    @Mock private TextField massaMagraField;
    @Mock private TextField percGorduraField;
    @Mock private Label mensagemLabel;

    @Mock
    private Scene mockScene;
    @Mock
    private Stage mockStage;

    @BeforeEach
    void setUp() throws Exception {
        Field fBusiness = CadastrarIndicadorBioController.class.getDeclaredField("indicadorbio");
        fBusiness.setAccessible(true);
        fBusiness.set(controller, indicadorBiomedicoBusinessMock);
        injectField(controller, "alturaField", alturaField);
        injectField(controller, "pesoField", pesoField);
        injectField(controller, "massaMagraField", massaMagraField);
        injectField(controller, "percGorduraField", percGorduraField);
        injectField(controller, "mensagemLabel", mensagemLabel);
        controller.setUsuarioLogado(usuarioLogadoMock);

        lenient().when(usuarioLogadoMock.getEmail()).thenReturn("teste@academia.com");
    }

    private void injectField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void setUsuarioLogadoTest() throws Exception {
        controller.setUsuarioLogado(usuarioLogadoMock);
        Field field = CadastrarIndicadorBioController.class.getDeclaredField("usuarioLogado");
        field.setAccessible(true);
        assertEquals(usuarioLogadoMock, field.get(controller));
    }

    @Test
    void setUsuarioTest() throws Exception {
        controller.setUsuario(usuarioLogadoMock);
        Field field = CadastrarIndicadorBioController.class.getDeclaredField("usuarioLogado");
        field.setAccessible(true);
        assertEquals(usuarioLogadoMock, field.get(controller));
    }

    @Test
    void usuarioNaoLogadoTest() {
        controller.setUsuarioLogado(null);
        controller.handleCadastrarIndicadores();
        verify(mensagemLabel).setText("Usuário não logado. Operação não permitida.");
        verifyNoInteractions(indicadorBiomedicoBusinessMock);
    }

    @Test
    void erroNumberFormatExceptionTest() {
        when(alturaField.getText()).thenReturn("1.70");
        when(pesoField.getText()).thenReturn("80");
        when(percGorduraField.getText()).thenReturn("ABC");
        lenient().when(massaMagraField.getText()).thenReturn("70");

        controller.handleCadastrarIndicadores();

        verify(mensagemLabel).setText("Digite valores numéricos válidos.");
        verifyNoInteractions(indicadorBiomedicoBusinessMock);
    }

    @Test
    void valoresMenoresOuIguaisAZeroTest() {
        when(alturaField.getText()).thenReturn("1.70");
        when(pesoField.getText()).thenReturn("-5"); // Valor inválido
        when(percGorduraField.getText()).thenReturn("20");
        when(massaMagraField.getText()).thenReturn("70");

        controller.handleCadastrarIndicadores();

        verify(mensagemLabel).setText("Peso e altura devem ser maiores que zero.");
        verifyNoInteractions(indicadorBiomedicoBusinessMock);
    }

    @Test
    void cadastroSucessoEFechamentoTest() {
        final double ALTURA = 1.80;
        final double PESO = 80.0;
        final double PERC_GORDURA = 15.0;
        final double PERC_MASSA_MAGRA = 75.0;
        final double IMC_ESPERADO = PESO / (ALTURA * ALTURA);

        when(alturaField.getText()).thenReturn(String.valueOf(ALTURA));
        when(pesoField.getText()).thenReturn(String.valueOf(PESO));
        when(percGorduraField.getText()).thenReturn(String.valueOf(PERC_GORDURA));
        when(massaMagraField.getText()).thenReturn(String.valueOf(PERC_MASSA_MAGRA));

        when(alturaField.getScene()).thenReturn(mockScene);
        when(mockScene.getWindow()).thenReturn(mockStage);

        doNothing().when(indicadorBiomedicoBusinessMock)
                .cadastrarIndicador(any(Usuario.class), any(IndicadorBiomedico.class));

        controller.handleCadastrarIndicadores();

        verify(indicadorBiomedicoBusinessMock).cadastrarIndicador(
                eq(usuarioLogadoMock),
                ArgumentMatchers.argThat(indicador -> indicador.getPeso() == PESO && indicador.getAltura() == ALTURA && Math.abs(indicador.getImc() - IMC_ESPERADO) < 0.01));

        verify(mockStage).close();
        verify(mensagemLabel, never()).setText(anyString());
    }
}