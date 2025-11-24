package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModificarAlunoController {
    @FXML private TextField emailField;
    @FXML private TextField nomeField;
    @FXML private TextField senhaField;
    @FXML private Label mensagemLabel;

    private AdmMenuController admMenuController; // Referência para atualizar a tabela no menu

    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioCsvRepository.getInstance());

    public void setStageAnterior(Stage stageAnterior) {
        // Método reservado para ser implementado se for necessário passar Stage entre telas.
        // Não faz nada por padrão.
    }

    public void setAdmMenuController(AdmMenuController admMenuController) { this.admMenuController = admMenuController; }

    @FXML
    public void handleModificar() {
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

        // Atualiza a tabela do menu administrador
        if (admMenuController != null) {
            admMenuController.atualizarTabelaAlunos();
        }

        // Fecha a tela após modificar com sucesso
        Stage atual = (Stage) emailField.getScene().getWindow();
        atual.close();
    }
}