package br.upe.academia2.business;

import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioBusinessTest {

    // Cria um mock (objeto falso) do repositório para isolar a classe de negócio.
    @Mock
    private UsuarioCsvRepository usuarioRepository;

    // Injeta o mock 'usuarioRepository' na instância de UsuarioBusiness.
    private UsuarioBusiness usuarioBusiness;

    @BeforeEach
    void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        usuarioBusiness = new UsuarioBusiness();

        Field field = UsuarioBusiness.class.getDeclaredField("usuarioRepository");
        field.setAccessible(true);

        field.setAccessible(true);
        field.set(usuarioBusiness, usuarioRepository);
    }

    @Test
    @DisplayName("Deve autenticar e retornar 'ADM' para um usuário administrador válido")
    void testAutenticar_ComAdmValido_DeveRetornarADM() {
        // Arrange (Organização)
        String email = "admin@upe.br";
        String senha = "123";
        Adm admin = new Adm("Admin", null, email, senha, null, null, null);

        // Define o comportamento do mock: quando findByEmail for chamado, retorne o objeto 'admin'.
        when(usuarioRepository.findByEmail(email)).thenReturn(admin);

        // Act (Ação)
        String tipoUsuario = usuarioBusiness.autenticar(email, senha);

        // Assert (Verificação)
        assertNotNull(tipoUsuario);
        assertEquals("ADM", tipoUsuario);
        verify(usuarioRepository, times(1)).persistirNoCsv();
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve autenticar e retornar 'COMUM' para um usuário comum válido")
    void testAutenticar_ComComumValido_DeveRetornarCOMUM() {
        // Arrange
        String email = "comum@upe.br";
        String senha = "abc";
        Comum comum = new Comum("Comum", null, email, senha, null, null, null);
        when(usuarioRepository.findByEmail(email)).thenReturn(comum);

        // Act
        String tipoUsuario = usuarioBusiness.autenticar(email, senha);

        // Assert
        assertNotNull(tipoUsuario);
        assertEquals("COMUM", tipoUsuario);
    }

    @Test
    @DisplayName("Não deve autenticar com senha incorreta e deve retornar nulo")
    void testAutenticar_ComSenhaIncorreta_DeveRetornarNulo() {
        // Arrange
        String email = "admin@upe.br";
        String senhaCorreta = "123";
        String senhaIncorreta = "errada";
        Adm admin = new Adm("Admin", null, email, senhaCorreta, null, null, null);
        when(usuarioRepository.findByEmail(email)).thenReturn(admin);

        // Act
        String tipoUsuario = usuarioBusiness.autenticar(email, senhaIncorreta);

        // Assert
        assertNull(tipoUsuario);
    }

    @Test
    @DisplayName("Não deve autenticar com email inexistente e deve retornar nulo")
    void testAutenticar_ComEmailInexistente_DeveRetornarNulo() {
        // Arrange
        String email = "inexistente@upe.br";
        when(usuarioRepository.findByEmail(email)).thenReturn(null);

        // Act
        String tipoUsuario = usuarioBusiness.autenticar(email, "qualquerSenha");

        // Assert
        assertNull(tipoUsuario);
    }

    @Test
    @DisplayName("Deve chamar o método create do repositório ao cadastrar usuário")
    void testCadastrarUsuario_DeveChamarCreate() {
        // Arrange
        Comum novoUsuario = new Comum("Novo", "", "novo@upe.br", "novaSenha", null, null, null);

        // Act
        usuarioBusiness.cadastrarUsuario(novoUsuario);

        // Assert
        verify(usuarioRepository, times(1)).persistirNoCsv();
        // Verifica se o método 'create' foi chamado exatamente uma vez com o objeto 'novoUsuario'.
        verify(usuarioRepository, times(1)).create(novoUsuario);
    }

    @Test
    @DisplayName("Deve retornar a lista de todos os usuários do repositório")
    void testListarUsuarios_DeveRetornarListaCompleta() {
        // Arrange
        Adm admin = new Adm("Admin", "", "admin@upe.br", "123", null, null, null);
        Comum comum = new Comum("Comum", "", "comum@upe.br", "abc", null, null, null);
        List<Usuario> listaEsperada = Arrays.asList(admin, comum);
        when(usuarioRepository.listarTodos()).thenReturn(listaEsperada);

        // Act
        List<Usuario> resultado = usuarioBusiness.listarUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(admin, resultado.get(0));
    }

    @Test
    @DisplayName("Deve retornar apenas a lista de usuários comuns")
    void testListarUsuariosComuns_DeveRetornarApenasComuns() {
        // Arrange
        Adm admin = new Adm("Admin", "", "admin@upe.br", "123", null, null, null);
        Comum comum1 = new Comum("Comum 1", "", "comum1@upe.br", "abc", null, null, null);
        Comum comum2 = new Comum("Comum 2", "", "comum2@upe.br", "def", null, null, null);
        List<Usuario> listaCompleta = Arrays.asList(admin, comum1, comum2);
        when(usuarioRepository.listarTodos()).thenReturn(listaCompleta);

        // Act
        List<Comum> resultado = usuarioBusiness.listarUsuariosComuns();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        // Garante que o admin não está na lista de comuns
        assertFalse(resultado.stream().anyMatch(u -> u.getEmail().equals("admin@upe.br")));
        assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals("comum1@upe.br")));
    }
    
    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há usuários comuns")
    void testListarUsuariosComuns_QuandoNaoHaComuns_DeveRetornarListaVazia() {
        // Arrange
        Adm admin = new Adm("Admin", "", "admin@upe.br", "123", null, null, null);
        List<Usuario> listaSoAdmin = Collections.singletonList(admin);
        when(usuarioRepository.listarTodos()).thenReturn(listaSoAdmin);

        // Act
        List<Comum> resultado = usuarioBusiness.listarUsuariosComuns();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve chamar o método delete do repositório ao deletar um usuário")
    void testDeletarUsuario_DeveChamarDelete() {
        // Arrange
        String emailParaDeletar = "remover@upe.br";

        // Act
        usuarioBusiness.deletarUsuario(emailParaDeletar);

        // Assert
        verify(usuarioRepository, times(1)).persistirNoCsv();
        verify(usuarioRepository, times(1)).delete(emailParaDeletar);
    }

    @Test
    @DisplayName("Deve chamar o método update do repositório ao atualizar um usuário")
    void testAtualizarUsuario_DeveChamarUpdate() {
        // Arrange
        Comum usuarioParaAtualizar = new Comum("Atualizado", "", "atualizar@upe.br", "novaSenha", null, null, null);

        // Act
        usuarioBusiness.atualizarUsuario(usuarioParaAtualizar);

        // Assert
        verify(usuarioRepository, times(1)).persistirNoCsv();
        verify(usuarioRepository, times(1)).update(usuarioParaAtualizar);
    }
}