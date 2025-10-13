package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Adm; // importar a classe Adm
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdmMenuController {

    @FXML private Button btnCadastrar;
    @FXML private Button btnListar;
    @FXML private Button btnModificar;
    @FXML private Button btnExcluir;
    @FXML private Button btnVoltar;

    private Adm adm; // atributo para armazenar o usuário Adm logado

    // Método para injetar o Adm logado
    public void setAdm(Adm adm) {
        this.adm = adm;
        // Aqui pode adicionar código para atualizar alguma info na UI usando o adm, se desejar
    }

    @FXML private void handleCadastrarAluno() { irParaTela("/fxml/CadastrarAluno.fxml", "Cadastrar Aluno", btnCadastrar); }
    @FXML private void handleListarAlunos()    { irParaTela("/fxml/ListarAlunos.fxml", "Listar Alunos", btnListar); }
    @FXML private void handleModificarAluno()  { irParaTela("/fxml/ModificarAluno.fxml", "Modificar Aluno", btnModificar); }
    @FXML private void handleExcluirAluno()    { irParaTela("/fxml/ExcluirAluno.fxml", "Excluir Aluno", btnExcluir); }

    @FXML
    private void handleVoltar() {
        Stage stageAtual = (Stage) btnVoltar.getScene().getWindow();
        stageAtual.close();
    }

    private void irParaTela(String caminhoFxml, String titulo, Button origem) {
        try {
            Stage stageAtual = (Stage) origem.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            controller.getClass().getMethod("setStageAnterior", Stage.class).invoke(controller, stageAtual);
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