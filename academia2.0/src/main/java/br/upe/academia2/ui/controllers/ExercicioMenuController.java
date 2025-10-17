package br.upe.academia2.ui.controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.academia2.data.beans.Usuario;
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

    private Usuario usuario;

    private final Logger logger = Logger.getLogger(ExercicioMenuController.class.getName());

    private Stage stageAnterior;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    public void handleVoltar() {
        if (stageAnterior != null) {
            stageAnterior.show();
        }
        Stage stageAtual = (Stage) btnVoltar.getScene().getWindow();
        stageAtual.close();
    }

    @FXML private void handleCadastrarExercicio() {
        irParaTela("/fxml/CadastrarExercicio.fxml", "Cadastrar Exercicio", btnCadastrar);
    }

    @FXML private void handleListarExercicio() {
        irParaTela("/fxml/ListarExercicios.fxml", "Listar Exercicio", btnListar);
    }

    @FXML private void handleModificarExercicio() {
        irParaTela("/fxml/ModificarExercicio.fxml", "Modificar Exercicio", btnModificar);
    }

    @FXML private void handleExcluirExercicio() {
        irParaTela("/fxml/ExcluirExercicio.fxml", "Excluir Exercicio", btnExcluir);
    }

    public void irParaTela(String caminhoFxml, String titulo, Button origem) {
        try {
            Stage stageAtual = (Stage) origem.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            controller.getClass().getMethod("setStageAnterior", Stage.class).invoke(controller, stageAtual);

            invokeSetUsuarioIfExists(controller);

            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            stageAtual.close();
            novaStage.show();
        } catch (IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            logger.log(Level.WARNING, "Erro ao carregar a tela", e);
        }
    }

    private void invokeSetUsuarioIfExists(Object controller) {
        try {
            controller.getClass().getMethod("setUsuario", Usuario.class).invoke(controller, usuario);
        } catch (NoSuchMethodException ignored) {
            // Metodo setUsuario não existe no controller, ignore porque é opcional para alguns controllers
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Erro ao invocar setUsuario no controller", e);
        }
    }
}