package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.*;
import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.repository.PlanoTreinoJpaRepository;
import br.upe.academia2.data.repository.UsuarioJpaRepository;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdicionarExercicioSecaoController {

    @FXML private ComboBox<String> secaoComboBox; // MUDANÇA
    @FXML private ListView<Exercicio> exerciciosListView; 
    @FXML private TextField seriesField;
    @FXML private TextField repeticoesField;
    @FXML private TextField cargaField;
    @FXML private Button btnAdicionar;
    @FXML private Button btnVoltar;

    private PlanoTreino planoParaModificar; // ADICIONADO
    
    private PlanoTreinoBusiness planoTreinoBusiness;
    private ExercicioBusiness exercicioBusiness;

    @FXML
    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioJpaRepository.getInstance(),
                new PlanoTreinoJpaRepository()
        );
        exercicioBusiness = new ExercicioBusiness();

        carregarListaExercicios();

        btnAdicionar.setOnAction(e -> adicionarExercicio());
        btnVoltar.setOnAction(e -> voltar());
    }

    /**
     * NOVO MÉTODO: Recebe o plano específico.
     */
    public void setPlanoParaModificar(PlanoTreino plano) {
        this.planoParaModificar = plano;
        carregarSecoesDoPlano();
    }

    // O método setUsuarioLogado(Usuario usuario) foi REMOVIDO

    private void carregarListaExercicios() {
        exerciciosListView.setCellFactory(param -> new ListCell<Exercicio>() {
            @Override
            protected void updateItem(Exercicio item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNome() == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " | " + item.getDescricao());
                }
            }
        });
        
        var exercicios = exercicioBusiness.listarExercicios();
        exerciciosListView.setItems(FXCollections.observableArrayList(exercicios));
    }

    /**
     * NOVO MÉTODO: Popula o ComboBox de seções.
     */
    private void carregarSecoesDoPlano() {
        secaoComboBox.getItems().clear();
        if (planoParaModificar != null) {
            for (SecaoTreino secao : planoParaModificar.getSecoes()) {
                secaoComboBox.getItems().add(secao.getNomeTreino());
            }
        }
    }

    public void adicionarExercicio() {
        // MUDANÇA: Pega o valor do ComboBox (que pode ser digitado, pois é 'editable')
        String nomeSecao = secaoComboBox.getEditor().getText().trim();
        Exercicio exercicio = exerciciosListView.getSelectionModel().getSelectedItem();
        
        String seriesStr = seriesField.getText().trim();
        String repeticoesStr = repeticoesField.getText().trim();
        String cargaStr = cargaField.getText().trim();

        if (nomeSecao.isEmpty() || seriesStr.isEmpty() || repeticoesStr.isEmpty() || cargaStr.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos (seção, séries, repetições e carga).", Alert.AlertType.WARNING);
            return;
        }

        if (exercicio == null) {
            mostrarAlerta("Erro", "Selecione um exercício da lista.", Alert.AlertType.ERROR);
            return;
        }
        
        // MUDANÇA: Usa o 'planoParaModificar'
        if (planoParaModificar == null) {
            mostrarAlerta("Erro", "Nenhum plano de treino foi selecionado.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int series = Integer.parseInt(seriesStr);
            int repeticoes = Integer.parseInt(repeticoesStr);
            int carga = Integer.parseInt(cargaStr);

            // MUDANÇA: Não busca mais o plano, usa o objeto recebido
            SecaoTreino secao = planoParaModificar.getOuCriarSecao(nomeSecao);
            ItemPlanoTreino item = new ItemPlanoTreino(exercicio, series, repeticoes, carga);
            secao.addItemSecao(item);

            planoTreinoBusiness.modificarPlanoDeTreino(planoParaModificar);

            mostrarAlerta("Sucesso", "Exercício adicionado à seção com sucesso!", Alert.AlertType.INFORMATION);
            
            carregarSecoesDoPlano(); // Atualiza a lista de seções caso uma nova tenha sido criada
            limparCampos();

        } catch (NumberFormatException ex) {
            mostrarAlerta("Erro", "Digite valores numéricos válidos para séries, repetições e carga.", Alert.AlertType.ERROR);
        }
    }

    public void limparCampos() {
        secaoComboBox.getEditor().clear(); // Limpa o editor do ComboBox
        secaoComboBox.setValue(null);
        exerciciosListView.getSelectionModel().clearSelection(); 
        seriesField.clear();
        repeticoesField.clear();
        cargaField.clear();
    }

    public void voltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
    }

    public void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}