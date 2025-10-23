package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.*;
import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import javafx.collections.FXCollections; // ADICIONADO
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdicionarExercicioSecaoController {

    @FXML private TextField secaoField;
    @FXML private ListView<Exercicio> exerciciosListView; 
    @FXML private TextField seriesField;
    @FXML private TextField repeticoesField;
    @FXML private TextField cargaField;
    @FXML private Button btnAdicionar;
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;

    private PlanoTreinoBusiness planoTreinoBusiness;
    private ExercicioBusiness exercicioBusiness;

    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
        exercicioBusiness = new ExercicioBusiness();

        // Popula a lista de exercícios
        carregarListaExercicios();

        btnAdicionar.setOnAction(e -> adicionarExercicio());
        btnVoltar.setOnAction(e -> voltar());
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }



    // ADICIONADO - Método para popular a ListView
    private void carregarListaExercicios() {
        // Define como cada item da lista deve ser exibido
        exerciciosListView.setCellFactory(param -> new ListCell<Exercicio>() {
            @Override
            protected void updateItem(Exercicio item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getNome() == null) {
                    setText(null);
                } else {
                    // Mesma lógica do ExercicioMenuController
                    setText(item.getNome() + " | " + item.getDescricao());
                }
            }
        });
        
        // Busca os exercícios e os adiciona à lista
        var exercicios = exercicioBusiness.listarExercicios();
        exerciciosListView.setItems(FXCollections.observableArrayList(exercicios));
    }


    public void adicionarExercicio() {
        String nomeSecao = secaoField.getText().trim();
        Exercicio exercicio = exerciciosListView.getSelectionModel().getSelectedItem(); // ADICIONADO
        
        String seriesStr = seriesField.getText().trim();
        String repeticoesStr = repeticoesField.getText().trim();
        String cargaStr = cargaField.getText().trim();

        if (nomeSecao.isEmpty() || seriesStr.isEmpty() || repeticoesStr.isEmpty() || cargaStr.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos (seção, séries, repetições e carga).", Alert.AlertType.WARNING);
            return;
        }

        // ADICIONADO - Verifica se um exercício foi selecionado
        if (exercicio == null) {
            mostrarAlerta("Erro", "Selecione um exercício da lista.", Alert.AlertType.ERROR);
            return;
        }

        try {
            int series = Integer.parseInt(seriesStr);
            int repeticoes = Integer.parseInt(repeticoesStr);
            int carga = Integer.parseInt(cargaStr);


            PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
            if (plano == null) {
                mostrarAlerta("Erro", "Você não possui um plano de treino cadastrado.", Alert.AlertType.ERROR);
                return;
            }

            SecaoTreino secao = plano.getOuCriarSecao(nomeSecao);
            ItemPlanoTreino item = new ItemPlanoTreino(exercicio, series, repeticoes, carga);
            secao.addItemSecao(item);

            planoTreinoBusiness.modificarPlanoDeTreino(plano);

            mostrarAlerta("Sucesso", "Exercício adicionado à seção com sucesso!", Alert.AlertType.INFORMATION);

            limparCampos();

        } catch (NumberFormatException ex) {
            mostrarAlerta("Erro", "Digite valores numéricos válidos para séries, repetições e carga.", Alert.AlertType.ERROR);
        }
    }

    public void limparCampos() {
        secaoField.clear();
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