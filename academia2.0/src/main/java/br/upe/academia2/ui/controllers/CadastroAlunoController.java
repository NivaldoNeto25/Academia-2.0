package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroAlunoController {
    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private TextField senhaField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;

    private Stage stageAnterior;
    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness();

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    private void handleCadastrar() {
        String nome = nomeField.getText();
        String email = emailField.getText();
        String senha = senhaField.getText();

        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            mensagemLabel.setText("Todos os campos são obrigatórios.");
            return;
        }
        if (usuarioBusiness.listarUsuarios().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            mensagemLabel.setText("Já existe um aluno com esse e-mail.");
            return;
        }
        Usuario novo = new Comum(nome, null, email, senha, null, null, null);
        usuarioBusiness.cadastrarUsuario(novo);
        mensagemLabel.setText("Aluno cadastrado com sucesso!");
        nomeField.clear(); emailField.clear(); senhaField.clear();
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}