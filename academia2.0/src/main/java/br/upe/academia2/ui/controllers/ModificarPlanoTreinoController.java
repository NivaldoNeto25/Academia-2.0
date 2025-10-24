package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.PlanoTreino;
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
import javafx.stage.Modality;
import java.util.logging.Logger;

public class ModificarPlanoTreinoController {

    @FXML private Button btnAlterarNome;
    @FXML private Button btnAlterarDatas;
    @FXML private Button btnAdicionarExercicio;
    @FXML private Button btnRemoverExercicio;
    @FXML private Button btnVoltar;

    private PlanoTreino planoParaModificar;

    public void setPlanoParaModificar(PlanoTreino plano) {
        this.planoParaModificar = plano;
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
        abrirTela("/fxml/AlterarDatas.fxml", "Alterar Datas do Plano");
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
            // Passa o plano para modificar para o novo controller
            invocarMetodoSeExiste(controller, "setPlanoParaModificar", PlanoTreino.class, planoParaModificar);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); 

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
    }
}