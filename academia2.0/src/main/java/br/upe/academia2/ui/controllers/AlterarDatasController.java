package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
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

    // private Usuario usuarioLogado; // REMOVIDO
    private PlanoTreino planoParaModificar; // ADICIONADO
    private PlanoTreinoBusiness planoTreinoBusiness;

    @FXML
    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
    }

    /**
     * NOVO MÉTODO: Recebe o plano específico do ModificarPlanoTreinoController.
     */
    public void setPlanoParaModificar(PlanoTreino plano) {
        this.planoParaModificar = plano;
        carregarDatasAtuais();
    }

    public void carregarDatasAtuais() {
        
        if (planoParaModificar != null) {
            if (planoParaModificar.getInicioPlano() != null)
                dataInicioPicker.setValue(planoParaModificar.getInicioPlano().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            if (planoParaModificar.getFimPlano() != null)
                dataFimPicker.setValue(planoParaModificar.getFimPlano().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
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

        // MUDANÇA: Usa o objeto 'planoParaModificar' recebido
        if (planoParaModificar == null) {
            mostrarAlerta("Erro", "Plano de treino não encontrado.", Alert.AlertType.ERROR);
            return;
        }

        planoParaModificar.setInicioPlano(dataInicio);
        planoParaModificar.setFimPlano(dataFim);
        planoTreinoBusiness.modificarPlanoDeTreino(planoParaModificar);

        mostrarAlerta("Sucesso", "Datas atualizadas com sucesso!", Alert.AlertType.INFORMATION);
        handleVoltar(); // Fecha a janela após salvar
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
    }

    public void mostrarAlerta(String titulo, String conteudo, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}