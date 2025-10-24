package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlunoMenuController implements Initializable {

    private Usuario aluno;

    @FXML private BorderPane mainPane;
    @FXML private ToggleGroup menuToggleGroup;
    @FXML private ToggleButton btnPerfil;
    @FXML private Button btnSair;

    Logger logger = Logger.getLogger(AlunoMenuController.class.getName());

    public void setAluno(Usuario aluno) {
        this.aluno = aluno;
        btnPerfil.setSelected(true);
        loadContent("/fxml/IndicadoresAluno.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização adicional, se necessário
    }

    @FXML public void handlePerfil(ActionEvent event) {
        loadContent("/fxml/IndicadoresAluno.fxml");
    }

    @FXML public void handleExercicio(ActionEvent event) {
        loadContent("/fxml/ExercicioMenu.fxml");
    }

    @FXML public void handlePlanoTreino(ActionEvent event) {
        loadContent("/fxml/PlanoTreinoAluno.fxml");
    }

    @FXML public void handleSecao(ActionEvent event) {
        loadContent("/fxml/SecaoTreino.fxml");
    }

    @FXML
    public void handleSair() {
        Stage stageAtual = (Stage) btnSair.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            stageAtual.setScene(new javafx.scene.Scene(loader.load()));
            stageAtual.setTitle("Academia 2.0 - Login");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao voltar para a tela de login.", e);
            stageAtual.close();
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            Parent view = loader.load();

            Object controller = loader.getController();

            if (controller instanceof ExercicioMenuController exerciciomenucontroller) {
                exerciciomenucontroller.setMainPane(mainPane);
            } else if (controller instanceof UsuarioDependente usuariodependente) {
                usuariodependente.setUsuario(aluno);
            } else if (controller instanceof PlanoTreinoAlunoController planotreinoalunocontroller) {
                planotreinoalunocontroller.setUsuario(this.aluno);
            } else if (controller instanceof SecaoTreinoController secaotreinocontroller) {
                secaotreinocontroller.setUsuario(this.aluno);
            }

            mainPane.setCenter(view);

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar o FXML ", e);
            mainPane.setCenter(new javafx.scene.control.Label("Erro ao carregar a página: " + fxmlPath));
        }
    }

    public interface UsuarioDependente {
        void setUsuario(Usuario usuario);
    }
}
