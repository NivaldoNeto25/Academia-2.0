package br.upe.academia2.ui.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExercicioMenuController {

    @FXML private Button btnCadastrar;
    @FXML private Button btnListar;
    @FXML private Button btnModificar;
    @FXML private Button btnExcluir;
    @FXML private ListView<String> listaExercicios;

    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    private Usuario usuario;

    private BorderPane mainPane;

    private final Logger logger = Logger.getLogger(ExercicioMenuController.class.getName());

    private Stage stageAnterior;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setMainPane(BorderPane mainPane) {
        this.mainPane = mainPane;
    }

    @FXML private void handleCadastrarExercicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CadastrarExercicio.fxml"));
            Parent root = loader.load();

            CadastrarExercicioController controller = loader.getController();

            Stage stageAtual = (Stage) mainPane.getScene().getWindow();
            controller.setStageAnterior(stageAtual);

            Stage novaJanela = new Stage();
            novaJanela.setTitle("Cadastrar Novo Exercício");
            novaJanela.setScene(new Scene(root));
            novaJanela.showAndWait();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Falha ao abrir a janela de cadastro.", e);
        }
    }

    @FXML private void handleListarExercicio() {
        loadContent("/fxml/ListarExercicios.fxml", "Listar Exercicio");
    }

    // Em ExercicioMenuController.java

    @FXML
    private void handleModificarExercicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModificarExercicio.fxml"));
            Parent root = loader.load();

            // Pega o controller da tela de Modificar
            ModificarExercicioController controller = loader.getController();

            Stage stageAtual = (Stage) mainPane.getScene().getWindow();
            controller.setStageAnterior(stageAtual);

            Stage novaJanela = new Stage();
            novaJanela.setTitle("Modificar Exercício");
            novaJanela.setScene(new Scene(root));
            novaJanela.initModality(Modality.WINDOW_MODAL);
            novaJanela.initOwner(stageAtual);
            novaJanela.showAndWait();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Falha ao abrir a janela de modificação.", e);
        }
    }

    @FXML
    private void handleExcluirExercicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExcluirExercicio.fxml"));
            Parent root = loader.load();

            ExcluirExercicioController controller = loader.getController();

            Stage stageAtual = (Stage) mainPane.getScene().getWindow();
            controller.setStageAnterior(stageAtual); // Passa a janela principal

            Stage novaJanela = new Stage();
            novaJanela.setTitle("Excluir Exercício");
            novaJanela.setScene(new Scene(root));
            novaJanela.initModality(Modality.WINDOW_MODAL);
            novaJanela.initOwner(stageAtual);
            novaJanela.showAndWait();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Falha ao abrir a janela de exclusão.", e);
        }
    }

    private void loadContent(String fxmlPath, String titulo) {
        if (mainPane == null) {
            logger.log(Level.WARNING, "MainPane é nulo. Navegação falhou.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();

            mainPane.setCenter(view);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar o FXML: " + fxmlPath, e);
            mainPane.setCenter(new javafx.scene.control.Label("Erro ao carregar a página."));
        }
    }
    public void initialize() {
        logger.info(() -> "Exercícios listados: " + exercicio.listarExercicios());
        var exercicios = exercicio.listarExercicios();
        var nomes = exercicios.stream()
                .map(a -> a.getNome() + " | " + a.getDescricao())
                .toList();
        listaExercicios.setItems(FXCollections.observableArrayList(nomes));
    }
}