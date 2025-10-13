package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PlanoTreinoAlunoController {
    private Usuario usuario;
    private Stage stageAnterior;

    @FXML private Button btnVoltar;

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML private void handleCadastrarPlano() { /* ... */ }
    @FXML private void handleListarPlano() { /* ... */ }
    @FXML private void handleModificarPlano() { /* ... */ }
    @FXML private void handleSecaoTreino() { /* ... */ }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}