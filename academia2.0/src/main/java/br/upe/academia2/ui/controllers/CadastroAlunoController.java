package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroAlunoController {
    @FXML
    public TextField nomeField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField senhaField;
    @FXML
    public Label mensagemLabel;

    private AdmMenuController admMenuController;
    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioCsvRepository.getInstance());

    public void setStageAnterior(Stage stageAnterior) {
        // Método reservado para ser implementado se for necessário passar Stage entre telas.
        // Não faz nada por padrão.
    }


    public void setAdmMenuController(AdmMenuController admMenuController) {
        this.admMenuController = admMenuController;
    }

    @FXML
    public void handleCadastrar() {
        String nome = nomeField.getText();
        String email = emailField.getText();
        String senha = senhaField.getText();

        mensagemLabel.setText(""); // Limpa mensagem anterior

        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            mensagemLabel.setText("Todos os campos são obrigatórios.");
            return;
        }
        if (usuarioBusiness.listarUsuarios().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            mensagemLabel.setText("Já existe um aluno com esse e-mail.");
            return;
        }

        Usuario novo = new Comum(nome, null, email, senha, null, null, null);
        try {
            usuarioBusiness.cadastrarUsuario(novo);
            mensagemLabel.setStyle("-fx-text-fill: green;");
            mensagemLabel.setText("Aluno cadastrado com sucesso!");

            // Atualizar tabela do menu
            if (admMenuController != null) {
                admMenuController.atualizarTabelaAlunos();
            }

            // Fecha a tela de cadastro ao salvar
            Stage atual = (Stage) nomeField.getScene().getWindow();
            atual.close();
        } catch (IllegalArgumentException ex) {
            mensagemLabel.setStyle("-fx-text-fill: red;");
            mensagemLabel.setText("Digite um e-mail válido!");
        }
    }
}