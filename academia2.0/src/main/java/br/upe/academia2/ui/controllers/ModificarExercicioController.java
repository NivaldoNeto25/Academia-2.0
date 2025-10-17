package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ModificarExercicioController extends BaseController {

    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private TextField caminhoGifField;

    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    @FXML
    public void handleModificar() {
        String nome = nomeField.getText();
        Exercicio existente = exercicio.listarExercicios().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            mensagemLabel.setText("Exercício não encontrado.");
            return;
        }

        String descricao = descricaoField.getText();
        String gif = caminhoGifField.getText();

        if (!nome.isBlank()) existente.setNome(nome);
        if (!descricao.isBlank()) existente.setDescricao(descricao);
        if (!gif.isBlank()) existente.setCaminhoGif(gif);

        exercicio.atualizarExercicio(existente);
        exercicio.salvarAlteracoesNoCsv();

        mensagemLabel.setText("Exercício modificado com sucesso!");
        nomeField.clear();
        descricaoField.clear();
        caminhoGifField.clear();
    }
}