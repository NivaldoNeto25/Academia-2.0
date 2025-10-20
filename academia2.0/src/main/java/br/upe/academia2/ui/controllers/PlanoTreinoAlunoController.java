package br.upe.academia2.ui.controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.SecaoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class PlanoTreinoAlunoController implements Initializable{
    private Usuario usuarioLogado;
    private List<PlanoTreino> planosDoUsuario;
    private PlanoTreinoBusiness planoTreinoBusiness;
    private Stage stageAnterior;

    @FXML private Button btnCadastrar;
    @FXML private Button btnModificar;
    @FXML private ComboBox<String> comboPlanos;
    @FXML private TableView<ItemPlanoTreino> tabelaExercicios;
    @FXML private TableColumn<ItemPlanoTreino, String> colunaExercicio;
    @FXML private TableColumn<ItemPlanoTreino, String> colunaSecao;
    @FXML private TableColumn<ItemPlanoTreino, Number> colunaSeries;
    @FXML private TableColumn<ItemPlanoTreino, Number> colunaReps;
    @FXML private TableColumn<ItemPlanoTreino, Number> colunaCarga;

    public void setUsuario(Usuario usuario) { 
        this.usuarioLogado = usuario; 
        carregarPlanosDoUsuario();
    }
    
    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML private void handleCadastrarPlano() { irParaTela("/fxml/CadastrarPlanoTreino.fxml", "Cadastrar Plano de Treino", btnCadastrar);}
    @FXML private void handleModificarPlano() {irParaTela("/fxml/ModificarPlanoTreino.fxml", "Modificar Plano de Treino", btnModificar);}

    Logger logger = Logger.getLogger(PlanoTreinoAlunoController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa a camada de negócio
        this.planoTreinoBusiness = new PlanoTreinoBusiness(UsuarioCsvRepository.getInstance(), new PlanoTreinoCsvRepository());

        // Configura como cada coluna da tabela vai obter seu valor
        configurarTabela();

        // Adiciona um listener para o ComboBox
        comboPlanos.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                carregarExerciciosDoPlano(newValue);
            }
        });
    }


    private void carregarPlanosDoUsuario() {
        if (usuarioLogado != null) {
            this.planosDoUsuario = planoTreinoBusiness.listarPlanosPorUsuario(usuarioLogado);
            ObservableList<String> nomesDosPlanos = FXCollections.observableArrayList();
            for (PlanoTreino plano : planosDoUsuario) {
                nomesDosPlanos.add(plano.getNomePlano());
            }
            comboPlanos.setItems(nomesDosPlanos);
        }
    }

    private void carregarExerciciosDoPlano(String nomeDoPlano) {
        // Encontra o objeto PlanoTreino completo a partir do nome selecionado
        PlanoTreino planoSelecionado = null;
        for (PlanoTreino plano : planosDoUsuario) {
            if (plano.getNomePlano().equals(nomeDoPlano)) {
                planoSelecionado = plano;
                break;
            }
        }

        if (planoSelecionado != null) {
            // planoSelecionado.getItens() já retorna a lista chata de todos os itens de todas as seções
            tabelaExercicios.setItems(FXCollections.observableArrayList(planoSelecionado.getItens()));
        }
    }

    private void configurarTabela() {
        colunaExercicio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExercicio().getNome()));
        colunaSeries.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSeries()));
        colunaReps.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRepeticoes()));
        colunaCarga.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCarga()));

        // Para a coluna Seção, precisamos de uma lógica mais elaborada
        colunaSecao.setCellValueFactory(cellData -> {
            ItemPlanoTreino itemAtual = cellData.getValue();
            // Procura em qual seção este item pertence dentro do plano selecionado
            for (PlanoTreino plano : planosDoUsuario) {
                for (SecaoTreino secao : plano.getSecoes()) {
                    if (secao.getItensPlano().contains(itemAtual)) {
                        return new SimpleStringProperty(secao.getNomeTreino());
                    }
                }
            }
            return new SimpleStringProperty("N/A"); // Caso não encontre
        });
    }


    public void irParaTela(String caminhoFxml, String titulo, Button origem) {
        try {
            Stage stageAtual = (Stage) origem.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Object controller = loader.getController();

            invocarMetodoSeExiste(controller, "setStageAnterior", Stage.class, stageAtual);
            invocarMetodoUsuario(controller);

            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            stageAtual.close();
            novaStage.show();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar a tela", e);
        }
    }

    private void invocarMetodoSeExiste(Object objeto, String metodoNome, Class<?> parametroClass, Object parametro) {
        try {
            var metodo = objeto.getClass().getMethod(metodoNome, parametroClass);
            metodo.invoke(objeto, parametro);
        } catch (NoSuchMethodException ignored) {
            logger.log(Level.WARNING, "Método não encontrado", ignored);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Erro ao chamar o método", e);
        }
    }

    private void invocarMetodoUsuario(Object controller) {
        try {
            controller.getClass().getMethod("setUsuario", Usuario.class).invoke(controller, this.usuarioLogado);
        } catch (NoSuchMethodException e1) {
            try {
                controller.getClass().getMethod("setUsuarioLogado", Usuario.class).invoke(controller, this.usuarioLogado);
            } catch (NoSuchMethodException ignored) {
                logger.log(Level.WARNING, "Método não encontrado", ignored);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.log(Level.WARNING, "Erro ao chamar o método setUsuarioLogado", e);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Erro ao chamar o método SetUsuario", e);
        }
    }
}