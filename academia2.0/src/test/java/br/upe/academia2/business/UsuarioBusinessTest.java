package br.upe.academia2.business;

import br.upe.academia2.business.UsuarioBusiness.ResultadoExclusao;
import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioJpaRepositorySingleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioBusinessTest {

    @Mock
    private UsuarioJpaRepositorySingleton usuarioRepository;

    private UsuarioBusiness usuarioBusiness;

    @BeforeEach
    void setUp() {
        usuarioBusiness = new UsuarioBusiness(usuarioRepository);
    }

    @Test
    @DisplayName("Deve autenticar e retornar 'ADM' para um usuário administrador válido")
    void testAutenticar_ComAdmValido_DeveRetornarADM() {
        String email = "admin@upe.br";
        String senha = "123";
        Adm admin = new Adm("Admin", null, email, senha, null, null, null);
        when(usuarioRepository.findByEmail(email)).thenReturn(admin);

        String tipoUsuario = usuarioBusiness.autenticar(email, senha);

        assertNotNull(tipoUsuario);
        assertEquals("ADM", tipoUsuario);
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve autenticar e retornar 'COMUM' para um usuário comum válido")
    void testAutenticar_ComComumValido_DeveRetornarCOMUM() {
        String email = "comum@upe.br";
        String senha = "abc";
        Comum comum = new Comum("Comum", null, email, senha, null, null, null);
        when(usuarioRepository.findByEmail(email)).thenReturn(comum);

        String tipoUsuario = usuarioBusiness.autenticar(email, senha);

        assertNotNull(tipoUsuario);
        assertEquals("COMUM", tipoUsuario);
    }

    @Test
    @DisplayName("Não deve autenticar com senha incorreta e deve retornar nulo")
    void testAutenticar_ComSenhaIncorreta_DeveRetornarNulo() {
        String email = "admin@upe.br";
        String senhaCorreta = "123";
        String senhaIncorreta = "errada";
        Adm admin = new Adm("Admin", null, email, senhaCorreta, null, null, null);
        when(usuarioRepository.findByEmail(email)).thenReturn(admin);

        String tipoUsuario = usuarioBusiness.autenticar(email, senhaIncorreta);

        assertNull(tipoUsuario);
    }

    @Test
    @DisplayName("Não deve autenticar com email inexistente e deve retornar nulo")
    void testAutenticar_ComEmailInexistente_DeveRetornarNulo() {
        String email = "inexistente@upe.br";
        when(usuarioRepository.findByEmail(email)).thenReturn(null);

        String tipoUsuario = usuarioBusiness.autenticar(email, "qualquerSenha");

        assertNull(tipoUsuario);
    }

    @Test
    @DisplayName("Deve chamar o método create do repositório ao cadastrar usuário com e-mail válido")
    void testCadastrarUsuario_DeveChamarCreate() {
        Comum novoUsuario = new Comum("Novo", "", "novo@upe.br", "novaSenha", null, null, null);

        usuarioBusiness.cadastrarUsuario(novoUsuario);

        verify(usuarioRepository, times(1)).create(novoUsuario);
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com e-mail inválido e deve lançar exceção")
    void testCadastrarUsuario_EmailInvalido_DeveLancarExcecao() {
        Comum usuarioEmailInvalido = new Comum("Nome", "", "email-invalido", "senha", null, null, null);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioBusiness.cadastrarUsuario(usuarioEmailInvalido);
        });

        verify(usuarioRepository, never()).create(usuarioEmailInvalido);
    }

    @Test
    @DisplayName("Deve retornar a lista de todos os usuários do repositório")
    void testListarUsuarios_DeveRetornarListaCompleta() {
        Adm admin = new Adm("Admin", "", "admin@upe.br", "123", null, null, null);
        Comum comum = new Comum("Comum", "", "comum@upe.br", "abc", null, null, null);
        List<Usuario> listaEsperada = Arrays.asList(admin, comum);
        when(usuarioRepository.listarTodos()).thenReturn(listaEsperada);

        List<Usuario> resultado = usuarioBusiness.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(admin, resultado.get(0));
    }

    @Test
    @DisplayName("Deve retornar apenas a lista de usuários comuns")
    void testListarUsuariosComuns_DeveRetornarApenasComuns() {
        Adm admin = new Adm("Admin", "", "admin@upe.br", "123", null, null, null);
        Comum comum1 = new Comum("Comum 1", "", "comum1@upe.br", "abc", null, null, null);
        Comum comum2 = new Comum("Comum 2", "", "comum2@upe.br", "def", null, null, null);
        List<Usuario> listaCompleta = Arrays.asList(admin, comum1, comum2);
        when(usuarioRepository.listarTodos()).thenReturn(listaCompleta);

        List<Comum> resultado = usuarioBusiness.listarUsuariosComuns();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertFalse(resultado.stream().anyMatch(u -> u.getEmail().equals("admin@upe.br")));
        assertTrue(resultado.stream().anyMatch(u -> u.getEmail().equals("comum1@upe.br")));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há usuários comuns")
    void testListarUsuariosComuns_QuandoNaoHaComuns_DeveRetornarListaVazia() {
        Adm admin = new Adm("Admin", "", "admin@upe.br", "123", null, null, null);
        List<Usuario> listaSoAdmin = Collections.singletonList(admin);
        when(usuarioRepository.listarTodos()).thenReturn(listaSoAdmin);

        List<Comum> resultado = usuarioBusiness.listarUsuariosComuns();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar um usuário comum e retornar SUCESSO")
    void testDeletarUsuario_ComUsuarioComumExistente_DeveRetornarSucesso() {
        String emailParaDeletar = "comum@upe.br";
        Comum usuarioComum = new Comum("Comum", null, emailParaDeletar, "123", null, null, null);

        when(usuarioRepository.findByEmail(emailParaDeletar)).thenReturn(usuarioComum);
        when(usuarioRepository.delete(emailParaDeletar)).thenReturn(true);

        ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(emailParaDeletar);

        assertEquals(ResultadoExclusao.SUCESSO, resultado);
        verify(usuarioRepository, times(1)).findByEmail(emailParaDeletar);
        verify(usuarioRepository, times(1)).delete(emailParaDeletar);
    }

    @Test
    @DisplayName("Não deve deletar um usuário inexistente e retornar NAO_ENCONTRADO")
    void testDeletarUsuario_ComUsuarioInexistente_DeveRetornarNaoEncontrado() {
        String emailParaDeletar = "naoexiste@upe.br";
        when(usuarioRepository.findByEmail(emailParaDeletar)).thenReturn(null);

        ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(emailParaDeletar);

        assertEquals(ResultadoExclusao.NAO_ENCONTRADO, resultado);
        verify(usuarioRepository, times(1)).findByEmail(emailParaDeletar);
        verify(usuarioRepository, never()).delete(emailParaDeletar);
    }

    @Test
    @DisplayName("Não deve deletar um usuário ADM e retornar NAO_PERMITIDO_ADM")
    void testDeletarUsuario_ComUsuarioAdm_DeveRetornarNaoPermitido() {
        String emailParaDeletar = "admin@upe.br";
        Adm usuarioAdmin = new Adm("Admin", null, emailParaDeletar, "123", null, null, null);
        when(usuarioRepository.findByEmail(emailParaDeletar)).thenReturn(usuarioAdmin);

        ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(emailParaDeletar);

        assertEquals(ResultadoExclusao.NAO_PERMITIDO_ADM, resultado);
        verify(usuarioRepository, times(1)).findByEmail(emailParaDeletar);
        verify(usuarioRepository, never()).delete(emailParaDeletar);
    }

    @Test
    @DisplayName("Deve chamar o método update do repositório ao atualizar um usuário")
    void testAtualizarUsuario_DeveChamarUpdate() {
        Comum usuarioParaAtualizar = new Comum("Atualizado", "", "atualizar@upe.br", "novaSenha", null, null, null);

        usuarioBusiness.atualizarUsuario(usuarioParaAtualizar);

        verify(usuarioRepository, times(1)).update(usuarioParaAtualizar);
    }
}
