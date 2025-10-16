package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Date;

public class AlterarDatasController {

    @FXML private DatePicker dataInicioPicker;
    @FXML private DatePicker dataFimPicker;
    @FXML private Button btnSalvar;
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;
    private PlanoTreinoBusiness planoTreinoBusiness;
    private Stage stageAnterior;

    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarDatasAtuais();
    }

    public void setStageAnterior(Stage stage) {
        this.stageAnterior = stage;
    }

    public void carregarDatasAtuais() {
        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
        if (plano != null) {
            if (plano.getInicioPlano() != null)
                dataInicioPicker.setValue(plano.getInicioPlano().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            if (plano.getFimPlano() != null)
                dataFimPicker.setValue(plano.getFimPlano().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    @FXML
    public void handleSalvar() {
        if (dataInicioPicker.getValue() == null || dataFimPicker.getValue() == null) {
            mostrarAlerta("Erro", "Selecione as duas datas para prosseguir.", Alert.AlertType.WARNING);
            return;
        }

        Date dataInicio = Date.from(dataInicioPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dataFim = Date.from(dataFimPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
        if (plano == null) {
            mostrarAlerta("Erro", "Plano de treino não encontrado.", Alert.AlertType.ERROR);
            return;
        }

        plano.setInicioPlano(dataInicio);
        plano.setFimPlano(dataFim);
        planoTreinoBusiness.modificarPlanoDeTreino(plano);

        mostrarAlerta("Sucesso", "Datas atualizadas com sucesso!", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) {
            stageAnterior.show();
        }
    }

    public void mostrarAlerta(String titulo, String conteudo, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}