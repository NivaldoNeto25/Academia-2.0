package br.upe.academia2.ui.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Stage;

public class CadastrarPlanoTreinoController{
    @FXML private TextField nomeField;
    @FXML private TextField dataInicioField;
    @FXML private TextField dataFimField;
    @FXML private Button btnVoltar;

    private Stage stageAnterior;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    private void handleCadastrar() {

    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }


}