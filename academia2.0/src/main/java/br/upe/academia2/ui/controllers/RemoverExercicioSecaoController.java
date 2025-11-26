package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.*;

import br.upe.academia2.data.repository.PlanoTreinoJpaRepository;
import br.upe.academia2.data.repository.UsuarioJpaRepository;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class RemoverExercicioSecaoController {

    @FXML private ComboBox<String> secaoComboBox;
    @FXML private ComboBox<String> exerciciosComboBox;
    @FXML private Button btnRemover;
    @FXML private Button btnVoltar;

    
    private PlanoTreino planoParaModificar; // MUDANÇA DE NOME

    private PlanoTreinoBusiness planoTreinoBusiness;

    @FXML
    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioJpaRepository.getInstance(),
                new PlanoTreinoJpaRepository()
        );

        btnRemover.setOnAction(e -> removerExercicio());
        btnVoltar.setOnAction(e -> voltar());

        secaoComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> atualizarListaExercicios(newVal));
    }

    
    public void setPlanoParaModificar(PlanoTreino plano) {
        this.planoParaModificar = plano;
        carregarSecoes(); 
    }

    private void carregarSecoes() {
        secaoComboBox.getItems().clear();
        if (this.planoParaModificar != null) {
            for (SecaoTreino secao : planoParaModificar.getSecoes()) {
                secaoComboBox.getItems().add(secao.getNomeTreino());
            }
        }
    }


    public void atualizarListaExercicios(String nomeSecao) {
        exerciciosComboBox.getItems().clear();

        if (nomeSecao == null || nomeSecao.trim().isEmpty() || this.planoParaModificar == null) {
            return;
        }

        SecaoTreino secao = planoParaModificar.getSecaoPorNome(nomeSecao);
        if (secao == null) {
            return;
        }

        List<ItemPlanoTreino> itens = secao.getItensPlano();
        for (ItemPlanoTreino item : itens) {
            exerciciosComboBox.getItems().add(item.getExercicio().getNome());
        }
    }

    public void removerExercicio() {
        String nomeSecao = secaoComboBox.getValue();
        String nomeExercicio = exerciciosComboBox.getValue();

        if (nomeSecao == null || nomeExercicio == null) {
            mostrarAlerta("Erro", "Informe a seção e selecione um exercício para remover.", Alert.AlertType.WARNING);
            return;
        }
        
        // MUDANÇA: Usa o 'planoParaModificar'
        SecaoTreino secao = planoParaModificar.getSecaoPorNome(nomeSecao);
        if (secao == null) {
            mostrarAlerta("Erro", "Seção não encontrada no plano.", Alert.AlertType.ERROR);
            return;
        }

        boolean removido = secao.getItensPlano().removeIf(item -> item.getExercicio().getNome().equals(nomeExercicio));

        if (removido) {
            planoTreinoBusiness.modificarPlanoDeTreino(planoParaModificar); // Salva o plano modificado
            mostrarAlerta("Sucesso", "Exercício removido com sucesso!", Alert.AlertType.INFORMATION);
            atualizarListaExercicios(nomeSecao);
        } else {
            mostrarAlerta("Erro", "Exercício não encontrado na seção.", Alert.AlertType.ERROR);
        }
    }

    public void voltar(){
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