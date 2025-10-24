package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.SecaoTreino;
import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import java.util.stream.Collectors;


import java.util.List;

public class SecaoExercicioController {

    @FXML private ComboBox<SecaoTreino> comboSecao;  // ComboBox para escolher a seção
    @FXML private ListView<String> listaExercicios;   // ListView para exibir os exercícios
    @FXML private Button btnEscolherExercicio;        // Botão para escolher o exercício
    @FXML private Button btnVoltar;                   // Botão de Voltar

    private List<SecaoTreino> secoes;  // Lista de seções
    private SecaoTreino secaoSelecionada;  // Seção atualmente selecionada

    // Método para carregar as seções no ComboBox
    public void setSecoes(List<SecaoTreino> secoes) {
        this.secoes = secoes;
        comboSecao.setItems(FXCollections.observableArrayList(secoes));  // Popula o ComboBox com as seções
    }

    // Inicializa os elementos da interface
    @FXML
    public void initialize() {
        comboSecao.setOnAction(e -> carregarExercicios());  // Ao selecionar uma seção, carrega os exercícios
        btnEscolherExercicio.setOnAction(e -> escolherExercicio());  // Ação de escolher exercício
        btnVoltar.setOnAction(e -> voltar());  // Ação de voltar
    }

    // Carrega os exercícios da seção selecionada no ListView
    private void carregarExercicios() {
        secaoSelecionada = comboSecao.getValue();
        if (secaoSelecionada != null) {
            List<String> exercicios = secaoSelecionada.getItensPlano().stream()
                    .map(item -> item.getExercicio().getNome())
                    .collect(Collectors.toList());

            listaExercicios.setItems(FXCollections.observableArrayList(exercicios));  // Atualiza o ListView
        }
    }

    // Método para escolher o exercício selecionado
    private void escolherExercicio() {
        String exercicioSelecionado = listaExercicios.getSelectionModel().getSelectedItem();
        if (exercicioSelecionado != null) {
            // Exibe uma mensagem ou abre a tela de detalhes do exercício
            mostrarAlerta("Exercício Selecionado", "Você escolheu o exercício: " + exercicioSelecionado, AlertType.INFORMATION);
        } else {
            mostrarAlerta("Erro", "Por favor, selecione um exercício para continuar.", AlertType.WARNING);
        }
    }

    // Método para voltar à tela anterior
    private void voltar() {
        Stage stage = (Stage) btnVoltar.getScene().getWindow();
        stage.close();  // Fecha a tela atual
    }

    // Método para exibir uma mensagem de alerta
    private void mostrarAlerta(String titulo, String mensagem, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
