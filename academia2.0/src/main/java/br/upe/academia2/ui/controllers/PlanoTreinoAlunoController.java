package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PlanoTreinoAlunoController {
    private Usuario usuario;
    private Stage stageAnterior;
    @FXML private Button btnCadastrar;
    @FXML private Button btnVoltar;
    @FXML private Button btnListar;
    @FXML private Button btnModificar;
    @FXML private Button btnSecaoTreino;

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML private void handleCadastrarPlano() { irParaTela("/fxml/CadastrarPlanoTreino.fxml", "Cadastrar Plano de Treino", btnCadastrar);}
    @FXML private void handleListarPlano() {irParaTela("/fxml/ListarPlanoTreino.fxml", "Listar Plano de Treino", btnListar);}
    @FXML private void handleModificarPlano() {irParaTela("/fxml/ModificarPlanoTreino.fxml", "Modificar Plano de Treino", btnModificar);}
    @FXML private void handleSecaoTreino() {irParaTela("/fxml/SecaoTreino.fxml", "Cadastrar Plano de Treino", btnSecaoTreino);}

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }

    public void irParaTela(String caminhoFxml, String titulo, Button origem) {
        try {
            Stage stageAtual = (Stage) origem.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Object controller = loader.getController();

            controller.getClass().getMethod("setStageAnterior", Stage.class).invoke(controller, stageAtual);

            try {
                controller.getClass().getMethod("setUsuario", Usuario.class).invoke(controller, this.usuario);
            } catch (NoSuchMethodException e1) {
                try {
                    controller.getClass().getMethod("setUsuarioLogado", Usuario.class).invoke(controller, this.usuario);
                } catch (NoSuchMethodException e2) {
                }
            }

            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            stageAtual.close();
            novaStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}