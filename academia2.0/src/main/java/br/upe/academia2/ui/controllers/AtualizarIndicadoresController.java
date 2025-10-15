package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AtualizarIndicadoresController {

    @FXML private ListView<String> listaIndicadores;
    @FXML private TextField tfPeso;
    @FXML private TextField tfAltura;
    @FXML private TextField tfGordura;
    @FXML private TextField tfMassaMagra;
    @FXML private TextField tfIMC;

    @FXML private Button btnSalvar;
    @FXML private Button btnFechar;

    private Usuario usuarioLogado;
    private Stage stageAnterior;

    private IndicadorBiomedicoBusiness indicadorBusiness = new IndicadorBiomedicoBusiness();

    private List<IndicadorBiomedico> indicadores;
    private IndicadorBiomedico indicadorSelecionado;

    @FXML
    public void initialize() {
        listaIndicadores.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int idx = newVal.intValue();
            if (idx >= 0 && idx < indicadores.size()) {
                indicadorSelecionado = indicadores.get(idx);
                carregarIndicadorNaTela(indicadorSelecionado);
            }
        });
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarIndicadores();
    }

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    private void carregarIndicadores() {
        indicadores = indicadorBusiness.listarIndicadores(usuarioLogado);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<String> items = indicadores.stream()
                .map(i -> String.format("Peso: %.2f, Alt: %.2f, IMC: %.2f, Gordura: %.2f%%, Massa Magra: %.2f%%, Data: %s",
                        i.getPeso(), i.getAltura(), i.getImc(), i.getPercentualGordura(),
                        i.getPercentualMassaMagra(), sdf.format(i.getDataRegistro())))
                .toList();
        listaIndicadores.setItems(FXCollections.observableArrayList(items));
        if (!indicadores.isEmpty()) {
            listaIndicadores.getSelectionModel().select(0);
        }
    }

    private void carregarIndicadorNaTela(IndicadorBiomedico ind) {
        tfPeso.setText(String.format(Locale.US, "%.2f", ind.getPeso()));
        tfAltura.setText(String.format(Locale.US, "%.2f", ind.getAltura()));
        tfGordura.setText(String.format(Locale.US, "%.2f", ind.getPercentualGordura()));
        tfMassaMagra.setText(String.format(Locale.US, "%.2f", ind.getPercentualMassaMagra()));
        tfIMC.setText(String.format(Locale.US, "%.2f", ind.getImc()));
    }

    @FXML
    private void handleSalvar() {
        if (indicadorSelecionado == null) return;
        try {
            double peso = Double.parseDouble(tfPeso.getText());
            double altura = Double.parseDouble(tfAltura.getText());
            double gordura = Double.parseDouble(tfGordura.getText());
            double massa = Double.parseDouble(tfMassaMagra.getText());
            double imc = Double.parseDouble(tfIMC.getText());

            // Atualiza o indicador existente com novos valores e data atual
            indicadorSelecionado.setPeso(peso);
            indicadorSelecionado.setAltura(altura);
            indicadorSelecionado.setPercentualGordura(gordura);
            indicadorSelecionado.setPercentualMassaMagra(massa);
            indicadorSelecionado.setImc(imc);
            indicadorSelecionado.setDataRegistro(new Date());

            boolean sucesso = indicadorBusiness.atualizarIndicador(indicadorSelecionado);
            if (sucesso) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Indicador atualizado com sucesso!");
                alert.showAndWait();
                carregarIndicadores();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Falha ao atualizar indicador.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Informe valores válidos nos campos.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleFechar() {
        Stage atual = (Stage) btnFechar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}