package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastrarPlanoTreinoController {

    @FXML private TextField nomeField;
    @FXML private TextField dataInicioField;
    @FXML private TextField dataFimField;
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;
    private Stage stageAnterior;
    private final PlanoTreinoBusiness planoTreinoBusiness;

    private static final String FORMATO_DATA = "dd/MM/yyyy";

    public CadastrarPlanoTreinoController() {
        this.planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    @FXML
    public void handleCadastrar() {
        String nome = nomeField.getText().trim();
        String dataInicioStr = dataInicioField.getText().trim();
        String dataFimStr = dataFimField.getText().trim();

        if (nome.isEmpty() || dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
            mostrarAlerta("Campos obrigatórios!", "Preencha todos os campos antes de salvar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);
            Date dataInicio = sdf.parse(dataInicioStr);
            Date dataFim = sdf.parse(dataFimStr);

            PlanoTreino novoPlano = new PlanoTreino(0, nome, dataInicio, dataFim, usuarioLogado);
            planoTreinoBusiness.cadastrarPlanoDeTreino(usuarioLogado, novoPlano);

            mostrarAlerta("Sucesso", "Plano de treino cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();

        } catch (ParseException e) {
            mostrarAlerta("Erro de data", "Formato de data inválido. Use o formato " + FORMATO_DATA, Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao cadastrar o plano: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }

    public void limparCampos() {
        nomeField.clear();
        dataInicioField.clear();
        dataFimField.clear();
    }

    public void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}