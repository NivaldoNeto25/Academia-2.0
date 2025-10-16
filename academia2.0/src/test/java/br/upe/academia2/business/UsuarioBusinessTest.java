package br.upe.academia2.business;

import br.upe.academia2.business.UsuarioBusiness.ResultadoExclusao;
import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

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
    private UsuarioCsvRepository usuarioRepository;

    // A classe a ser testada. A injeção será feita manualmente no setUp.
    private UsuarioBusiness usuarioBusiness;

    @BeforeEach
    void setUp() {
        // Injeta o mock no construtor da classe de negócio
        // (Assumindo que você aplicou a injeção de dependência na sua classe UsuarioBusiness)
        usuarioBusiness = new UsuarioBusiness(usuarioRepository);
    }

    @Test
    @DisplayName("Deve autenticar e retornar 'ADM' para um usuário administrador válido")
    void testAutenticar_ComAdmValido_DeveRetornarADM() {
        // Arrange
        String email = "admin@upe.br";
        String senha = "123";
        Adm admin = new Adm("Admin", null, email, senha, null, null, null);
        when(usuarioRepository.findByEmail(email)).thenReturn(admin);

        // Act
        String tipoUsuario = usuarioBusiness.autenticar(email, senha);

        // Assert
        assertNotNull(tipoUsuario);
        assertEquals("ADM", tipoUsuario);
        // CORREÇÃO: A verificação de persistirNoCsv() foi removida.
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
        // CORREÇÃO: A verificação de persistirNoCsv() foi removida.
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
    @DisplayName("Deve retornar apenas la lista de usuários comuns")
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
    @DisplayName("Deve deletar um usuário comum e retornar SUCESSO")
    void testDeletarUsuario_ComUsuarioComumExistente_DeveRetornarSucesso() {
        // ARRANGE
        String emailParaDeletar = "comum@upe.br";
        Comum usuarioComum = new Comum("Comum", null, emailParaDeletar, "123", null, null, null);
        
        // Simula que o usuário foi ENCONTRADO
        when(usuarioRepository.findByEmail(emailParaDeletar)).thenReturn(usuarioComum);
        // Simula que a deleção no repositório foi bem-sucedida
        when(usuarioRepository.delete(emailParaDeletar)).thenReturn(true);

        // ACT
        ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(emailParaDeletar);

        // ASSERT
        assertEquals(ResultadoExclusao.SUCESSO, resultado);
        verify(usuarioRepository, times(1)).findByEmail(emailParaDeletar);
        verify(usuarioRepository, times(1)).delete(emailParaDeletar); // Agora a verificação passa!
    }

    @Test
    @DisplayName("Não deve deletar um usuário inexistente e retornar NAO_ENCONTRADO")
    void testDeletarUsuario_ComUsuarioInexistente_DeveRetornarNaoEncontrado() {
        // ARRANGE
        String emailParaDeletar = "naoexiste@upe.br";
        // Simula que o usuário NÃO foi encontrado
        when(usuarioRepository.findByEmail(emailParaDeletar)).thenReturn(null);

        // ACT
        ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(emailParaDeletar);

        // ASSERT
        assertEquals(ResultadoExclusao.NAO_ENCONTRADO, resultado);
        verify(usuarioRepository, times(1)).findByEmail(emailParaDeletar);
        verify(usuarioRepository, never()).delete(emailParaDeletar); // Verifica que delete NUNCA foi chamado
    }

    @Test
    @DisplayName("Não deve deletar um usuário ADM e retornar NAO_PERMITIDO_ADM")
    void testDeletarUsuario_ComUsuarioAdm_DeveRetornarNaoPermitido() {
        // ARRANGE
        String emailParaDeletar = "admin@upe.br";
        Adm usuarioAdmin = new Adm("Admin", null, emailParaDeletar, "123", null, null, null);
        // Simula que o usuário ADM foi encontrado
        when(usuarioRepository.findByEmail(emailParaDeletar)).thenReturn(usuarioAdmin);

        // ACT
        ResultadoExclusao resultado = usuarioBusiness.deletarUsuario(emailParaDeletar);

        // ASSERT
        assertEquals(ResultadoExclusao.NAO_PERMITIDO_ADM, resultado);
        verify(usuarioRepository, times(1)).findByEmail(emailParaDeletar);
        verify(usuarioRepository, never()).delete(emailParaDeletar); // Verifica que delete NUNCA foi chamado
    }
    
    @Test
    @DisplayName("Deve chamar o método update do repositório ao atualizar um usuário")
    void testAtualizarUsuario_DeveChamarUpdate() {
        // Arrange
        Comum usuarioParaAtualizar = new Comum("Atualizado", "", "atualizar@upe.br", "novaSenha", null, null, null);

        // Act
        usuarioBusiness.atualizarUsuario(usuarioParaAtualizar);

        // Assert
        // CORREÇÃO: A verificação de persistirNoCsv() foi removida.
        verify(usuarioRepository, times(1)).update(usuarioParaAtualizar);
    }
}