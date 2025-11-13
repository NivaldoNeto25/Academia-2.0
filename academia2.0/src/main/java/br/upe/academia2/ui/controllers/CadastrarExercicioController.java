package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.image.Image;

public class CadastrarExercicioController {
    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private TextField caminhoGifField;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;
    @FXML private Button btnCadastrar;
    @FXML private ImageView gifPreview;
    @FXML private Button btnImportar;

    private String caminhoGifSelecionado;

    private Stage stageAnterior;
    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    public void handleImportarGif() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar GIF do Exercício");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens GIF", "*.gif")
        );

        File file = fileChooser.showOpenDialog(btnImportar.getScene().getWindow());

        if (file != null) {
            caminhoGifSelecionado = file.toURI().toString();

            Image image = new Image(caminhoGifSelecionado);
            gifPreview.setImage(image);

            gifPreview.setStyle("-fx-background-color: #FFFFFF;");
            mensagemLabel.setText("");
        }
    }

    @FXML
    public void handleCadastrar() {
        String nome = nomeField.getText();
        String descricao = descricaoField.getText();
        String caminhoGif = caminhoGifSelecionado;

        if (nome.isBlank() || descricao.isBlank() || caminhoGif.isBlank()) {
            mensagemLabel.setText("Todos os campos são obrigatórios.");
            return;
        }

        if (exercicio.listarExercicios().stream().anyMatch(u -> u.getNome().equalsIgnoreCase(nome))) {
            mensagemLabel.setText("Este exercício já existe");
            return;}

        Exercicio novoExercicio = new Exercicio(nome, descricao, caminhoGif);
        exercicio.salvar(novoExercicio);
        exercicio.salvarAlteracoesNoCsv();
        mensagemLabel.setText("Exercicio cadastrado com sucesso!");

        Stage stageAtual = (Stage) btnCadastrar.getScene().getWindow();
        stageAtual.close();
    }
        @FXML
        public void handleVoltar() {
            Stage atual = (Stage) btnVoltar.getScene().getWindow();
            atual.close();
            if (stageAnterior != null) stageAnterior.show();
        }

    }