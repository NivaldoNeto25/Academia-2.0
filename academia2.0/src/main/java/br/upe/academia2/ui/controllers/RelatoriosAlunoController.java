package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class RelatoriosAlunoController {

    private static final Logger logger = Logger.getLogger(RelatoriosAlunoController.class.getName());
    private Usuario usuario;

    @FXML
    private TextArea txtSaida;

    private static final String FORMATO_DATA = "yyyy-MM-dd HH:mm:ss";
    private IndicadorBiomedicoBusiness indicadorBusiness = new IndicadorBiomedicoBusiness();

    private Stage stageAnterior;

    @FXML
    private Button btnVoltar;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setStageAnterior(Stage stageAnterior) { this.stageAnterior = stageAnterior; }

    @FXML
    private void handleRelatorioGeral(ActionEvent event) {
        if (usuario == null) {
            txtSaida.setText(" Usuário não definido! Faça login novamente.");
            return;
        }

        txtSaida.clear();
        txtSaida.appendText(" Gerando Relatório Geral...\n");

        List<IndicadorBiomedico> lista = indicadorBusiness.listarIndicadores(usuario);
        if (lista.isEmpty()) {
            txtSaida.appendText("Nenhum indicador encontrado para este usuário.\n");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);
        for (IndicadorBiomedico ind : lista) {
            txtSaida.appendText(formatarIndicador(ind));
        }

        txtSaida.appendText("\n Relatório Geral gerado com sucesso!\n");
    }

    @FXML
    private void handleRelatorioComparativo(ActionEvent event) {
        if (usuario == null) {
            txtSaida.setText(" Usuário não definido! Faça login novamente.");
            return;
        }

        txtSaida.clear();
        txtSaida.appendText(" Gerando Relatório Comparativo...\n");

        List<IndicadorBiomedico> lista = indicadorBusiness.listarIndicadores(usuario);
        if (lista.isEmpty()) {
            txtSaida.appendText("Nenhum indicador encontrado.\n");
            return;
        }

        if (lista.size() < 2) {
            txtSaida.appendText("Apenas um registro encontrado. Exibindo:\n");
            txtSaida.appendText(formatarIndicador(lista.get(0)));
            return;
        }

        // Pega os dois registros mais recentes
        lista.sort(Comparator.comparing(IndicadorBiomedico::getDataRegistro).reversed());
        IndicadorBiomedico atual = lista.get(0);
        IndicadorBiomedico anterior = lista.get(1);

        txtSaida.appendText(" Comparando os dois registros mais recentes:\n\n");
        txtSaida.appendText(" Registro mais recente:\n" + formatarIndicador(atual) + "\n");
        txtSaida.appendText(" Registro anterior:\n" + formatarIndicador(anterior));
    }

    @FXML
    private void handleVoltar() throws Exception {
        URL fxml = Objects.requireNonNull(
                getClass().getResource("/fxml/AlunoMenu.fxml"),
                "FXML /fxml/AlunoMenu.fxml não encontrado"
        );
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        Stage nova = new Stage();
        nova.setScene(new Scene(root));
        atual.close();
        nova.show();
    }

    private String formatarIndicador(IndicadorBiomedico ind) {
        return String.format(
                "------------------------------\n" +
                        "Data: %s\n" +
                        "Peso: %.2f kg\n" +
                        "Altura: %.2f m\n" +
                        "IMC: %.2f\n" +
                        "Gordura: %.2f%%\n" +
                        "Massa Magra: %.2f%%\n" +
                        "------------------------------\n",
                new SimpleDateFormat(FORMATO_DATA).format(ind.getDataRegistro()),
                ind.getPeso(),
                ind.getAltura(),
                ind.getImc(),
                ind.getPercentualGordura(),
                ind.getPercentualMassaMagra()
        );
    }
}