package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Usuario;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioJpaRepositoryTest {

    private UsuarioJpaRepository usuarioRepository;

    @Mock
    private EntityManagerFactory emfMock;
    @Mock
    private EntityManager emMock;
    @Mock
    private EntityTransaction txMock;
    @Mock
    private TypedQuery<Usuario> typedQueryMock;

    @BeforeEach
    void setUp() throws Exception {
        // Obtém a instância Singleton
        usuarioRepository = UsuarioJpaRepository.getInstance();

        Field field = UsuarioJpaRepository.class.getDeclaredField("emf");
        field.setAccessible(true);

        field.set(null, emfMock); // Passa null no objeto pois o campo é estático

        // Configura comportamento padrão do Factory
        when(emfMock.createEntityManager()).thenReturn(emMock);
    }

    @Test
    @DisplayName("Deve criar um novo usuário chamando persist")
    void testCreate_NovoUsuario() {
        // Arrange
        // Usamos classe anônima pois Usuario é abstrato
        Usuario usuario = new Usuario() {}; 
        usuario.setEmail("teste@email.com");

        when(emMock.getTransaction()).thenReturn(txMock);

        // Act
        Usuario resultado = usuarioRepository.create(usuario);

        // Assert
        verify(txMock).begin();
        verify(emMock).persist(usuario);
        verify(txMock).commit();
        verify(emMock).close();
        assertEquals(usuario, resultado);
    }

    @Test
    @DisplayName("Deve encontrar um usuário pelo email")
    void testFindByEmail_UsuarioExistente() {
        // Arrange
        String email = "teste@find.com";
        Usuario usuarioEsperado = new Usuario() {};
        usuarioEsperado.setEmail(email);

        when(emMock.find(Usuario.class, email)).thenReturn(usuarioEsperado);

        // Act
        Usuario resultado = usuarioRepository.findByEmail(email);

        // Assert
        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());
        verify(emMock).close();
    }

    @Test
    @DisplayName("Deve retornar null se email não existe")
    void testFindByEmail_NaoEncontrado() {
        // Arrange
        when(emMock.find(Usuario.class, "naoexiste@email.com")).thenReturn(null);

        // Act
        Usuario resultado = usuarioRepository.findByEmail("naoexiste@email.com");

        // Assert
        assertNull(resultado);
        verify(emMock).close();
    }

    @Test
    @DisplayName("Deve atualizar usuário chamando merge")
    void testUpdate() {
        // Arrange
        Usuario usuario = new Usuario() {};
        usuario.setEmail("update@email.com");
        
        when(emMock.getTransaction()).thenReturn(txMock);
        when(emMock.merge(usuario)).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioRepository.update(usuario);

        // Assert
        verify(txMock).begin();
        verify(emMock).merge(usuario);
        verify(txMock).commit();
        verify(emMock).close();
        assertEquals(usuario, resultado);
    }

    @Test
    @DisplayName("Deve deletar usuário se encontrado")
    void testDelete_UsuarioExistente() {
        // Arrange
        String email = "delete@email.com";
        Usuario usuario = new Usuario() {};
        
        when(emMock.find(Usuario.class, email)).thenReturn(usuario);
        when(emMock.getTransaction()).thenReturn(txMock);

        // Act
        boolean deletado = usuarioRepository.delete(email);

        // Assert
        assertTrue(deletado);
        verify(emMock).remove(usuario);
        verify(txMock).commit();
        verify(emMock).close();
    }

    @Test
    @DisplayName("Não deve deletar (e não abrir transação) se usuário não encontrado")
    void testDelete_UsuarioInexistente() {
        // Arrange
        String email = "fantasma@email.com";
        when(emMock.find(Usuario.class, email)).thenReturn(null);

        // Act
        boolean deletado = usuarioRepository.delete(email);

        // Assert
        assertFalse(deletado);
        verify(emMock, never()).remove(any());
        verify(emMock, never()).getTransaction(); // Não deve abrir transação
        verify(emMock).close();
    }

    @Test
    @DisplayName("Deve listar todos os usuários via JPQL")
    void testListarTodos() {
        // Arrange
        List<Usuario> listaEsperada = Collections.singletonList(new Usuario() {});
        
        when(emMock.createQuery(anyString(), eq(Usuario.class))).thenReturn(typedQueryMock);
        when(typedQueryMock.getResultList()).thenReturn(listaEsperada);

        // Act
        List<Usuario> resultado = usuarioRepository.listarTodos();

        // Assert
        assertEquals(1, resultado.size());
        verify(emMock).createQuery("SELECT u FROM Usuario u", Usuario.class);
        verify(emMock).close();
    }
}