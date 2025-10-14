package br.upe.academia2.ui.controllers;
import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModificarExercicioController {
    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private TextField caminhoGifField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;

    private Stage stageAnterior;
    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    private void handleModificar() {
        String nome = nomeField.getText();
        Exercicio existente = exercicio.listarExercicios().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            mensagemLabel.setText("Exercicio não encontrado.");
            return;
        }

        String descricao = descricaoField.getText();
        String gif = caminhoGifField.getText();
        if (!nome.isBlank()) existente.setNome(nome);
        if (!descricao.isBlank()) existente.setDescricao(descricao);
        if (!descricao.isBlank()) existente.setCaminhoGif(gif);

        exercicio.atualizarExercicio(existente);
        exercicio.salvarAlteracoesNoCsv();
        mensagemLabel.setText("Exercicio modificado com sucesso!");
        nomeField.clear(); descricaoField.clear(); caminhoGifField.clear();
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}