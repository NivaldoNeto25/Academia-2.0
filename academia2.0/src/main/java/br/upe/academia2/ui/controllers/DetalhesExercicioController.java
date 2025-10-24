package br.upe.academia2.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

public class DetalhesExercicioController {

    @FXML private TextField tfRepeticoes;
    @FXML private TextField tfCarga;
    @FXML private TextField tfSeries;
    @FXML private Button btnSalvar;

    private String exercicioSelecionado;

    public void setExercicioSelecionado(String exercicio) {
        this.exercicioSelecionado = exercicio;
        // Preenche os campos com as informações do exercício, se disponíveis
    }

    @FXML
    public void initialize() {
        btnSalvar.setOnAction(e -> salvarDetalhes());
    }

    private void salvarDetalhes() {
        int rep = Integer.parseInt(tfRepeticoes.getText());
        double carga = Double.parseDouble(tfCarga.getText());
        int series = Integer.parseInt(tfSeries.getText());

        // Exemplo de comparação com dados já salvos
        boolean dadosDiferentes = verificarDiferencas(rep, carga, series);
        if (dadosDiferentes) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Atualizar Dados");
            alert.setHeaderText("Deseja atualizar as informações?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    atualizarDados(rep, carga, series);
                }
            });
        }
    }

    private boolean verificarDiferencas(int rep, double carga, int series) {
        // Verifique se os dados informados são diferentes dos já salvos
        return rep != 10 || carga != 50.0 || series != 3;
    }

    private void atualizarDados(int rep, double carga, int series) {
        // Atualizar os dados no sistema ou banco de dados
    }
}
