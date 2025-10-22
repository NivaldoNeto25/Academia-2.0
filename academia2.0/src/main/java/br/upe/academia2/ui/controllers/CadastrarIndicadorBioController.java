package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.IndicadorBiomedicoBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastrarIndicadorBioController {
    @FXML private TextField alturaField;
    @FXML private TextField pesoField;
    @FXML private TextField massaMagraField;
    @FXML private TextField percGorduraField;
    @FXML private Label mensagemLabel;

    private Usuario usuarioLogado;
    private final IndicadorBiomedicoBusiness indicadorbio = new IndicadorBiomedicoBusiness();

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public void setUsuario(Usuario usuario) {
        setUsuarioLogado(usuario);
    }

    @FXML
    public void handleCadastrarIndicadores() {
        try {
            if (usuarioLogado == null) {
                mensagemLabel.setText("Usuário não logado. Operação não permitida.");
                return;
            }

            double altura = Double.parseDouble(alturaField.getText());
            double peso = Double.parseDouble(pesoField.getText());
            double percGordura = Double.parseDouble(percGorduraField.getText());
            double percMassaMagra = Double.parseDouble(massaMagraField.getText());

            if (altura <= 0 || peso <= 0) {
                mensagemLabel.setText("Peso e altura devem ser maiores que zero.");
                return;
            }
            double imc = peso / (altura * altura);

            IndicadorBiomedico indicador = new IndicadorBiomedico(
                    usuarioLogado.getEmail(), peso, altura, percGordura, percMassaMagra, imc, new java.util.Date()
            );

            indicadorbio.cadastrarIndicador(usuarioLogado, indicador);

            Stage atual = (Stage) alturaField.getScene().getWindow();
            atual.close();

        } catch (NumberFormatException e) {
            mensagemLabel.setText("Digite valores numéricos válidos.");
        }
    }
}