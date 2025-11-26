package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.repository.UsuarioJpaRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExcluirAlunoController {
    @FXML private TextField emailField;
    @FXML private Label mensagemLabel;

    private AdmMenuController admMenuController;

    private final UsuarioBusiness usuarioBusiness =
            new UsuarioBusiness(UsuarioJpaRepository.getInstance());

    public void setAdmMenuController(AdmMenuController admMenuController) {
        this.admMenuController = admMenuController;
    }

    @FXML
    public void handleExcluir() {
        String email = emailField.getText();
        if (email.isBlank()) {
            mensagemLabel.setText("Informe o e-mail.");
            return;
        }

        UsuarioBusiness.ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(email);

        switch (resultado) {
            case SUCESSO -> {
                mensagemLabel.setText("Aluno excluído com sucesso!");
                if (admMenuController != null) {
                    admMenuController.atualizarTabelaAlunos();
                }
                Stage atual = (Stage) emailField.getScene().getWindow();
                atual.close();
            }
            case NAO_ENCONTRADO -> mensagemLabel.setText("Usuário não encontrado.");
            case NAO_PERMITIDO_ADM -> mensagemLabel.setText("Não é possível excluir usuário ADM.");
        }

        emailField.clear();
    }
}