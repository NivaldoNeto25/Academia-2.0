package br.upe.academia2.ui.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usuarioField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private Label mensagemLabel;
    public Button btnVoltar;
    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioCsvRepository.getInstance());

    @FXML
    private void handleEntrar() {
        String usuario = usuarioField.getText();
        String senha = senhaField.getText();
        String tipoUsuario = usuarioBusiness.autenticar(usuario, senha);

        if (tipoUsuario != null) {
            Usuario usuarioLogado = UsuarioCsvRepository.getInstance().findByEmail(usuario);
            if (usuarioLogado == null) {
                mensagemLabel.setText("Usuário autenticado mas não encontrado.");
                return;
            }
            mensagemLabel.setText("Login realizado com sucesso!");

            try {
                Stage stage = (Stage) usuarioField.getScene().getWindow();
                FXMLLoader loader;

                if ("ADM".equals(tipoUsuario)) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/AdmMenu.fxml"));
                    Scene cena = new Scene(loader.load());
                    AdmMenuController controller = loader.getController();
                    controller.setAdm((Adm) usuarioLogado);
                    stage.setScene(cena);
                } else if ("COMUM".equals(tipoUsuario)) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/AlunoMenu.fxml"));
                    Scene cena = new Scene(loader.load());
                    AlunoMenuController controller = loader.getController();
                    controller.setAluno(usuarioLogado);
                    stage.setScene(cena);
                }
            } catch (Exception e) {
                mensagemLabel.setText("Erro: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            mensagemLabel.setText("Usuário ou senha incorretos.");
        }
    }

    @FXML
    private void handleSair() {
        Stage stageAtual = (Stage) btnVoltar.getScene().getWindow();
        stageAtual.close();
    }

}