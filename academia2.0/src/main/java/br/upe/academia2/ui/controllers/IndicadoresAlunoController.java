package br.upe.academia2.ui.controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    Logger logger = Logger.getLogger(IndicadoresAlunoController.class.getName());


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

            invocarMetodoSeExiste(controller, "setUsuarioLogado", Usuario.class, usuarioLogado);
            invocarMetodoSeExiste(controller, "setStageAnterior", Stage.class, stageAtual);

            Stage novaStage = new Stage();
            novaStage.setTitle(titulo);
            novaStage.setScene(new Scene(root));
            novaStage.show();

            stageAtual.close();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro a o carregar FXML", e);
        }
    }

    private void invocarMetodoSeExiste(Object objeto, String metodoNome, Class<?> parametroClass, Object parametro) {
        try {
            var metodo = objeto.getClass().getMethod(metodoNome, parametroClass);
            metodo.invoke(objeto, parametro);
        } catch (NoSuchMethodException ignored) {
            logger.log(Level.WARNING, "Método não encontrado: ", ignored);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Erro ao chamar o método", e);
        }
    }
}