package br.upe.academia2.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.AdmMenuController;
import br.upe.academia2.ui.controllers.CadastroAlunoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroAlunoControllerTest {

    @InjectMocks
    private CadastroAlunoController controller;
    @Mock
    private UsuarioBusiness usuarioBusiness;
    @Mock
    private AdmMenuController admMenuController;

    private String nome;
    private String email;
    private String senha;
    private String mensagem;

    @BeforeEach
    void setUp() {
        controller.setAdmMenuController(admMenuController);
        nome = "";
        email = "";
        senha = "";
        mensagem = "";
    }

    // metodo que simula handleCadastrar sem JavaFX com uma variavel teste ela em todos
    private void handleCadastrarSimulado() {
        mensagem = "";

        if (nome == null || nome.isBlank() || email == null || email.isBlank() || senha == null || senha.isBlank()) {
            mensagem = "Todos os campos são obrigatórios.";
            return;
        }

        List<Usuario> usuariosExistentes = usuarioBusiness.listarUsuarios();
        if (usuariosExistentes.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            mensagem = "Já existe um aluno com esse e-mail.";
            return;
        }

        Usuario novo = new Comum(nome, null, email, senha, null, null, null);
        try {
            usuarioBusiness.cadastrarUsuario(novo);
            if (admMenuController != null) {
                admMenuController.atualizarTabelaAlunos();
            }
            mensagem = "Aluno cadastrado com sucesso!";
        } catch (IllegalArgumentException ex) {
            mensagem = "Digite um e-mail válido!";
        }
    }

    @Test
    void testCamposVazios() {
        nome = "";
        email = "";
        senha = "";
        handleCadastrarSimulado();
        assertEquals("Todos os campos são obrigatórios.", mensagem);
    }

    @Test
    void testEmailExistente() {
        nome = "Nivaldo";
        email = "nivaldo@email.com";
        senha = "senha123";

        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Comum("SEVERINO", null, "nivaldo@email.com", "123", null, null, null));
        when(usuarioBusiness.listarUsuarios()).thenReturn(usuarios);
        handleCadastrarSimulado();
        assertEquals("Já existe um aluno com esse e-mail.", mensagem);

        verify(usuarioBusiness, never()).cadastrarUsuario(any(Usuario.class));
    }

    @Test
    void testCadastroSucesso() {
        nome = "MariaMilena";
        email = "mariamilena@email.com";
        senha = "senha123";

        when(usuarioBusiness.listarUsuarios()).thenReturn(new ArrayList<>());
        doNothing().when(usuarioBusiness).cadastrarUsuario(any(Usuario.class));

        handleCadastrarSimulado();
        assertEquals("Aluno cadastrado com sucesso!", mensagem);

        verify(usuarioBusiness).cadastrarUsuario(any(Usuario.class));
        verify(admMenuController).atualizarTabelaAlunos();
    }

    @Test
    void testEmailInvalido() {
        nome = "Xavierfilho";
        email = "xavierpai";
        senha = "senha123";

        when(usuarioBusiness.listarUsuarios()).thenReturn(new ArrayList<>());
        doThrow(new IllegalArgumentException("Email inválido"))
                .when(usuarioBusiness).cadastrarUsuario(any(Usuario.class));

        handleCadastrarSimulado();
        assertEquals("Digite um e-mail válido!", mensagem);
    }
}

