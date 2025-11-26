package br.upe.academia2.ui.controllers;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioJpaRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usuarioField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private Label mensagemLabel;

    @FXML
    private ImageView logoImageView;

    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioJpaRepository.getInstance());

    Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/LogoFit.png")));
            logoImageView.setImage(logo);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erro ao carregar imagem do logo", e);
            logoImageView.setVisible(false);
        }
    }

    @FXML
    public void handleEntrar() {
        String usuario = usuarioField.getText();
        String senha = senhaField.getText();
        String tipoUsuario = usuarioBusiness.autenticar(usuario, senha);

        if (tipoUsuario != null) {
            Usuario usuarioLogado = UsuarioJpaRepository.getInstance().findByEmail(usuario);
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
            } catch (IOException e) {
                logger.log(Level.WARNING, "Erro ao carregar FXML", e);
            }
        } else {
            mensagemLabel.setText("Usuário ou senha incorretos.");
        }
    }
}
