package br.upe.academia2.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.ui.controllers.ModificarNomePlanoController;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModificarNomePlanoControllerTest {
    @Spy
    @InjectMocks
    private ModificarNomePlanoController controller;

    @Mock private javafx.scene.Scene mockScene;
    @Mock private javafx.stage.Stage mockStage;

    @Mock
    private PlanoTreinoBusiness planoTreinoBusiness;

    @Mock
    private PlanoTreino planoParaModificar;

    @Mock
    private Button btnVoltar;

    @Mock
    private TextField nomePlanoField;

    @BeforeAll
    public static void initJFX() {
        try {
            new javafx.embed.swing.JFXPanel();
        } catch (Exception ignored) {}
    }

    @BeforeEach
    void setUp() throws Exception {
        var field = ModificarNomePlanoController.class.getDeclaredField("planoTreinoBusiness");
        field.setAccessible(true);
        field.set(controller, planoTreinoBusiness);

        var nomePlanoFieldVar = ModificarNomePlanoController.class.getDeclaredField("nomePlanoField");
        nomePlanoFieldVar.setAccessible(true);
        nomePlanoFieldVar.set(controller, nomePlanoField);
    }

    @Test
    void setPlanoParaModificarTest() throws Exception {
        doCallRealMethod().when(controller).carregarNomeAtual();

        controller.setPlanoParaModificar(planoParaModificar);

        var field = ModificarNomePlanoController.class.getDeclaredField("planoParaModificar");
        field.setAccessible(true);

        Object value = field.get(controller);
        assert(value == planoParaModificar);
    }

    @Test
    void carregarNomeAtualTest() {
        doCallRealMethod().when(controller).carregarNomeAtual();
        when(planoParaModificar.getNomePlano()).thenReturn("Treino A");

        controller.setPlanoParaModificar(planoParaModificar);

        verify(nomePlanoField).setText("Treino A");
    }

    @Test
    void handleAlterarNulo() {
        lenient().doNothing().when(controller).mostrarAlerta(
                anyString(), anyString(), ArgumentMatchers.any(Alert.AlertType.class));
        when(nomePlanoField.getText()).thenReturn("");

        controller.setPlanoParaModificar(planoParaModificar);
        controller.handleAlterar();

        verify(planoTreinoBusiness, never()).modificarPlanoDeTreino(any());
    }

    @Test
    void handleAlterarSucesso() {
        doNothing().when(controller).mostrarAlerta(
                anyString(), anyString(), ArgumentMatchers.any(Alert.AlertType.class));
        doNothing().when(controller).carregarNomeAtual();

        when(nomePlanoField.getText()).thenReturn("Novo Nome");

        when(btnVoltar.getScene()).thenReturn(mockScene);
        when(mockScene.getWindow()).thenReturn(mockStage);

        controller.setPlanoParaModificar(planoParaModificar);
        controller.handleAlterar();

        verify(planoParaModificar).setNomePlano("Novo Nome");
        verify(planoTreinoBusiness).modificarPlanoDeTreino(planoParaModificar);
        verify(mockStage).close();
    }

    @Test
    void mostrarAlertaTest() {
        doNothing().when(controller).mostrarAlerta(
                anyString(), anyString(), ArgumentMatchers.any(Alert.AlertType.class));

        controller.mostrarAlerta("Título", "Mensagem", Alert.AlertType.INFORMATION);

        verify(controller).mostrarAlerta("Título", "Mensagem", Alert.AlertType.INFORMATION);
    }
}