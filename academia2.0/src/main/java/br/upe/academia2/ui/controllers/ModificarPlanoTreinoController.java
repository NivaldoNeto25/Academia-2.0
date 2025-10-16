package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModificarPlanoTreinoController {

    @FXML private Button btnAlterarNome;
    @FXML private Button btnAlterarDatas;
    @FXML private Button btnAdicionarExercicio;
    @FXML private Button btnRemoverExercicio;
    @FXML private Button btnVoltar;

    private Stage stageAnterior;
    private Usuario usuarioLogado;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    Logger logger = Logger.getLogger(ModificarPlanoTreinoController.class.getName());

    @FXML
    public void initialize() {
        btnAlterarNome.setOnAction(e -> abrirTelaAlterarNomePlano());
        btnAlterarDatas.setOnAction(e -> abrirTelaAlterarDatasPlano());
        btnAdicionarExercicio.setOnAction(e -> abrirTelaAdicionarExercicio());
        btnRemoverExercicio.setOnAction(e -> abrirTelaRemoverExercicio());
    }

    public void abrirTelaAlterarNomePlano() {
        abrirTela("/fxml/ModificarNomePlano.fxml", "Alterar Nome do Plano");
    }

    public void abrirTelaAlterarDatasPlano() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AlterarDatas.fxml"));
            Parent root = loader.load();

            br.upe.academia2.ui.controllers.AlterarDatasController controller = loader.getController();
            controller.setUsuarioLogado(usuarioLogado);
            controller.setStageAnterior(stageAnterior);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Alterar Datas do Plano");
            stage.show();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar a tela", e);
        }
    }

    public void abrirTelaAdicionarExercicio() {
        abrirTela("/fxml/AdicionarExercicioSecao.fxml", "Adicionar Exercício");
    }

    public void abrirTelaRemoverExercicio() {
        abrirTela("/fxml/RemoverExercicioSecao.fxml", "Remover Exercício");
    }

    public void abrirTela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            invocarMetodoSeExiste(controller, "setUsuarioLogado", Usuario.class, usuarioLogado);
            invocarMetodoSeExiste(controller, "setStageAnterior", Stage.class, stageAnterior);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException ex) {
            logger.log(Level.WARNING, "Erro ao carregar a tela", ex);
        }
    }

    private void invocarMetodoSeExiste(Object objeto, String metodoNome, Class<?> parametroClass, Object parametro) {
        try {
            Method metodo = objeto.getClass().getMethod(metodoNome, parametroClass);
            metodo.invoke(objeto, parametro);
        } catch (NoSuchMethodException | IllegalAccessException ignored) {
            logger.log(Level.WARNING, "Método não encontrado", ignored);
        } catch (InvocationTargetException e){
            logger.log(Level.WARNING, "Erro ao chamar o método", e);
        }
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}