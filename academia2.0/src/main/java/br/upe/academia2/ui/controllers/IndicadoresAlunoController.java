package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.IndBioRepoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndicadoresAlunoController implements AlunoMenuController.UsuarioDependente {

    private Usuario usuarioLogado;
    private IndBioRepoImpl repo = new IndBioRepoImpl("db/usuario.csv");

    @FXML private Button btnCadastrar;
    @FXML private Button btnImportar;
    @FXML private TableView<IndicadorBiomedico> tabelaIndicadores;
    @FXML private TableColumn<IndicadorBiomedico, String> colData;
    @FXML private TableColumn<IndicadorBiomedico, Double> colPeso;
    @FXML private TableColumn<IndicadorBiomedico, Double> colAltura;
    @FXML private TableColumn<IndicadorBiomedico, Double> colPercentualGordura;
    @FXML private TableColumn<IndicadorBiomedico, Double> colPercentualMassaMagra;

    Logger logger = Logger.getLogger(IndicadoresAlunoController.class.getName());

    @FXML
    public void initialize() {
        colData.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return new javafx.beans.property.SimpleStringProperty(sdf.format(cellData.getValue().getDataRegistro()));
        });
        colPeso.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("peso"));
        colAltura.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("altura"));
        colPercentualGordura.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("percentualGordura"));
        colPercentualMassaMagra.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("percentualMassaMagra"));
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        atualizarTabela();
    }

    public void atualizarTabela() {
        if (usuarioLogado != null) {
            List<IndicadorBiomedico> todos = repo.findAll();
            List<IndicadorBiomedico> filtraUsuario = todos.stream()
                    .filter(ind -> ind.getEmail().equals(usuarioLogado.getEmail()))
                    .toList();
            ObservableList<IndicadorBiomedico> obsList = FXCollections.observableArrayList(filtraUsuario);
            tabelaIndicadores.setItems(obsList);
        }
    }

    @FXML
    public void handleCadastrarIndicadores() {
        abrirTelaModal("/fxml/CadastrarIndicadoresBio.fxml", "Cadastrar Indicador");
    }

    @FXML
    public void handleImportarIndicadores() {
        abrirTelaModal("/fxml/ImportarIndicadoresBio.fxml", "Importar Indicadores CSV");
    }

    public void abrirTelaModal(String caminhoFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            invocarMetodoSeExiste(controller, "setUsuarioLogado", Usuario.class, this.usuarioLogado);

            Stage modalStage = new Stage();
            modalStage.setTitle(titulo);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            modalStage.setOnHiding(event -> atualizarTabela());
            modalStage.show();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar a tela modal: " + caminhoFxml, e);
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