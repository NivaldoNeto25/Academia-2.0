package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioJpaRepositoryTest {

    private UsuarioJpaRepositorySingleton usuarioRepository;
    private EntityManagerFactory emfTest;

    @BeforeEach
    void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
        
        emfTest = Persistence.createEntityManagerFactory("academiaTestPU");
        
        usuarioRepository = usuarioRepository.getInstance();

        Field field = UsuarioJpaRepositorySingleton.class.getDeclaredField("emf");
        field.setAccessible(true);
        
        field.set(null, emfTest); 
    }

    @AfterEach
    void tearDown() {
        // Fecha a fábrica após cada teste para limpar o banco H2 (drop tables)
        if (emfTest != null && emfTest.isOpen()) {
            emfTest.close();
        }
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve salvar um usuário no banco H2 e recuperar com sucesso")
    void testCreate_NovoUsuario() {
        // Arrange (Usamos uma classe concreta 'Comum' pois o JPA precisa instanciar)
        Usuario usuario = new Comum("João Teste", "8199999", "teste@email.com", "senha123", 80.0, 1.80, 15.0);

        // Act - Executa o INSERT real no H2
        Usuario salvo = usuarioRepository.create(usuario);

        // Assert
        assertNotNull(salvo);
        
        // Verificação dupla: Busca no banco para garantir que persistiu
        Usuario encontrado = usuarioRepository.findByEmail("teste@email.com");
        assertNotNull(encontrado);
        assertEquals("João Teste", encontrado.getNome());
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve retornar null ao buscar email inexistente")
    void testFindByEmail_NaoEncontrado() {
        Usuario resultado = usuarioRepository.findByEmail("naoexiste@email.com");
        assertNull(resultado);
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve atualizar os dados no banco real")
    void testUpdate() {
        // Arrange
        Usuario usuario = new Comum("Maria", "111", "maria@email.com", "123", 60.0, 1.65, 20.0);
        usuarioRepository.create(usuario);

        // Modifica o objeto
        usuario.setNome("Maria Silva");
        usuario.setPesoAtual(58.0);

        // Act - Executa o UPDATE real
        Usuario atualizado = usuarioRepository.update(usuario);

        // Assert
        assertEquals("Maria Silva", atualizado.getNome());
        
        // Busca nova instância do banco para garantir que o update persistiu
        Usuario doBanco = usuarioRepository.findByEmail("maria@email.com");
        assertEquals(58.0, doBanco.getPesoAtual());
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve deletar usuário do banco")
    void testDelete_UsuarioExistente() {
        // Arrange
        Usuario usuario = new Comum("Pedro", "222", "pedro@email.com", "123", 90.0, 1.85, 10.0);
        usuarioRepository.create(usuario);

        // Garante que existe antes de deletar
        assertNotNull(usuarioRepository.findByEmail("pedro@email.com"));

        // Act - Executa DELETE
        boolean deletado = usuarioRepository.delete("pedro@email.com");

        // Assert
        assertTrue(deletado);
        assertNull(usuarioRepository.findByEmail("pedro@email.com"));
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve listar todos os usuários salvos")
    void testListarTodos() {
        // Arrange - Limpeza implícita ocorre no tearDown/setUp (create-drop), banco começa vazio
        usuarioRepository.create(new Comum("User1", "1", "u1@t.com", "s", 0.0, 0.0, 0.0));
        usuarioRepository.create(new Comum("User2", "2", "u2@t.com", "s", 0.0, 0.0, 0.0));

        // Act
        List<Usuario> lista = usuarioRepository.listarTodos();

        // Assert
        assertNotNull(lista);
        assertEquals(2, lista.size());
    }
}