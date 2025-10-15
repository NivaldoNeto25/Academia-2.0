package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastrarIndicadorBioController {
    @FXML private TextField Altura;
    @FXML private TextField Peso;
    @FXML private TextField MassaMagra;
    @FXML private TextField PercGordura;
    @FXML private Button btnVoltar;
    @FXML private Label mensagemLabel;

    private Stage stageAnterior;
    private final IndicadorBiomedicoBusiness indicadorbio = new IndicadorBiomedicoBusiness();

    private Usuario usuarioLogado;

    public void setStageAnterior(Stage stageAnterior) {
        this.stageAnterior = stageAnterior;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    @FXML
    private void handleCadastrarIndicadores() {

        try {
            if (usuarioLogado == null) {
                mensagemLabel.setText("Usuário não logado. Operação não permitida.");
                return;
            }

            double altura = Double.parseDouble(Altura.getText());
            double peso = Double.parseDouble(Peso.getText());
            double percGordura = Double.parseDouble(PercGordura.getText());
            double percMassaMagra = Double.parseDouble(MassaMagra.getText());

            if (altura <= 0 || peso <= 0) {
                mensagemLabel.setText("Peso e altura devem ser maiores que zero.");
                return;
            }
            double imc = peso / (altura * altura);

            IndicadorBiomedico indicador = new IndicadorBiomedico(
                    usuarioLogado.getEmail(), peso, altura, percGordura, percMassaMagra, imc, new java.util.Date()
            );

            indicadorbio.cadastrarIndicador(usuarioLogado, indicador);

            mensagemLabel.setText("Indicador cadastrado com sucesso!");
            limparCampos();

        } catch (NumberFormatException e) {
            mensagemLabel.setText("Digite valores numéricos válidos.");
        } catch (Exception e) {
            mensagemLabel.setText("Erro ao cadastrar indicador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        Altura.clear();
        Peso.clear();
        PercGordura.clear();
        MassaMagra.clear();
    }

    @FXML
    private void handleVoltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}

