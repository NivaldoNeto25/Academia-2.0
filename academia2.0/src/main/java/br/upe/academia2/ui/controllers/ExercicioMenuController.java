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
import br.upe.academia2.data.beans.Exercicio;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class ExercicioMenuController implements AlunoMenuController.UsuarioDependente {

    @FXML private Button btnCadastrar;
    @FXML private Button btnModificar;
    @FXML private Button btnExcluir;
    @FXML private ListView<Exercicio> listaExercicios;

    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    private BorderPane mainPane;

    private final Logger logger = Logger.getLogger(ExercicioMenuController.class.getName());




    @Override
    public void setUsuario(Usuario usuario) {
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
            refreshList();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Falha ao abrir a janela de cadastro.", e);
        }
    }

    @FXML
    private void handleModificarExercicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModificarExercicio.fxml"));
            Parent root = loader.load();
            ModificarExercicioController controller = loader.getController();

            Stage stageAtual = (Stage) mainPane.getScene().getWindow();
            controller.setStageAnterior(stageAtual);

            Stage novaJanela = new Stage();
            novaJanela.setTitle("Modificar Exercício");
            novaJanela.setScene(new Scene(root));
            novaJanela.initModality(Modality.WINDOW_MODAL);
            novaJanela.initOwner(stageAtual);
            novaJanela.showAndWait();
            refreshList();
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
            refreshList();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Falha ao abrir a janela de exclusão.", e);
        }
    }

    public void initialize() {
        listaExercicios.setCellFactory(param -> new ListCell<Exercicio>() {
            @Override
            protected void updateItem(Exercicio item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNome() == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " | " + item.getDescricao());
                }
            }
        });

        listaExercicios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mostrarExecucao(newSelection);
                    }
                }
        );

        refreshList();
    }


    public void refreshList() {
        logger.info("Atualizando lista de exercícios...");
        var exercicios = exercicio.listarExercicios();
        listaExercicios.setItems(FXCollections.observableArrayList(exercicios));
    }

    private void mostrarExecucao(Exercicio exercicio) {
        if (exercicio.getCaminhoGif() == null || exercicio.getCaminhoGif().isBlank()) {
            logger.log(Level.WARNING, "Exercício sem caminho de GIF: {0}", exercicio.getNome());
            return;
        }

        try {
            Image gif = new Image(exercicio.getCaminhoGif());
            ImageView gifView = new ImageView(gif);
            gifView.setFitHeight(150.0);
            gifView.setFitWidth(150.0);
            gifView.setPreserveRatio(true);

            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);
            layout.setStyle("-fx-background-color: #0F4C83; -fx-padding: 15px;");
            layout.getChildren().add(gifView);

            Stage gifStage = new Stage();
            Scene scene = new Scene(layout);
            gifStage.setTitle("Execução: " + exercicio.getNome());
            gifStage.setScene(scene);
            gifStage.initModality(Modality.APPLICATION_MODAL);
            gifStage.initOwner(mainPane.getScene().getWindow());
            gifStage.setWidth(200);
            gifStage.setHeight(200);
            gifStage.setResizable(false);
            gifStage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar GIF: {0}", exercicio.getCaminhoGif());
        }
    }
}