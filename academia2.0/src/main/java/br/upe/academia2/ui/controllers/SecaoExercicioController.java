package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.SecaoTreino;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecaoExercicioController implements Initializable {

    @FXML private ComboBox<SecaoTreino> comboSecao;
    @FXML private ListView<ItemPlanoTreino> listaExercicios; 
    @FXML private Button btnEscolherExercicio;
    @FXML private Button btnVoltar; 

    private PlanoTreino planoSelecionado;

    private static final Logger logger = Logger.getLogger(SecaoExercicioController.class.getName());

    /**
     * Este método é chamado pelo SecaoTreinoController (a tela anterior)
     * para injetar o plano de treino que o usuário selecionou.
     */
    public void setPlanoParaModificar(PlanoTreino plano) {
        this.planoSelecionado = plano;
        // Agora que temos o plano, podemos carregar suas seções
        carregarSecoesDoPlano();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarComboBoxSecao();
        configurarListViewExercicios();

        // Adiciona um listener para o ComboBox de Seção
        // Quando o usuário escolher uma seção, atualiza a lista de exercícios
        comboSecao.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // 'newVal' é um objeto SecaoTreino
                carregarExerciciosDaSecao(newVal);
            } else {
                listaExercicios.getItems().clear();
            }
        });

        // Configura as ações dos botões
        btnVoltar.setOnAction(e -> handleVoltar()); 
        btnEscolherExercicio.setOnAction(e -> handleEscolherExercicio()); 
    }

    /**
     * Popula o ComboBox de seções com base no plano que foi injetado.
     */
    private void carregarSecoesDoPlano() {
        if (planoSelecionado != null) {
            comboSecao.setItems(FXCollections.observableArrayList(planoSelecionado.getSecoes()));
        }
    }

    /**
     * Popula a ListView de exercícios com base na seção que foi selecionada.
     */
    private void carregarExerciciosDaSecao(SecaoTreino secao) {
        if (secao != null) {
            listaExercicios.setItems(FXCollections.observableArrayList(secao.getItensPlano()));
        }
    }

    /**
     * Configura o ComboBox para exibir o nome da SeçãoTreino.
     */
    private void configurarComboBoxSecao() {
        StringConverter<SecaoTreino> converter = new StringConverter<>() {
            @Override
            public String toString(SecaoTreino secao) {
                return (secao == null) ? null : secao.getNomeTreino();
            }
            @Override
            public SecaoTreino fromString(String string) {
                return null; // Não é necessário
            }
        };

        comboSecao.setConverter(converter);
        comboSecao.setCellFactory(param -> new ListCell<SecaoTreino>() {
            @Override
            protected void updateItem(SecaoTreino item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomeTreino());
            }
        });
    }

    /**
     * Configura a ListView para exibir os detalhes do ItemPlanoTreino (exercício).
     */
    private void configurarListViewExercicios() {
        listaExercicios.setCellFactory(param -> new ListCell<ItemPlanoTreino>() {
            @Override
            protected void updateItem(ItemPlanoTreino item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Formata a string de exibição do exercício
                    String texto = String.format("%s (%d séries x %d reps, Carga: %dkg)",
                            item.getExercicio().getNome(),
                            item.getSeries(),
                            item.getRepeticoes(),
                            item.getCarga());
                    setText(texto);
                }
            }
        });
    }

    @FXML
    private void handleVoltar() {
        Stage stage = (Stage) btnVoltar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEscolherExercicio() {
        ItemPlanoTreino exercicioSelecionado = listaExercicios.getSelectionModel().getSelectedItem();

        if (exercicioSelecionado == null) {
            mostrarAlerta("Erro", "Nenhum exercício foi selecionado da lista.");
            return;
        }

        abrirJanelaModal("/fxml/RegistrarPerformance.fxml", "Registrar Performance", exercicioSelecionado);

    }
    
    private void mostrarAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    private void abrirJanelaModal(String caminhoFxml, String titulo, ItemPlanoTreino item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFxml));
            Parent root = loader.load();
            Object controller = loader.getController();

            // Passa o item de treino selecionado para o novo controller
            invocarMetodoSeExiste(controller, "setItemParaRegistrar", ItemPlanoTreino.class, item);

            invocarMetodoSeExiste(controller, "setPlanoPai", PlanoTreino.class, this.planoSelecionado);

            Stage novaStage = new Stage();
            novaStage.setScene(new Scene(root));
            novaStage.setTitle(titulo);
            novaStage.initModality(Modality.APPLICATION_MODAL); // Trava a janela anterior
            novaStage.showAndWait(); // Espera a janela fechar

            // ATUALIZA a lista de exercícios na tela atual
            // Isso mostrará as novas cargas/reps se o usuário as atualizou
            listaExercicios.refresh(); 

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao carregar a tela: {0}", caminhoFxml);
        }
    }

    private void invocarMetodoSeExiste(Object objeto, String metodoNome, Class<?> parametroClass, Object parametro) {
        try {
            var metodo = objeto.getClass().getMethod(metodoNome, parametroClass);
            metodo.invoke(objeto, parametro);
        } catch (NoSuchMethodException ignored) {
            logger.log(Level.WARNING, "Método {0} não encontrado no controller.", metodoNome);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.WARNING, "Erro ao chamar o método {0}", metodoNome);
        }
    }
}