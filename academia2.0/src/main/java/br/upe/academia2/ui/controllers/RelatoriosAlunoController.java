package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RelatoriosAlunoController {

    private Usuario usuario;

    @FXML
    private TextArea txtSaida;

    private static final String FORMATO_DATA = "yyyy-MM-dd HH:mm:ss";
    private final IndicadorBiomedicoBusiness indicadorBusiness = new IndicadorBiomedicoBusiness();

    Logger logger = Logger.getLogger(RelatoriosAlunoController.class.getName());

    @FXML
    private Button btnVoltar;

    private Stage stageAnterior;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    public void handleRelatorioGeral(ActionEvent event) {
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

        for (IndicadorBiomedico ind : lista) {
            txtSaida.appendText(formatarIndicador(ind));
        }

        txtSaida.appendText("\n Relatório Geral gerado com sucesso!\n");
    }

    @FXML
    public void handleRelatorioComparativo(ActionEvent event) {
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

        lista.sort(Comparator.comparing(IndicadorBiomedico::getDataRegistro).reversed());
        IndicadorBiomedico atual = lista.get(0);
        IndicadorBiomedico anterior = lista.get(1);

        txtSaida.appendText(" Comparando os dois registros mais recentes:\n\n");
        txtSaida.appendText(" Registro mais recente:\n" + formatarIndicador(atual) + "\n");
        txtSaida.appendText(" Registro anterior:\n" + formatarIndicador(anterior));
    }

    @FXML
    public void handleVoltar() {
        if (stageAnterior != null) {
            Stage atual = (Stage) btnVoltar.getScene().getWindow();
            atual.close();
            stageAnterior.show();
        } else {
            logger.log(Level.WARNING, "Stage anterior não definido. Não é possível voltar.");
        }
    }

    public String formatarIndicador(IndicadorBiomedico ind) {
        return String.format("""
                ------------------------------
                Data: %s
                Peso: %.2f kg
                Altura: %.2f m
                IMC: %.2f"
                Gordura: %.2f%%
                Massa Magra: %.2f%%
                ------------------------------
                """,
                new SimpleDateFormat(FORMATO_DATA).format(ind.getDataRegistro()),
                ind.getPeso(),
                ind.getAltura(),
                ind.getImc(),
                ind.getPercentualGordura(),
                ind.getPercentualMassaMagra()
        );
    }
}