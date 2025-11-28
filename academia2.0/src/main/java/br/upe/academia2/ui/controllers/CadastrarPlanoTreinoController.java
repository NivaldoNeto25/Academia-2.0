package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoJpaRepository;
import br.upe.academia2.data.repository.UsuarioJpaRepositorySingleton;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker; 
import javafx.stage.Stage;

import java.time.LocalDate; 
import java.time.ZoneId; 
import java.util.Date;

public class CadastrarPlanoTreinoController {

    @FXML private TextField nomeField;
    @FXML private DatePicker dataInicioPicker; 
    @FXML private DatePicker dataFimPicker;    
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;
    private final PlanoTreinoBusiness planoTreinoBusiness;


    public CadastrarPlanoTreinoController() {
        this.planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioJpaRepositorySingleton.getInstance(),
                new PlanoTreinoJpaRepository()
        );
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }


    @FXML
    public void handleCadastrar() {
        String nome = nomeField.getText().trim();
        LocalDate dataInicioLocal = dataInicioPicker.getValue(); // Pega o valor do calendário
        LocalDate dataFimLocal = dataFimPicker.getValue();     // Pega o valor do calendário

        if (nome.isEmpty() || dataInicioLocal == null || dataFimLocal == null) {
            mostrarAlerta("Campos obrigatórios!", "Preencha todos os campos antes de salvar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Date dataInicio = Date.from(dataInicioLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dataFim = Date.from(dataFimLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (dataInicio.after(dataFim)) {
                mostrarAlerta("Erro de data", "A data de início não pode ser depois da data de término.", Alert.AlertType.ERROR);
                return;
            }

            PlanoTreino novoPlano = new PlanoTreino(0, nome, dataInicio, dataFim, usuarioLogado);
            planoTreinoBusiness.cadastrarPlanoDeTreino(usuarioLogado, novoPlano);

            mostrarAlerta("Sucesso", "Plano de treino cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao cadastrar o plano: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        
    }

    @FXML
    public void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
    }

    public void limparCampos() {
        nomeField.clear();
        dataInicioPicker.setValue(null); 
        dataFimPicker.setValue(null);    
    }

    public void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}