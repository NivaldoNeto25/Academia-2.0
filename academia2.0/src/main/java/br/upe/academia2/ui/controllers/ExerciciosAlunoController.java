package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ExerciciosAlunoController {

    private Usuario usuario;
    private Stage stageAnterior; // Janela do AlunoMenuController

    @FXML
    private Button btnVoltar; // Defina fx:‘id’="btnVoltar" no botão "Voltar" do FXML

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Recebe referência da tela anterior (menu do aluno)
    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    public void handleVoltar() {
        // Fecha a tela de exercícios
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        // Reabre a tela anterior (menu do aluno)
        if (stageAnterior != null) {
            stageAnterior.show();
        }
    }
}