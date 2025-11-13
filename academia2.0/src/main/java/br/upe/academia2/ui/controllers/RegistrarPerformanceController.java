package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.SecaoTreinoBusiness;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class RegistrarPerformanceController {

    @FXML private Label exercicioLabel;
    @FXML private Label seriesPlanoLabel;
    @FXML private Label repsPlanoLabel;
    @FXML private Label cargaPlanoLabel;
    @FXML private TextField seriesField;
    @FXML private TextField repsField;
    @FXML private TextField cargaField;
    @FXML private Button btnSalvar;
    @FXML private Button btnVoltar;

    private ItemPlanoTreino itemOriginal;
    private SecaoTreinoBusiness secaoTreinoBusiness;
    private PlanoTreino planoPai;

    @FXML
    public void initialize() {
        this.secaoTreinoBusiness = new SecaoTreinoBusiness();
    }

    /**
     * Método de injeção chamado pelo SecaoExercicioController.
     * Recebe o item de treino e preenche os campos da tela.
     */
    public void setItemParaRegistrar(ItemPlanoTreino item) {
        this.itemOriginal = item;
        popularDadosOriginais();
    }

    public void setPlanoPai(PlanoTreino plano) {
        this.planoPai = plano;
    }

    private void popularDadosOriginais() {
        if (itemOriginal != null) {
            exercicioLabel.setText("Exercício: " + itemOriginal.getExercicio().getNome());
            
            // Preenche os dados do plano
            seriesPlanoLabel.setText(String.valueOf(itemOriginal.getSeries()));
            repsPlanoLabel.setText(String.valueOf(itemOriginal.getRepeticoes()));
            cargaPlanoLabel.setText(String.valueOf(itemOriginal.getCarga()));

            // Preenche os campos "Realizado" com os valores do plano como padrão
            seriesField.setText(String.valueOf(itemOriginal.getSeries()));
            repsField.setText(String.valueOf(itemOriginal.getRepeticoes()));
            cargaField.setText(String.valueOf(itemOriginal.getCarga()));
        }
    }

    @FXML
    private void handleSalvar() {
        try {
            // 1. Obter os novos valores
            int seriesRealizadas = Integer.parseInt(seriesField.getText());
            int repsRealizadas = Integer.parseInt(repsField.getText());
            int cargaRealizada = Integer.parseInt(cargaField.getText());

            // 2. Verificar se houve diferença
            boolean isDiferente = (seriesRealizadas != itemOriginal.getSeries()) ||
                                  (repsRealizadas != itemOriginal.getRepeticoes()) ||
                                  (cargaRealizada != itemOriginal.getCarga());

            if (isDiferente) {
                // 3. Se for diferente, pedir confirmação
                Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacao.setTitle("Atualizar Plano?");
                confirmacao.setHeaderText("A performance foi diferente do planejado.");
                confirmacao.setContentText("Deseja atualizar o seu plano de treino com estes novos valores?");

                Optional<ButtonType> resultado = confirmacao.showAndWait();

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    // 4. Se o usuário confirmar, chamar o business
                    secaoTreinoBusiness.registrarPerformance(planoPai, itemOriginal, cargaRealizada, repsRealizadas, seriesRealizadas);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Plano de treino atualizado com a nova performance!");
                } else {
                    // Usuário cancelou, não faz nada
                }
            } else {
                // 5. Se for igual, apenas informar o registro
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Performance registrada com sucesso!");
            }

            // 6. Fechar a janela
            handleVoltar();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "Por favor, insira apenas números válidos para séries, repetições e carga.");
        }
    }

    @FXML
    private void handleVoltar() {
        Stage stage = (Stage) btnVoltar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}