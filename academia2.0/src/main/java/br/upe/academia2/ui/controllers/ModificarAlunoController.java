package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModificarAlunoController {
    @FXML private TextField emailField;
    @FXML private TextField nomeField;
    @FXML private TextField senhaField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;

    private Stage stageAnterior;
    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioCsvRepository.getInstance());

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    private void handleModificar() {
        String email = emailField.getText();
        Usuario existente = usuarioBusiness.listarUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            mensagemLabel.setText("Aluno não encontrado.");
            return;
        }

        String nome = nomeField.getText();
        String senha = senhaField.getText();
        if (!nome.isBlank()) existente.setNome(nome);
        if (!senha.isBlank()) existente.setSenha(senha);

        usuarioBusiness.atualizarUsuario(existente);
        usuarioBusiness.salvarAlteracoesNoCsv();
        mensagemLabel.setText("Aluno modificado com sucesso!");
        nomeField.clear(); senhaField.clear();
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}