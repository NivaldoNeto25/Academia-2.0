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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PlanoTreinoAlunoController implements Initializable{
    private Usuario usuarioLogado;
    private List<PlanoTreino> planosDoUsuario;
    private PlanoTreinoBusiness planoTreinoBusiness;

    @FXML private Button btnCadastrar;
    @FXML private Button btnModificar;
    @FXML private ComboBox<PlanoTreino> comboPlanos;
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

    @FXML private void handleCadastrarPlano() { 
        abrirJanelaModal("/fxml/CadastrarPlanoTreino.fxml", "Cadastrar Plano de Treino", null);
    }
    @FXML private void handleModificarPlano() {
        PlanoTreino planoSelecionado = comboPlanos.getValue();
        if (planoSelecionado == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nenhum Plano Selecionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecione um plano de treino para modificar.");
            alert.showAndWait();
            return;

        }
        abrirJanelaModal("/fxml/ModificarPlanoTreino.fxml", "Modificar Plano de Treino", planoSelecionado);
    }

    Logger logger = Logger.getLogger(PlanoTreinoAlunoController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa a camada de negócio
        this.planoTreinoBusiness = new PlanoTreinoBusiness(UsuarioCsvRepository.getInstance(), new PlanoTreinoCsvRepository());

        // Configura como cada coluna da tabela vai obter seu valor
        configurarTabela();
        configurarComboBox();

        comboPlanos.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                carregarExerciciosDoPlano(newValue);
            } else {
                tabelaExercicios.getItems().clear();
            }
        });
        // Adiciona um listener para o ComboBox
        comboPlanos.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                carregarExerciciosDoPlano(newValue);
            }
        });
    }

    private void configurarComboBox() {
    
        StringConverter<PlanoTreino> converter = new StringConverter<PlanoTreino>() {
        
        // Como exibir o objeto PlanoTreino como um String
            @Override
            public String toString(PlanoTreino plano) {
                if (plano == null) {
                    return null;
                } else {
                    return plano.getNomePlano();
                }
            }

        // Como encontrar o objeto PlanoTreino a partir de um String
        // (Isso não é usado por nós, mas é boa prática implementar)
            @Override
            public PlanoTreino fromString(String string) {
            // Não precisamos disso, pois o usuário não pode digitar um novo plano
                return null; 
            }
        };

   
        comboPlanos.setConverter(converter);
    
        comboPlanos.setCellFactory(param -> new ListCell<PlanoTreino>() {
            @Override
            protected void updateItem(PlanoTreino item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNomePlano());
                }
            }
        });
    }


    private void carregarPlanosDoUsuario() {
        if (usuarioLogado != null) {
            this.planosDoUsuario = planoTreinoBusiness.listarPlanosPorUsuario(usuarioLogado);
            comboPlanos.setItems(FXCollections.observableArrayList(planosDoUsuario));
        }
    }

    private void carregarExerciciosDoPlano(PlanoTreino planoSelecionado) {
        // Encontra o objeto PlanoTreino completo a partir do nome selecionado
        if(planoSelecionado != null){
            tabelaExercicios.setItems(FXCollections.observableArrayList(planoSelecionado.getItens()));
        }
    }

    private void configurarTabela() {
        colunaExercicio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExercicio().getNome()));
        colunaSeries.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSeries()));
        colunaReps.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRepeticoes()));
        colunaCarga.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCarga()));

        colunaSecao.setCellValueFactory(cellData -> {
            ItemPlanoTreino itemAtual = cellData.getValue();
            PlanoTreino planoSelecionado = comboPlanos.getValue(); // Pega o plano selecionado
            
            if (planoSelecionado != null) {
                for (SecaoTreino secao : planoSelecionado.getSecoes()) {
                    if (secao.getItensPlano().contains(itemAtual)) {
                        return new SimpleStringProperty(secao.getNomeTreino());
                    }
                }
            }
            return new SimpleStringProperty("N/A");
        });
    }


    private void abrirJanelaModal(String caminhoFxml, String titulo, PlanoTreino plano) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Object controller = loader.getController();


            // Passa o plano selecionado (se existir)
            if (plano != null) {
                invocarMetodoSeExiste(controller, "setPlanoParaModificar", PlanoTreino.class, plano);
            }

            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            novaStage.initModality(Modality.APPLICATION_MODAL); // Trava a janela principal
            novaStage.showAndWait(); // Espera a janela fechar

            // ATUALIZA a lista de planos após fechar a janela
            carregarPlanosDoUsuario();

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar a tela", e);
        }
    }

    private void invocarMetodoSeExiste(Object objeto, String metodoNome, Class<?> parametroClass, Object parametro) {
        try {
            var metodo = objeto.getClass().getMethod(metodoNome, parametroClass);
            metodo.invoke(objeto, parametro);
        } catch (NoSuchMethodException ignored) {
            // Ignora silenciosamente se o método não existir
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Erro ao chamar o método " + metodoNome, e);
        }
    }
}