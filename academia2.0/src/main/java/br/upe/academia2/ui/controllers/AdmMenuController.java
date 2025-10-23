package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdmMenuController {

    @FXML private Button btnCadastrar;
    @FXML private Button btnEditar;
    @FXML private Button btnExcluir;
    @FXML private Button btnSair;
    @FXML private TextField searchField;
    @FXML private TableView<Usuario> alunosTable;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;

    Logger logger = Logger.getLogger(AdmMenuController.class.getName());

    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioCsvRepository.getInstance());
    private ObservableList<Usuario> alunosList;

    public void setAdm(Adm adm) {
        // Método para configurar o administrador atual, se necessário
    }

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        alunosList = FXCollections.observableArrayList(usuarioBusiness.listarUsuariosComuns());
        alunosTable.setItems(alunosList);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                alunosTable.setItems(alunosList);
            } else {
                var filtrados = alunosList.filtered(u ->
                        u.getNome().toLowerCase().contains(newVal.toLowerCase()) ||
                                u.getEmail().toLowerCase().contains(newVal.toLowerCase()));
                alunosTable.setItems(filtrados);
            }
        });
    }

    public void atualizarTabelaAlunos() {
        alunosList.setAll(usuarioBusiness.listarUsuariosComuns());
    }

    @FXML
    private void handleCadastrarAluno() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CadastroAluno.fxml"));
            Scene cadastroScene = new Scene(loader.load());
            Stage cadastroStage = new Stage();
            cadastroStage.setTitle("Cadastro de Aluno");
            cadastroStage.setScene(cadastroScene);

            CadastroAlunoController controller = loader.getController();
            controller.setStageAnterior((Stage) btnCadastrar.getScene().getWindow());
            controller.setAdmMenuController(this);

            cadastroStage.initOwner(btnCadastrar.getScene().getWindow());
            cadastroStage.setResizable(false);
            cadastroStage.show();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao abrir tela de cadastro de aluno", e);
        }
    }

    @FXML
    private void handleEditarAluno() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModificarAluno.fxml"));
            Scene editarScene = new Scene(loader.load());

            ModificarAlunoController controller = loader.getController();
            controller.setStageAnterior((Stage) btnEditar.getScene().getWindow());
            controller.setAdmMenuController(this);

            Stage editarStage = new Stage();
            editarStage.setTitle("Modificar Aluno");
            editarStage.setScene(editarScene);
            editarStage.initOwner(btnEditar.getScene().getWindow());
            editarStage.setResizable(false);
            editarStage.show();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao abrir tela de edição de aluno", e);
        }
    }

    @FXML
    private void handleExcluirAluno() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExcluirAluno.fxml"));
            Scene excluirScene = new Scene(loader.load());

            ExcluirAlunoController controller = loader.getController();
            controller.setAdmMenuController(this);

            Stage excluirStage = new Stage();
            excluirStage.setTitle("Excluir Aluno");
            excluirStage.setScene(excluirScene);
            excluirStage.initOwner(btnExcluir.getScene().getWindow());
            excluirStage.setResizable(false);
            excluirStage.show();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao abrir tela de exclusão de aluno", e);
        }
    }

    @FXML
    public void handleSair() {
        Stage stageAtual = (Stage) btnSair.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            stageAtual.setScene(loginScene);
            stageAtual.setTitle("Academia 2.0 - Login");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao voltar para a tela de login.", e);
            stageAtual.close();
        }
    }
}
