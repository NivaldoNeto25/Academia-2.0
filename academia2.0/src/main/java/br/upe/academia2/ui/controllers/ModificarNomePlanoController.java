package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModificarNomePlanoController {

    @FXML private TextField nomePlanoField;
    @FXML private Button btnAlterar;
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;
    private Stage stageAnterior;
    private PlanoTreinoBusiness planoTreinoBusiness;

    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarNomeAtual();
    }

    public void setStageAnterior(Stage stage) {
        this.stageAnterior = stage;
    }

    public void carregarNomeAtual() {
        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
        if (plano != null) {
            nomePlanoField.setText(plano.getNomePlano());
        } else {
            nomePlanoField.setText("");
        }
    }

    @FXML
    public void handleAlterar() {
        String novoNome = nomePlanoField.getText().trim();

        if (novoNome.isEmpty()) {
            mostrarAlerta("Erro", "O nome do plano não pode ser vazio.", Alert.AlertType.WARNING);
            return;
        }

        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
        if (plano == null) {
            mostrarAlerta("Erro", "Nenhum plano de treino encontrado para alterar.", Alert.AlertType.ERROR);
            return;
        }

        plano.setNomePlano(novoNome);
        planoTreinoBusiness.modificarPlanoDeTreino(plano);

        mostrarAlerta("Sucesso", "Nome do plano alterado com sucesso!", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) {
            stageAnterior.show();
        }
    }

    public void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}