package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CadastrarExercicioController extends BaseController {

    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private TextField caminhoGifField;
    @FXML private Button btnCadastrar;

    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    @FXML
    public void handleCadastrar() {
        String nome = nomeField.getText();
        String descricao = descricaoField.getText();
        String caminhoGif = caminhoGifField.getText();

        if (nome.isBlank() || descricao.isBlank() || caminhoGif.isBlank()) {
            mensagemLabel.setText("Todos os campos são obrigatórios.");
            return;
        }

        if (exercicio.listarExercicios().stream()
                .anyMatch(u -> u.getNome().equalsIgnoreCase(nome))) {
            mensagemLabel.setText("Este exercício já existe.");
            return;
        }

        Exercicio novoExercicio = new Exercicio(nome, descricao, caminhoGif);
        exercicio.salvar(novoExercicio);
        exercicio.salvarAlteracoesNoCsv();
        mensagemLabel.setText("Exercício cadastrado com sucesso!");

        nomeField.clear();
        descricaoField.clear();
        caminhoGifField.clear();
    }
}
