package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

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

    @FXML
    private void initialize() {
        btnAlterarNome.setOnAction(e -> abrirTelaAlterarNomePlano());
        btnAlterarDatas.setOnAction(e -> abrirTelaAlterarDatasPlano());
        btnAdicionarExercicio.setOnAction(e -> abrirTelaAdicionarExercicio());
        btnRemoverExercicio.setOnAction(e -> abrirTelaRemoverExercicio());
    }

    private void abrirTelaAlterarNomePlano() {
        abrirTela("/fxml/ModificarNomePlano.fxml", "Alterar Nome do Plano");
    }

    private void abrirTelaAlterarDatasPlano() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AlterarDatasPlano.fxml"));
            Parent root = loader.load();

            br.upe.academia2.ui.controllers.AlterarDatasController controller = loader.getController();
            controller.setUsuarioLogado(usuarioLogado);
            controller.setStageAnterior(stageAnterior);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Alterar Datas do Plano");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaAdicionarExercicio() {
        abrirTela("/fxml/AdicionarExercicioPlano.fxml", "Adicionar Exercício");
    }

    private void abrirTelaRemoverExercicio() {
        abrirTela("/fxml/RemoverExercicioPlano.fxml", "Remover Exercício");
    }

    private void abrirTela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            try {
                controller.getClass().getMethod("setUsuarioLogado", Usuario.class).invoke(controller, usuarioLogado);
            } catch (NoSuchMethodException ignored) {}

            try {
                controller.getClass().getMethod("setStageAnterior", Stage.class).invoke(controller, stageAnterior);
            } catch (NoSuchMethodException ignored) {}

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}