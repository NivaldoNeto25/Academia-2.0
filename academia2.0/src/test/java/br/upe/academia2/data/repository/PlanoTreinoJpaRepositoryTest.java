package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoJpaRepositoryTest {

    private PlanoTreinoJpaRepository repository;

    @Mock
    private EntityManagerFactory emfMock;
    @Mock
    private EntityManager emMock;
    @Mock
    private EntityTransaction txMock;
    @Mock
    private TypedQuery<PlanoTreino> typedQueryMock;
    @Mock
    private Query queryMock;

    @BeforeEach
    void setUp() throws Exception {
        // Instancia o repositório
        repository = new PlanoTreinoJpaRepository();

        // --- REFLEXÃO PARA INJEÇÃO DE DEPENDÊNCIA ---
        // A classe original cria o EntityManagerFactory internamente (new Persistence...).
        // Precisamos substituir esse campo privado pelo nosso Mock para controlar o teste.
        Field emfField = PlanoTreinoJpaRepository.class.getDeclaredField("emf");
        emfField.setAccessible(true);
        emfField.set(repository, emfMock);

        // Configura o comportamento padrão: sempre que pedir um EntityManager, devolve o nosso mock
        when(emfMock.createEntityManager()).thenReturn(emMock);
    }

    @Test
    void testSalvarPlanos_DeveDeletarAntigosESalvarNovos() {
        // Arrange
        Usuario usuario = new Comum();
        usuario.setEmail("teste@email.com");
        
        PlanoTreino plano1 = new PlanoTreino();
        List<PlanoTreino> planos = List.of(plano1);

        // Mock da Transação
        when(emMock.getTransaction()).thenReturn(txMock);

        // Mock da Query de Deleção (DELETE FROM ...)
        when(emMock.createQuery(contains("DELETE"))).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);

        // Act
        repository.salvarPlanos(planos, usuario);

        // Assert
        // 1. Verifica se iniciou transação
        verify(txMock).begin();
        
        // 2. Verifica se tentou deletar os planos antigos desse usuário
        verify(emMock).createQuery(contains("DELETE"));
        verify(queryMock).setParameter("email", "teste@email.com");
        verify(queryMock).executeUpdate();

        // 3. Verifica se persistiu o novo plano
        verify(emMock).persist(plano1);

        // 4. Verifica se commitou e fechou
        verify(txMock).commit();
        verify(emMock).close();
    }

    @Test
    void testCarregarPlanos_DeveRetornarListaDoBanco() {
        // Arrange
        Usuario usuario = new Comum();
        usuario.setEmail("teste@email.com");
        PlanoTreino planoRetornado = new PlanoTreino();
        List<PlanoTreino> listaEsperada = List.of(planoRetornado);

        // Mock da Query de Seleção (SELECT ...)
        when(emMock.createQuery(contains("SELECT"), eq(PlanoTreino.class))).thenReturn(typedQueryMock);
        when(typedQueryMock.setParameter(anyString(), any())).thenReturn(typedQueryMock);
        when(typedQueryMock.getResultList()).thenReturn(listaEsperada);

        // Act
        List<PlanoTreino> resultado = repository.carregarPlanos(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(typedQueryMock).setParameter("email", "teste@email.com");
        verify(emMock).close();
    }

    @Test
    void testSalvarOuAtualizar_DevePersistirSeIdZero() {
        // Arrange
        PlanoTreino novoPlano = new PlanoTreino();
        novoPlano.setId(0); // ID 0 indica novo cadastro

        when(emMock.getTransaction()).thenReturn(txMock);

        // Act
        repository.salvarOuAtualizarPlano(novoPlano);

        // Assert
        verify(emMock).persist(novoPlano); // Deve chamar persist
        verify(emMock, never()).merge(any()); // Não deve chamar merge
        verify(txMock).commit();
    }

    @Test
    void testSalvarOuAtualizar_DeveMergeSeIdMaiorQueZero() {
        // Arrange
        PlanoTreino planoExistente = new PlanoTreino();
        planoExistente.setId(10); // ID existe

        when(emMock.getTransaction()).thenReturn(txMock);

        // Act
        repository.salvarOuAtualizarPlano(planoExistente);

        // Assert
        verify(emMock).merge(planoExistente); // Deve chamar merge
        verify(emMock, never()).persist(any()); // Não deve chamar persist
        verify(txMock).commit();
    }

    @Test
    void testDeletarPlano_ComSucesso() {
        // Arrange
        int idParaDeletar = 5;
        PlanoTreino planoEncontrado = new PlanoTreino();
        
        when(emMock.getTransaction()).thenReturn(txMock);
        when(emMock.find(PlanoTreino.class, idParaDeletar)).thenReturn(planoEncontrado);

        // Act
        boolean removido = repository.deletarPlano(idParaDeletar);

        // Assert
        assertTrue(removido);
        verify(emMock).remove(planoEncontrado);
        verify(txMock).commit();
    }

    @Test
    void testDeletarPlano_NaoEncontrado() {
        // Arrange
        int idParaDeletar = 99;
        
        when(emMock.getTransaction()).thenReturn(txMock);
        when(emMock.find(PlanoTreino.class, idParaDeletar)).thenReturn(null); // Não achou

        // Act
        boolean removido = repository.deletarPlano(idParaDeletar);

        // Assert
        assertFalse(removido);
        verify(emMock, never()).remove(any()); // Não deve tentar remover nada
        verify(txMock).commit();
    }
}