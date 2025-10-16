package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class ListarExerciciosController {
    private static final Logger logger = Logger.getLogger(ListarExerciciosController.class.getName());

    @FXML private ListView<String> listaExercicios;
    @FXML private Button fechar;

    private Stage stageAnterior;
    private final ExercicioBusiness exercicio = new ExercicioBusiness();

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    public void initialize() {
        logger.info(() -> "Exercícios listados: " + exercicio.listarExercicios());
        var exercicios = exercicio.listarExercicios();
        var nomes = exercicios.stream()
                .map(a -> a.getNome() + " | " + a.getDescricao())
                .toList();
        listaExercicios.setItems(FXCollections.observableArrayList(nomes));
    }

    @FXML
    public void handleFechar() {
        Stage atual = (Stage) fechar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}