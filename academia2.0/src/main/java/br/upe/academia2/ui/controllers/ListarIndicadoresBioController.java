package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

public class ListarIndicadoresBioController {

    private static final Logger logger = Logger.getLogger(ListarIndicadoresBioController.class.getName());

    @FXML private ListView<String> listaIndicadores;
    @FXML private Button fechar;

    private final IndicadorBiomedicoBusiness indicadorBusiness = new IndicadorBiomedicoBusiness();
    private Stage stageAnterior;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        // Variável local para usuário dentro do método
        List<IndicadorBiomedico> indicadores = indicadorBusiness.listarIndicadores(usuarioLogado);
        logger.info(() -> "Total de indicadores encontrados: " + indicadores.size());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        var listaFormatada = indicadores.stream().map(i ->
                String.format("Peso: %.2f | Altura: %.2f | IMC: %.2f | Gordura: %.2f%% | Massa Magra: %.2f%% | Data: %s",
                        i.getPeso(), i.getAltura(), i.getImc(), i.getPercentualGordura(), i.getPercentualMassaMagra(), sdf.format(i.getDataRegistro()))
        ).toList();

        listaIndicadores.setItems(FXCollections.observableArrayList(listaFormatada));
    }

    @FXML
    public void handleFechar() {
        Stage atual = (Stage) fechar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}