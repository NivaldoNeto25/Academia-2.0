package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.SecaoTreinoBusiness;
import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SecaoTreinoController {

    @FXML private Label labelPlanoNome;
    @FXML private Button btnIniciarSessao;
    @FXML private Button btnVoltar;

    private Usuario usuarioLogado;
    private Stage stageAnterior;

    private PlanoTreinoBusiness planoTreinoBusiness;
    private SecaoTreinoBusiness secaoTreinoBusiness;

    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
        secaoTreinoBusiness = new SecaoTreinoBusiness();

        btnIniciarSessao.setOnAction(e -> iniciarSessao());
        btnVoltar.setOnAction(e -> voltar());
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        carregarDadosPlano();
    }

    public void setStageAnterior(Stage stage) {
        this.stageAnterior = stage;
    }

    private void carregarDadosPlano() {
        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
        if (plano != null) {
            labelPlanoNome.setText(plano.getNomePlano());
        } else {
            labelPlanoNome.setText("Nenhum plano encontrado");
            btnIniciarSessao.setDisable(true);
        }
    }

    private void iniciarSessao() {
        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);
        if (plano == null || plano.getSecoes().isEmpty()) {
            // Aqui você pode mostrar um alerta informando que não há plano ou seções
            return;
        }

        secaoTreinoBusiness.iniciarSessao(plano);

        // Logica para executar treino, mostrar cartões etc,
        // Depende da arquitetura da sua UI — aqui apenas placeholder
    }

    private void voltar() {
        Stage atual = (Stage) btnVoltar.getScene().getWindow();
        atual.close();
        if (stageAnterior != null) stageAnterior.show();
    }
}