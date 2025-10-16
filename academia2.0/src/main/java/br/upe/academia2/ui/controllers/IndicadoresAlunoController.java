package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class IndicadoresAlunoController {

    private Usuario usuarioLogado;
    private Stage stageAnterior;

    @FXML private Button btnVoltar;
    @FXML private Button btnCadastrar;
    @FXML private Button btnListar;
    @FXML private Button btnImportar;


    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    public void handleCadastrarIndicadores() {
        abrirTela("/fxml/CadastrarIndicadoresBio.fxml", "Cadastrar Indicadores", btnCadastrar);
    }

    @FXML
    public void handleListarIndicadores() {
        abrirTela("/fxml/ListarIndicadoresBio.fxml", "Listar Indicadores", btnListar);
    }

    @FXML
    public void handleImportarIndicadores() {
        abrirTela("/fxml/ImportarIndicadoresBio.fxml", "Importar Indicadores", btnImportar);
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }

    public void abrirTela(String caminhoFxml, String titulo, Button origem) {
        try {
            Stage stageAtual = (Stage) origem.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();

            Object controller = loader.getController();

            try {
                controller.getClass().getMethod("setUsuarioLogado", Usuario.class).invoke(controller, usuarioLogado);
            } catch (NoSuchMethodException ignored) {}

            try {
                controller.getClass().getMethod("setStageAnterior", Stage.class).invoke(controller, stageAtual);
            } catch (NoSuchMethodException ignored) {}

            Stage novaStage = new Stage();
            novaStage.setTitle(titulo);
            novaStage.setScene(new Scene(root));
            novaStage.show();

            stageAtual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
