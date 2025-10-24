package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.PlanoTreinoBusiness;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;

public class SecaoTreinoController {

    @FXML private ComboBox<PlanoTreino> comboPlanos;  // ComboBox para selecionar o plano
    @FXML private ListView listaSecoes;                // Lista de seções
    @FXML private Button btnRealizarSecao;             // Botão para realizar as seções

    private Usuario usuarioLogado;
    private PlanoTreinoBusiness planoTreinoBusiness;

    public void initialize() {
        planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),
                new PlanoTreinoCsvRepository()
        );
        carregarPlanosDoUsuario();  // Carrega os planos assim que o controlador for inicializado

        // Ao selecionar um plano, carrega as seções do plano
        comboPlanos.setOnAction(e -> carregarSecoesDoPlano());
    }

    // Método para carregar os planos do usuário no ComboBox
    private void carregarPlanosDoUsuario() {
        // Exemplo: Carregar planos de treino (substitua com o código correto para obter os planos)
        var planos = planoTreinoBusiness.listarPlanosPorUsuario(usuarioLogado); // Substitua com o método correto
        if (planos != null && !planos.isEmpty()) {
            comboPlanos.setItems(FXCollections.observableArrayList(planos));
        }
    }

    // Método para carregar as seções de um plano selecionado
    private void carregarSecoesDoPlano() {
        PlanoTreino planoSelecionado = comboPlanos.getValue();
        if (planoSelecionado != null) {
            listaSecoes.setItems(FXCollections.observableArrayList(planoSelecionado.getSecoes()));
        }
    }

    // Método para lidar com o botão "Realizar Seções"
    @FXML
    public void handleRealizarSecao() {
        PlanoTreino plano = comboPlanos.getValue();
        if (plano != null && plano.getSecoes() != null) {
            // Ação para abrir a tela de exercícios (SecaoExercicio.fxml)
            loadExercicioTela();
        } else {
            mostrarAlerta("Seleção Inválida", "Por favor, selecione um plano e uma seção.", Alert.AlertType.WARNING);
        }
    }

    private void loadExercicioTela() {
        // Lógica para carregar a próxima tela (SecaoExercicio.fxml)
    }

    public void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
