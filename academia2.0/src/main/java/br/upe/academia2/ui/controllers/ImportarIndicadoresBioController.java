package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.File;

public class ImportarIndicadoresBioController {

    @FXML private TextField campoArquivo;
    @FXML private Button btnImportar;
    @FXML private Label mensagemLabel;

    private final IndicadorBiomedicoBusiness indicadorBusiness = new IndicadorBiomedicoBusiness();

    public void setUsuarioLogado(Usuario usuario) {}

    public void setUsuario(Usuario usuario) {
        setUsuarioLogado(usuario);
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
    public void handleSelecionarArquivo(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv")
        );
        Stage stage = (Stage) campoArquivo.getScene().getWindow();
        File arquivo = fileChooser.showOpenDialog(stage);
        if (arquivo != null) {
            campoArquivo.setText(arquivo.getAbsolutePath());
        }
    }
}