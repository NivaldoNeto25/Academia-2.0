package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Comum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ListarAlunosController {
    @FXML private ListView<String> listaAlunosView;
    @FXML private Button btnVoltar;

    private Stage stageAnterior;
    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness();

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    private void initialize() {
        var alunos = usuarioBusiness.listarUsuariosComuns();
        var nomes = alunos.stream()
                .map(a -> a.getNome() + " | " + a.getEmail())
                .toList();
        listaAlunosView.setItems(FXCollections.observableArrayList(nomes));
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}