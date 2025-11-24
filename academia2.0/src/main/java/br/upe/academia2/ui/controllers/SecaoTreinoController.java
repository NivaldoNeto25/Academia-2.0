package br.upe.academia2.ui.controllers;

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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecaoTreinoController implements Initializable {

    // --- Campos FXML ---
    // Note que os @FXML correspondem ao SecaoTreino.fxml
    @FXML private Label tituloPagina;
    @FXML private ComboBox<PlanoTreino> comboPlanos;
    @FXML private Button btnRealizarSecao;
    @FXML private TableView<ItemPlanoTreino> tabelaExercicios;
    @FXML private TableColumn<ItemPlanoTreino, String> colunaExercicio;
    @FXML private TableColumn<ItemPlanoTreino, String> colunaSecao;
    @FXML private TableColumn<ItemPlanoTreino, Number> colunaSeries;
    @FXML private TableColumn<ItemPlanoTreino, Number> colunaReps;
    @FXML private TableColumn<ItemPlanoTreino, Number> colunaCarga;

    Logger logger = Logger.getLogger(SecaoTreinoController.class.getName());

    // --- Atributos de Lógica (Copiados do PlanoTreinoAlunoController) ---
    private Usuario usuarioLogado;
    private PlanoTreinoBusiness planoTreinoBusiness;

    /**
     * Este é o método de injeção que será chamado pelo AlunoMenuController.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarPlanosDoUsuario();
    }

    /**
     * Método executado quando o FXML é carregado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );

        configurarTabela();
        configurarComboBox();

        // Listener para atualizar a tabela quando um plano é selecionado
        comboPlanos.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                carregarExerciciosDoPlano(newValue);
            } else {
                tabelaExercicios.getItems().clear();
            }
        });
    }

    /**
     * Novo método de handler para o botão "Realizar Seções".
     */
    @FXML
    private void handleRealizarSecaoTreino() {
        PlanoTreino planoSelecionado = comboPlanos.getValue();
        if (planoSelecionado == null) {
            mostrarAlerta("Nenhum Plano Selecionado", "Por favor, selecione um plano para continuar.");
            return;
        }

        abrirJanelaModal("/fxml/RealizarSecaoTreino.fxml", "Realizar Seção de Treino", planoSelecionado);
    }


    private void configurarComboBox() {
        StringConverter<PlanoTreino> converter = new StringConverter<>() {
            @Override
            public String toString(PlanoTreino plano) {
                return (plano == null) ? null : plano.getNomePlano();
            }
            @Override
            public PlanoTreino fromString(String string) {
                return null; // Não é necessário
            }
        };

        comboPlanos.setConverter(converter);
        comboPlanos.setCellFactory(param -> new ListCell<PlanoTreino>() {
            @Override
            protected void updateItem(PlanoTreino item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomePlano());
            }
        });
    }

    private void carregarPlanosDoUsuario() {
        if (usuarioLogado != null) {
            List<PlanoTreino> planosDoUsuario = planoTreinoBusiness.listarPlanosPorUsuario(usuarioLogado);
            comboPlanos.setItems(FXCollections.observableArrayList(planosDoUsuario));
        }
    }

    private void carregarExerciciosDoPlano(PlanoTreino planoSelecionado) {
        if (planoSelecionado != null) {
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

    private void mostrarAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
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
            logger.log(Level.WARNING, "Erro ao chamar o método {0}", metodoNome);
        }
    }
}