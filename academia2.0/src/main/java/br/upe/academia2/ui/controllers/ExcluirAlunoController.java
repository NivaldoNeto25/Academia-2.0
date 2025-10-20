package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
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
    private AdmMenuController admMenuController; // Referência para atualizar a tabela no menu

    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioCsvRepository.getInstance());

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }
    public void setAdmMenuController(AdmMenuController admMenuController) { this.admMenuController = admMenuController; }

    @FXML
    public void handleExcluir() {
        String email = emailField.getText();
        if (email.isBlank()) {
            mensagemLabel.setText("Informe o e-mail.");
            return;
        }

        UsuarioBusiness.ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(email);
        usuarioBusiness.salvarAlteracoesNoCsv();

        switch (resultado) {
            case SUCESSO -> mensagemLabel.setText("Aluno excluído com sucesso!");
            case NAO_ENCONTRADO -> mensagemLabel.setText("Usuário não encontrado.");
            case NAO_PERMITIDO_ADM -> mensagemLabel.setText("Não é possível excluir usuário ADM.");
        }

        // Atualiza a tabela do menu administrador
        if (admMenuController != null) {
            admMenuController.atualizarTabelaAlunos();
        }

        emailField.clear();
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}