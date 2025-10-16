package br.upe.academia2.ui.controllers;

import br.upe.academia2.data.beans.*;
import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.business.SecaoTreinoBusiness;
import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdicionarExercicioSecaoController {

    @FXML private TextField secaoField;
    @FXML private TextField exercicioField;
    @FXML private TextField seriesField;
    @FXML private TextField repeticoesField;
    @FXML private TextField cargaField;
    @FXML private Button btnAdicionar;
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;
    private Stage stageAnterior;

    private PlanoTreinoBusiness planoTreinoBusiness;
    private ExercicioBusiness exercicioBusiness;
    private SecaoTreinoBusiness secaoTreinoBusiness;

    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
        exercicioBusiness = new ExercicioBusiness();
        secaoTreinoBusiness = new SecaoTreinoBusiness();

        btnAdicionar.setOnAction(e -> adicionarExercicio());
        btnVoltar.setOnAction(e -> voltar());
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    private void adicionarExercicio() {
        String nomeSecao = secaoField.getText().trim();
        String nomeExercicio = exercicioField.getText().trim();
        String seriesStr = seriesField.getText().trim();
        String repeticoesStr = repeticoesField.getText().trim();
        String cargaStr = cargaField.getText().trim();

        if (nomeSecao.isEmpty() || nomeExercicio.isEmpty() || seriesStr.isEmpty() || repeticoesStr.isEmpty() || cargaStr.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int series = Integer.parseInt(seriesStr);
            int repeticoes = Integer.parseInt(repeticoesStr);
            int carga = Integer.parseInt(cargaStr);

            Exercicio exercicio = exercicioBusiness.buscarExercicioPorNome(nomeExercicio);
            if (exercicio == null) {
                mostrarAlerta("Erro", "Exercício não encontrado. Cadastre-o primeiro.", Alert.AlertType.ERROR);
                return;
            }

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

    private void limparCampos() {
        secaoField.clear();
        exercicioField.clear();
        seriesField.clear();
        repeticoesField.clear();
        cargaField.clear();
    }

    private void voltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) {
            stageAnterior.show();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}