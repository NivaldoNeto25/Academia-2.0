package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
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
    private PlanoTreinoBusiness planoTreinoBusiness;
    private PlanoTreino planoParaModificar;

    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
    }

    public void setPlanoParaModificar(PlanoTreino plano) {
        this.planoParaModificar = plano;
        carregarNomeAtual();
    }

    public void carregarNomeAtual() {
        
        if (planoParaModificar != null) {
            nomePlanoField.setText(planoParaModificar.getNomePlano());
        } else {
            nomePlanoField.setText("");
            mostrarAlerta("Erro", "Nenhum plano foi passado para esta tela.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAlterar() {
        String novoNome = nomePlanoField.getText().trim();

        if (novoNome.isEmpty()) {
            mostrarAlerta("Erro", "O nome do plano não pode ser vazio.", Alert.AlertType.WARNING);
            return;
        }

        if(planoParaModificar == null) {
            mostrarAlerta("Erro", "Nenhum plano selecionado para modificação.", Alert.AlertType.ERROR);
            return;
        }
        planoParaModificar.setNomePlano(novoNome);
        planoTreinoBusiness.modificarPlanoDeTreino(planoParaModificar);
        mostrarAlerta("Sucesso", "Nome do plano alterado com sucesso!", Alert.AlertType.INFORMATION);
        handleVoltar();
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
    }

    public void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}