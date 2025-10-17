package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Adm;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdmMenuController {

    @FXML private Button btnCadastrar;
    @FXML private Button btnListar;
    @FXML private Button btnModificar;
    @FXML private Button btnExcluir;
    @FXML private Button btnVoltar;

    Logger logger = Logger.getLogger(AdmMenuController.class.getName());

    public void setAdm(Adm adm) {
        // Lógica para utilizar os dados do administrador, se necessário
    }

    @FXML private void handleCadastrarAluno() { irParaTela("/fxml/CadastroAluno.fxml", "Cadastrar Aluno", btnCadastrar); }
    @FXML private void handleListarAlunos()    { irParaTela("/fxml/ListarAlunos.fxml", "Listar Alunos", btnListar); }
    @FXML private void handleModificarAluno()  { irParaTela("/fxml/ModificarAluno.fxml", "Modificar Aluno", btnModificar); }
    @FXML private void handleExcluirAluno()    { irParaTela("/fxml/ExcluirAluno.fxml", "Excluir Aluno", btnExcluir); }

    @FXML
    public void handleVoltar() {
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
        } catch (IOException | InvocationTargetException | IllegalAccessException e) {
            logger.log(Level.WARNING, "Erro ao caregar a tela", e);
        } catch (NoSuchMethodException e) {
            logger.log(Level.WARNING, "O controlador não possui o método setStageAnterior", e);
        }
    }
}