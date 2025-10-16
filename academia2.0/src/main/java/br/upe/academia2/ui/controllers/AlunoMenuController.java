package br.upe.academia2.ui.controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AlunoMenuController {

    private Usuario aluno;
    @FXML
    private Button btnExercicios;
    @FXML
    private Button btnIndicadores;
    @FXML
    private Button btnPlanoTreino;
    @FXML
    private Button btnRelatorios;
    @FXML
    private Button btnSair;

    private Logger logger = Logger.getLogger(AlunoMenuController.class.getName());

    public void setAluno(Usuario aluno) {
        this.aluno = aluno;
    }

    @FXML
    public void handleExercicios() {
        Stage stageAtual = (Stage) btnExercicios.getScene().getWindow();
        trocarTela("/fxml/ExercicioMenu.fxml", "Exercícios", stageAtual);
    }

    @FXML
    public void handleIndicadores() {
        Stage stageAtual = (Stage) btnIndicadores.getScene().getWindow();
        trocarTela("/fxml/IndicadoresAluno.fxml", "Indicadores Biomédicos", stageAtual);
    }

    @FXML
    public void handlePlanoTreino() {
        Stage stageAtual = (Stage) btnPlanoTreino.getScene().getWindow();
        trocarTela("/fxml/PlanoTreinoAluno.fxml", "Plano de Treino", stageAtual);
    }

    @FXML
    public void handleRelatorio() {
        Stage stageAtual = (Stage) btnRelatorios.getScene().getWindow();
        trocarTela("/fxml/RelatorioAluno.fxml", "Relatórios", stageAtual);
    }

    @FXML
    public void handleSair() {
        Stage stageAtual = (Stage) btnSair.getScene().getWindow();
        stageAtual.close();
    }

    public void trocarTela(String caminhoFxml, String titulo, Stage stageAtual) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            try {
                var setUser = controller.getClass().getMethod("setUsuario", Usuario.class);
                setUser.invoke(controller, aluno);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored){
                logger.log(Level.WARNING,"Método não encontrado !!!", ignored);
            }

            try {
                var setStageAnt = controller.getClass().getMethod("setStageAnterior", Stage.class);
                setStageAnt.invoke(controller, stageAtual);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                logger.log(Level.WARNING,"Método não encontrado, ou acesso ilegal !!!", ignored);
            } 

            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            stageAtual.close();
            novaStage.show();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar", e);
        }
    }
}