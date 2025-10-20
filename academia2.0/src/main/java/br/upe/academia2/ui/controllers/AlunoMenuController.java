package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlunoMenuController implements Initializable {

    private Usuario aluno;

    @FXML
    private BorderPane mainPane; // Referência ao nosso container principal

    @FXML
    private ToggleGroup menuToggleGroup; // Grupo para os botões de navegação

    @FXML
    private ToggleButton btnPerfil;

    @FXML
    private Button btnSair;
    
    Logger logger = Logger.getLogger(AlunoMenuController.class.getName());

    public void setAluno(Usuario aluno) {
        this.aluno = aluno;
        // Você pode carregar informações do aluno na tela aqui, se necessário
    }

    // Este método é chamado automaticamente depois que o FXML é carregado
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Garante que o botão de perfil seja o primeiro a ser selecionado e carregado
        btnPerfil.setSelected(true); 
        //loadContent("/fxml/Perfil.fxml"); // Carrega a tela de perfil por padrão
    }

    @FXML
    public void handlePerfil(ActionEvent event) {
        //loadContent("/fxml/Perfil.fxml");
    }

    @FXML
    public void handleExercicio(ActionEvent event) {
        //loadContent("/fxml/Exerciciotest.fxml");
    }

    @FXML
    public void handlePlanoTreino(ActionEvent event) {
        //loadContent("/fxml/PlanoTreinoAluno.fxml");
    }
    
    @FXML
    public void handleSecao(ActionEvent event) {
        // Crie um FXML para "Seção" se necessário
        // loadContent("/fxml/Secao.fxml");
        //System.out.println("Botão Seção clicado. Crie o FXML correspondente.");
    }

    @FXML
    public void handleSair() {
        Stage stageAtual = (Stage) btnSair.getScene().getWindow();
        stageAtual.close();
    }

    /**
     * Carrega um arquivo FXML no painel central do BorderPane.
     * @param fxmlPath O caminho para o arquivo FXML a ser carregado.
     */
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainPane.setCenter(view); // A MÁGICA ACONTECE AQUI!

            // Se o controller do FXML carregado precisar do objeto Aluno, podemos passá-lo
            //PerfilController controller = loader.getController();
            //controller.setAluno(this.aluno);

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar o FXML: " + fxmlPath, e);
            // Opcional: mostrar uma tela de erro no painel central
            mainPane.setCenter(new javafx.scene.control.Label("Erro ao carregar a página."));
        }
    }
}