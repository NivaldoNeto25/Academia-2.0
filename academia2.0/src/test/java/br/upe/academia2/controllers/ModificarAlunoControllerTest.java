package br.upe.academia2.controllers;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.ui.controllers.AdmMenuController;
import br.upe.academia2.ui.controllers.ModificarAlunoController;
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
class ModificarAlunoControllerTest {

    @InjectMocks
    private ModificarAlunoController controller;

    @Mock
    private UsuarioBusiness usuarioBusiness;

    @Mock
    private AdmMenuController admMenuController;

    private List<Usuario> usuariosExistentes;

    @BeforeEach
    void setUp() {
        controller.setAdmMenuController(admMenuController);
        usuariosExistentes = new ArrayList<>();
    }

    private String modificar(String email, String nome, String senha) {
        // evitar erro de "Unnecessary Stubbing"
        lenient().when(usuarioBusiness.listarUsuarios()).thenReturn(usuariosExistentes);

        Usuario existente = usuariosExistentes.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            return "Aluno não encontrado.";
        }

        if (!nome.isBlank()) existente.setNome(nome);
        if (!senha.isBlank()) existente.setSenha(senha);

        usuarioBusiness.atualizarUsuario(existente);
        usuarioBusiness.salvarAlteracoesNoCsv();

        if (admMenuController != null) {
            admMenuController.atualizarTabelaAlunos();
        }

        return "Modificação realizada com sucesso!";
    }

    @Test
    void testAlunoNaoExistente() {
        String msg = modificar("naoexistente@email.com", "NovoNome", "NovaSenha");
        assertEquals("Aluno não encontrado.", msg);

        verify(usuarioBusiness, never()).atualizarUsuario(any());
        verify(usuarioBusiness, never()).salvarAlteracoesNoCsv();
        verify(admMenuController, never()).atualizarTabelaAlunos();
    }

    @Test
    void testModificarNomeESenha() {
        Usuario u = new Comum("NOVO", null, "novo@email.com", "123", null, null, null);
        usuariosExistentes.add(u);

        String msg = modificar("novo@email.com", "NovoNome", "NovaSenha");

        assertEquals("NovoNome", u.getNome());
        assertEquals("NovaSenha", u.getSenha());
        assertEquals("Modificação realizada com sucesso!", msg);

        verify(usuarioBusiness).atualizarUsuario(u);
        verify(usuarioBusiness).salvarAlteracoesNoCsv();
        verify(admMenuController).atualizarTabelaAlunos();
    }

    @Test
    void testModificarApenasNome() {
        Usuario u = new Comum("Antigo", null, "xavier@email.com", "senha", null, null, null);
        usuariosExistentes.add(u);

        String msg = modificar("xavier@email.com", "NomeAtualizado", "");

        assertEquals("NomeAtualizado", u.getNome());
        assertEquals("senha", u.getSenha());
        assertEquals("Modificação realizada com sucesso!", msg);
    }

    @Test
    void testModificarApenasSenha() {
        Usuario u = new Comum("Nome", null, "dudinha@email.com", "antiga", null, null, null);
        usuariosExistentes.add(u);

        String msg = modificar("dudinha@email.com", "", "novaSenha");

        assertEquals("Nome", u.getNome());
        assertEquals("novaSenha", u.getSenha());
        assertEquals("Modificação realizada com sucesso!", msg);
    }
}

