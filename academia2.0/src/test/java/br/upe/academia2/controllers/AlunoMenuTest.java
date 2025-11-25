package br.upe.academia2.controllers;


import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.AlunoMenuController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlunoMenuTest extends Usuario{

    @Spy
    @InjectMocks
    private AlunoMenuController alunoMenuController;

    @Mock
    private ToggleButton btnPerfil = new ToggleButton();

    @Mock
    private Button btnSair;

    @Mock private BorderPane mainPane;

    private static Comum mockusuario;


    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit já inicializado (caso seja executado mais de uma vez)
        }
        // OBS: Se Platform.startup() não funcionar, considere usar a extensão TestFX ou JUnit-JavaFX
    }

    @BeforeEach
    void setup(){
        mockusuario = new Comum("Milena","00000000","Milena@gmail.com","12345",null, null, null);
    }

    @Test
    void setAluno() {
        alunoMenuController.setAluno(mockusuario);
        verify(btnPerfil).setSelected(true);
        verify(alunoMenuController).loadContent("/fxml/IndicadoresAluno.fxml");
    }

    @Test
    void handlePerfil(){
        alunoMenuController.handlePerfil(new ActionEvent());
        verify(alunoMenuController).loadContent("/fxml/IndicadoresAluno.fxml");
    }

    @Test
    void handleExercício(){
        alunoMenuController.handleExercicio(new ActionEvent());
        verify(alunoMenuController).loadContent("/fxml/ExercicioMenu.fxml");
    }

    @Test
    void handlePlanoTreino() {
        alunoMenuController.handlePlanoTreino(new ActionEvent());
        verify(alunoMenuController).loadContent("/fxml/PlanoTreinoAluno.fxml");
    }

    @Test void handleSecao() {
        alunoMenuController.handleSecao(new ActionEvent());
        verify(alunoMenuController).loadContent("/fxml/SecaoTreino.fxml");
    }

    void handleSair() throws Exception {

        Stage stageMock = mock(Stage.class);
        var sceneMock = mock(javafx.scene.Scene.class);

        when(btnSair.getScene()).thenReturn(sceneMock);
        when(sceneMock.getWindow()).thenReturn(stageMock);

        alunoMenuController.handleSair();

        verify(stageMock).setTitle("Academia 2.0 - Login");
        verify(stageMock).setScene(any());
    }

    // nao mexa aqui
    @Test
    void loadContent() {
        try {
            // Simula a falha de Runtime que você havia definido
            doThrow(new RuntimeException("Falhou")).when(alunoMenuController).loadContent("/fxml/Falha.fxml");

            alunoMenuController.loadContent("/fxml/Falha.fxml");

        } catch (RuntimeException expected) {
        }
    }

}
