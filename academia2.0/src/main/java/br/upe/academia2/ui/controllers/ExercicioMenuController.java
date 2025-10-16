package br.upe.academia2.ui.controllers;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ExercicioMenuController {
    @FXML private Button btnCadastrar;
    @FXML private Button btnListar;
    @FXML private Button btnModificar;
    @FXML private Button btnExcluir;
    @FXML private Button btnVoltar;

    @FXML private void handleCadastrarExercicio() { irParaTela("/fxml/CadastrarExercicio.fxml", "Cadastrar Exercicio", btnCadastrar); }
    @FXML private void handleListarExercicio()    { irParaTela("/fxml/ListarExercicios.fxml", "Listar Exercicio", btnListar); }
    @FXML private void handleModificarExercicio()  { irParaTela("/fxml/ModificarExercicio.fxml", "Modificar Exercicio", btnModificar); }
    @FXML private void handleExcluirExercicio()    { irParaTela("/fxml/ExcluirExercicio.fxml", "Excluir Exercicio", btnExcluir); }

    private Logger logger = Logger.getLogger(ExercicioMenuController.class.getName());

    private Stage stageAnterior;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    public void handleVoltar() {
        if (stageAnterior != null) {
            stageAnterior.show();
        }
        Stage stageAtual = (Stage) btnVoltar.getScene().getWindow();
        stageAtual.close();
    }

    public void irParaTela(String caminhoFxml, String titulo, Button origem) {
        try {
            Stage stageAtual = (Stage) origem.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            controller.getClass().getMethod("setStageAnterior", Stage.class).invoke(controller, stageAtual);
            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            stageAtual.close();
            novaStage.show();
        } catch (IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            logger.log(Level.WARNING, "Erro ao carregar a tela", e);
        }
    }


}