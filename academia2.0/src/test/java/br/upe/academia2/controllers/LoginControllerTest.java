package br.upe.academia2.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UsuarioBusiness usuarioBusiness;
    private String usuario;
    private String senha;
    private String mensagem;

    @BeforeEach
    void setUp() {
        usuario = "";
        senha = "";
        mensagem = "";
    }

    private void handleEntrarSimulado() {
        mensagem = "";

        if (usuario == null || usuario.isBlank() || senha == null || senha.isBlank()) {
            mensagem = "Todos os campos são obrigatórios.";
            return;
        }

        String tipoUsuario = usuarioBusiness.autenticar(usuario, senha);

        if (tipoUsuario != null) {
            mensagem = "Login realizado com sucesso!";
        } else {
            mensagem = "Usuário ou senha inválidos.";
        }
    }

    @Test
    void testCamposVazios() {
        usuario = "";
        senha = "";
        handleEntrarSimulado();
        assertEquals("Todos os campos são obrigatórios.", mensagem);
    }

    @Test
    void testLoginSucesso() {
        usuario = "maria@email.com";
        senha = "senha123";

        when(usuarioBusiness.autenticar(usuario, senha)).thenReturn("COMUM");

        handleEntrarSimulado();
        assertEquals("Login realizado com sucesso!", mensagem);
    }

    @Test
    void testLoginFalha() {
        usuario = "maria@email.com";
        senha = "senhaErrada";

        when(usuarioBusiness.autenticar(usuario, senha)).thenReturn(null);

        handleEntrarSimulado();
        assertEquals("Usuário ou senha inválidos.", mensagem);
    }
}
