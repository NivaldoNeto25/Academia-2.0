package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class IndicadoresAlunoController {
    private Usuario usuario;
    private Stage stageAnterior;

    @FXML private Button btnVoltar;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML private void handleCadastrarIndicadores() { /* ... */ }
    @FXML private void handleListarIndicadores() { /* ... */ }
    @FXML private void handleImportarIndicadores() { /* ... */ }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}