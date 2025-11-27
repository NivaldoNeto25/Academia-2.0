package br.upe.academia2.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.ui.controllers.AlterarDatasController;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarDatasControllerTest {

    @Mock
    private AlterarDatasController controller;

    @Mock
    private PlanoTreino planoParaModificar;
    @Mock
    private PlanoTreinoBusiness planoTreinoBusiness;

    @Mock
    private DatePicker dataInicioPicker;
    @Mock
    private DatePicker dataFimPicker;
    @Mock
    private Button btnVoltar;
    @Mock
    private Stage mockStage;
    @Mock
    private Scene mockScene;

    @BeforeEach
    void setUp() throws Exception {

        setMockField("planoTreinoBusiness", planoTreinoBusiness);
        setMockField("dataInicioPicker", dataInicioPicker);
        setMockField("dataFimPicker", dataFimPicker);
        setMockField("btnVoltar", btnVoltar);

        lenient().doNothing().when(controller).mostrarAlerta(anyString(), anyString(), any(Alert.AlertType.class));
        lenient().doNothing().when(controller).handleVoltar();

        lenient().when(btnVoltar.getScene()).thenReturn(mockScene);
        lenient().when(mockScene.getWindow()).thenReturn(mockStage);
    }

    // Método utilitário para injetar mocks via Reflection
    private void setMockField(String fieldName, Object mock) throws Exception {
        Field field = AlterarDatasController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, mock);
    }

    @Test
    void carregarDatasAtuaisComDatasValidas() throws Exception {

        Date inicio = Date.from(LocalDate.of(2025, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        when(planoParaModificar.getInicioPlano()).thenReturn(inicio);

        setMockField("planoParaModificar", planoParaModificar);

        doCallRealMethod().when(controller).setPlanoParaModificar(planoParaModificar);
        doCallRealMethod().when(controller).carregarDatasAtuais();

        controller.setPlanoParaModificar(planoParaModificar);

        verify(dataInicioPicker).setValue(LocalDate.of(2025, 1, 1));
    }

    @Test
    void handleSalvarDatasNulasFalha() throws Exception {

        setMockField("planoParaModificar", planoParaModificar);
        when(dataInicioPicker.getValue()).thenReturn(null);
        lenient().when(dataFimPicker.getValue()).thenReturn(LocalDate.of(2025, 12, 31));

        doCallRealMethod().when(controller).handleSalvar();
        controller.handleSalvar();
        verify(controller).mostrarAlerta(eq("Erro"), anyString(), eq(Alert.AlertType.WARNING));
        verify(planoTreinoBusiness, never()).modificarPlanoDeTreino(any());
        verify(controller, never()).handleVoltar();
    }

    @Test
    void handleSalvar_DatasValidas_DeveAtualizarPlanoEChamarBusiness() throws Exception {

        LocalDate inicioLocal = LocalDate.of(2025, 1, 1);
        LocalDate fimLocal = LocalDate.of(2025, 12, 31);

        when(dataInicioPicker.getValue()).thenReturn(inicioLocal);
        when(dataFimPicker.getValue()).thenReturn(fimLocal);

        setMockField("planoParaModificar", planoParaModificar);

        doCallRealMethod().when(controller).handleSalvar();
        controller.handleSalvar();
        Date dataInicioEsperada = Date.from(inicioLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        verify(planoParaModificar).setInicioPlano(dataInicioEsperada);

        verify(planoTreinoBusiness).modificarPlanoDeTreino(planoParaModificar);

        verify(controller).mostrarAlerta(eq("Sucesso"), anyString(), eq(Alert.AlertType.INFORMATION));
        verify(controller).handleVoltar();
    }

    @Test
    void handleVoltarTest() throws Exception {

        AlterarDatasController realController = new AlterarDatasController();

        Field field = AlterarDatasController.class.getDeclaredField("btnVoltar");
        field.setAccessible(true);
        field.set(realController, btnVoltar);

        realController.handleVoltar();

        verify(mockStage).close();
    }
}