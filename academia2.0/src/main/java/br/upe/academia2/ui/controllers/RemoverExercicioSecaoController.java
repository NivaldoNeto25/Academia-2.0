package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.*;

import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class RemoverExercicioSecaoController {

    @FXML private TextField secaoField;
    @FXML private ComboBox<String> exerciciosComboBox;
    @FXML private Button btnRemover;
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;
    private Stage stageAnterior;

    private PlanoTreinoBusiness planoTreinoBusiness;

    @FXML
    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );

        btnRemover.setOnAction(e -> removerExercicio());
        btnVoltar.setOnAction(e -> voltar());

        secaoField.textProperty().addListener((obs, oldText, newText) -> atualizarListaExercicios(newText));
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void setStageAnterior(Stage stage) {
        this.stageAnterior = stage;
    }

    private void atualizarListaExercicios(String nomeSecao) {
        exerciciosComboBox.getItems().clear();

        if (nomeSecao == null || nomeSecao.trim().isEmpty()) {
            return;
        }

        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);

        if (plano == null) {
            return;
        }

        SecaoTreino secao = plano.getSecaoPorNome(nomeSecao);
        if (secao == null) {
            return;
        }

        List<ItemPlanoTreino> itens = secao.getItensPlano();
        for (ItemPlanoTreino item : itens) {
            exerciciosComboBox.getItems().add(item.getExercicio().getNome());
        }
    }

    private void removerExercicio() {
        String nomeSecao = secaoField.getText().trim();
        String nomeExercicio = exerciciosComboBox.getValue();

        if (nomeSecao.isEmpty() || nomeExercicio == null) {
            mostrarAlerta("Erro", "Informe a seção e selecione um exercício para remover.", Alert.AlertType.WARNING);
            return;
        }

        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
        if (plano == null) {
            mostrarAlerta("Erro", "Plano de treino não encontrado.", Alert.AlertType.ERROR);
            return;
        }

        SecaoTreino secao = plano.getSecaoPorNome(nomeSecao);
        if (secao == null) {
            mostrarAlerta("Erro", "Seção não encontrada no plano.", Alert.AlertType.ERROR);
            return;
        }

        boolean removido = secao.getItensPlano().removeIf(item -> item.getExercicio().getNome().equals(nomeExercicio));

        if (removido) {
            planoTreinoBusiness.modificarPlanoDeTreino(plano);
            mostrarAlerta("Sucesso", "Exercício removido com sucesso!", Alert.AlertType.INFORMATION);
            atualizarListaExercicios(nomeSecao);
        } else {
            mostrarAlerta("Erro", "Exercício não encontrado na seção.", Alert.AlertType.ERROR);
        }
    }

    private void voltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) {
            stageAnterior.show();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}