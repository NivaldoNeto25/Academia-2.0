package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExcluirAlunoController {
    @FXML private TextField emailField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;

    private Stage stageAnterior;
    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness();

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    private void handleExcluir() {
        String email = emailField.getText();
        if (email.isBlank()) {
            mensagemLabel.setText("Informe o e-mail.");
            return;
        }
        usuarioBusiness.deletarUsuario(email);
        mensagemLabel.setText("Aluno excluído (se existia)!");
        emailField.clear();
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}