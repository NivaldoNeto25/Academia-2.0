package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastrarExercicioController {
    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private TextField caminhoGifField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;
    @FXML private Button btnCadastrar;

    private Stage stageAnterior;
    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    private void handleCadastrar() {
        String nome = nomeField.getText();
        String descricao = descricaoField.getText();
        String caminhoGif = caminhoGifField.getText();

        if (nome.isBlank() || descricao.isBlank() || caminhoGif.isBlank()) {
            mensagemLabel.setText("Todos os campos são obrigatórios.");
            return;
        }

        if (exercicio.listarExercicios().stream().anyMatch(u -> u.getNome().equalsIgnoreCase(nome))) {
            mensagemLabel.setText("Este exercício já existe");
            return;}
        try {
            Exercicio novoExercicio = new Exercicio(nome, descricao, caminhoGif);
            exercicio.salvar(novoExercicio);
            exercicio.salvarAlteracoesNoCsv();
            mensagemLabel.setText("Exercicio cadastrado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace(); // É bom manter o log do erro no console para depuração
        }
    }
        @FXML
        private void handleVoltar() {
            Stage atual = (Stage) btnVoltar.getScene().getWindow();
            atual.close();
            if (stageAnterior != null) stageAnterior.show();
        }

    }