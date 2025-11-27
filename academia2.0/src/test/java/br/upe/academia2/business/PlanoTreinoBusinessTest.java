package br.upe.academia2.business;

import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoJpaRepository;
import br.upe.academia2.data.repository.interfaces.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoBusinessTest {

    @Mock
    private IUsuarioRepository mockUsuarioRepository;
    @Mock
    private PlanoTreinoJpaRepository mockPlanoRepository;

    private PlanoTreinoBusiness planoTreinoBusiness;

    @Mock
    private Usuario mockUsuario;
    @Mock
    private PlanoTreino mockPlano1;
    @Mock
    private PlanoTreino mockPlano2;

    @BeforeEach
    void setUp() {
        planoTreinoBusiness = new PlanoTreinoBusiness(mockUsuarioRepository, mockPlanoRepository);
    }

    @Test
    @DisplayName("Deve modificar um plano existente")
    void testModificarPlanoDeTreino_PlanoEncontrado() {
        // Arrange
        lenient().when(mockPlano1.getId()).thenReturn(1); 
        lenient().when(mockPlano1.getNomePlano()).thenReturn("Treino A");
        lenient().when(mockPlano1.getUsuario()).thenReturn(mockUsuario);

        // Act
        planoTreinoBusiness.modificarPlanoDeTreino(mockPlano1);

        // Assert
        verify(mockPlanoRepository).salvarOuAtualizarPlano(mockPlano1);
    }
    
    
    @Test
    @DisplayName("Deve cadastrar um novo plano com sucesso")
    void testCadastrarPlanoDeTreino_Sucesso() {
        // Arrange
        lenient().when(mockPlano1.getId()).thenReturn(0); 
        lenient().when(mockPlano1.getNomePlano()).thenReturn("Novo Treino");

        // Act
        planoTreinoBusiness.cadastrarPlanoDeTreino(mockUsuario, mockPlano1);

        // Assert
        verify(mockPlano1).setUsuario(mockUsuario);
        verify(mockPlanoRepository).salvarOuAtualizarPlano(mockPlano1);
    }

    @Test
    @DisplayName("Deve listar planos do usuário")
    void testListarPlanosPorUsuario_Sucesso() {
        List<PlanoTreino> listaDoRepositorio = List.of(mockPlano1, mockPlano2);
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(listaDoRepositorio);

        List<PlanoTreino> resultado = planoTreinoBusiness.listarPlanosPorUsuario(mockUsuario);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve retornar nulo se a lista de planos estiver vazia")
    void testCarregarPlanoDoUsuario_ListaVazia() {
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(Collections.emptyList());
        PlanoTreino resultado = planoTreinoBusiness.carregarPlanoDoUsuario(mockUsuario);
        assertNull(resultado);
    }
}