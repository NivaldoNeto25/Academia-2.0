package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;


public class ExcluirExercicioController {
    @FXML private TextField nomeField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;

    private Stage stageAnterior;
    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    private void handleExcluir() {
        String nome = nomeField.getText();
        if (nome.isBlank()) {
            mensagemLabel.setText("Informe o nome.");
            return;
        }
        exercicio.deletarExercicio(nome);
        exercicio.salvarAlteracoesNoCsv();
        mensagemLabel.setText("Exercicio excluído!");
        nomeField.clear();
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) {
            stageAnterior.show();
        }
    }
}

