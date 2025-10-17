package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImportarIndicadoresBioController {

    @FXML private TextField campoArquivo;
    @FXML private Button btnImportar;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;

    private final IndicadorBiomedicoBusiness indicadorBusiness = new IndicadorBiomedicoBusiness();

    private Stage stageAnterior;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    public void handleImportar() {
        String caminho = campoArquivo.getText();
        if (caminho == null || caminho.isBlank()) {
            mensagemLabel.setText("Selecione um arquivo CSV válido.");
            return;
        }

        boolean sucesso = indicadorBusiness.importarIndicadoresDeCSV(campoArquivo.getText());
        if (sucesso) {
            mensagemLabel.setText("Importação realizada com sucesso!");
        } else {
            mensagemLabel.setText("Erro ao importar dados. Verifique o arquivo.");
        }
    }

    @FXML
    public void handleSelecionarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv")
        );

        File arquivo = fileChooser.showOpenDialog(null);
        if (arquivo != null) {
            campoArquivo.setText(arquivo.getAbsolutePath());
        }
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}