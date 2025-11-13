package br.upe.academia2.ui.controllers;
import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ModificarExercicioController {
    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;
    @FXML private Button btnBuscar;
    @FXML private ImageView gifPreview;
    @FXML private Button btnImportar;

    private static final String STYLE = "-fx-background-color: #FFFFFF;";
    private Stage stageAnterior;
    private final ExercicioBusiness exercicio = new ExercicioBusiness();
    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }
    private String novoCaminhoGif = null;
    private Exercicio existente;

    @FXML
    public void handleBuscar() {
        String nome = nomeField.getText();
        this.existente = exercicio.listarExercicios().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            mensagemLabel.setText("Exercicio não encontrado.");
            descricaoField.clear();
            gifPreview.setImage(null);
            gifPreview.setStyle(STYLE);
        } else {
            // Carrega os dados atuais nos campos
            descricaoField.setText(existente.getDescricao());
            if (existente.getCaminhoGif() != null) {
                Image image = new Image(existente.getCaminhoGif());
                gifPreview.setImage(image);
                gifPreview.setStyle(STYLE);
            }
            mensagemLabel.setText("Exercício carregado. Modifique os campos.");
            novoCaminhoGif = null;
        }
    }

    @FXML
    public void handleModificar() {
        String nome = nomeField.getText();
        this.existente = exercicio.listarExercicios().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            mensagemLabel.setText("Exercicio não encontrado.");
            return;
        }

        String descricao = descricaoField.getText();
        if (!nome.isBlank()) existente.setNome(nome);
        if (!descricao.isBlank()) existente.setDescricao(descricao);
        if (novoCaminhoGif != null && !novoCaminhoGif.isBlank()) {
            existente.setCaminhoGif(novoCaminhoGif);
        }

        exercicio.atualizarExercicio(existente);
        exercicio.salvarAlteracoesNoCsv();
        Stage stageAtual = (Stage) nomeField.getScene().getWindow();
        stageAtual.close();
        mensagemLabel.setText("Exercicio modificado com sucesso!");
        nomeField.clear(); descricaoField.clear();
    }

    @FXML
    public void handleImportarGif() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Novo GIF do Exercício");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens GIF", "*.gif")
        );

        Stage stage = (Stage) btnImportar.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {

            novoCaminhoGif = file.toURI().toString();
            Image image = new Image(novoCaminhoGif);
            gifPreview.setImage(image);
            gifPreview.setStyle(STYLE);
            mensagemLabel.setText("Novo GIF selecionado.");
        }
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}