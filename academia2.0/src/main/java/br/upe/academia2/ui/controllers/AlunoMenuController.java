package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AlunoMenuController {

    private Usuario aluno;

    // Componentes da tela, um de cada botão principal deve ter fx:‘id’ definido no FXML.
    @FXML
    private Button btnExercicios;
    @FXML
    private Button btnIndicadores;
    @FXML
    private Button btnPlanoTreino;
    @FXML
    private Button btnRelatorios;

    public void setAluno(Usuario aluno) {
        this.aluno = aluno;
    }

    @FXML
    private void handleExercicios() {
        Stage stageAtual = (Stage) btnExercicios.getScene().getWindow();
        trocarTela("/fxml/ExercicioMenu.fxml", "Exercícios", stageAtual);
    }

    @FXML
    private void handleIndicadores() {
        Stage stageAtual = (Stage) btnIndicadores.getScene().getWindow();
        trocarTela("/fxml/IndicadoresAluno.fxml", "Indicadores Biomédicos", stageAtual);
    }

    @FXML
    private void handlePlanoTreino() {
        Stage stageAtual = (Stage) btnPlanoTreino.getScene().getWindow();
        trocarTela("/fxml/PlanoTreinoAluno.fxml", "Plano de Treino", stageAtual);
    }

    @FXML
    private void handleRelatorio() {
        Stage stageAtual = (Stage) btnRelatorios.getScene().getWindow();
        trocarTela("/fxml/RelatorioAluno.fxml", "Relatórios", stageAtual);
    }

    @FXML
    private void handleSair() {
        Stage stageAtual = (Stage) btnExercicios.getScene().getWindow();
        stageAtual.close();
    }

    private void trocarTela(String caminhoFxml, String titulo, Stage stageAtual) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            try {
                var setUser = controller.getClass().getMethod("setUsuario", Usuario.class);
                setUser.invoke(controller, aluno);
            } catch (NoSuchMethodException ignored) {}

            try {
                var setStageAnt = controller.getClass().getMethod("setStageAnterior", Stage.class);
                setStageAnt.invoke(controller, stageAtual);
            } catch (NoSuchMethodException ignored) {}

            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            stageAtual.close();
            novaStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}