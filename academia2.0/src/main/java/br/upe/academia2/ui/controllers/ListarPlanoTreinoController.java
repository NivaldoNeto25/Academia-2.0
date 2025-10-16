package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class ListarPlanoTreinoController {

    @FXML private ListView<String> listViewPlanos;
    @FXML private Button fechar;

    private Usuario usuarioLogado;
    private final PlanoTreinoBusiness planoTreinoBusiness;

    public ListarPlanoTreinoController() {
        this.planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        carregarPlanosDoUsuario();
    }

    private void carregarPlanosDoUsuario() {
        if (usuarioLogado == null) {
            mostrarAlerta("Erro", "Usuário não logado. Não é possível listar os planos.", Alert.AlertType.ERROR);
            return;
        }

        List<PlanoTreino> planos = planoTreinoBusiness.listarPlanosPorUsuario(usuarioLogado);

        if (planos == null || planos.isEmpty()) {
            mostrarAlerta("Informação", "Nenhum plano de treino encontrado.", Alert.AlertType.INFORMATION);
            listViewPlanos.setItems(FXCollections.observableArrayList());
            return;
        }

        ObservableList<String> itens = FXCollections.observableArrayList();
        for (PlanoTreino plano : planos) {
            String itemTexto = String.format(
                    "%s | Início: %s | Fim: %s",
                    plano.getNomePlano(),
                    plano.getInicioPlano(),
                    plano.getFimPlano()
            );
            itens.add(itemTexto);
        }

        listViewPlanos.setItems(itens);
    }

    @FXML
    private void handleFechar() {
        Stage atual = (Stage) fechar.getScene().getWindow();
        atual.close();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}