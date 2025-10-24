package br.upe.academia2.business;

import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.interfaces.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoBusinessTest {

    // 1. Mocar as dependências
    @Mock
    private IUsuarioRepository mockUsuarioRepository;
    @Mock
    private PlanoTreinoCsvRepository mockPlanoRepository;

    // 2. A Classe que estamos testando
    private PlanoTreinoBusiness planoTreinoBusiness;

    // 3. Mocks para os objetos de dados
    @Mock
    private Usuario mockUsuario;
    @Mock
    private PlanoTreino mockPlano1;
    @Mock
    private PlanoTreino mockPlano2;
    
    // 4. Captor para verificar listas
    @Captor
    private ArgumentCaptor<List<PlanoTreino>> planosListCaptor;

    @BeforeEach
    void setUp() {
        // Injeta os mocks no construtor da classe de negócio
        planoTreinoBusiness = new PlanoTreinoBusiness(mockUsuarioRepository, mockPlanoRepository);
    }

    @Test
    @DisplayName("Deve cadastrar um novo plano com sucesso")
    void testCadastrarPlanoDeTreino_Sucesso() {
        // --- Arrange (Preparação) ---
        // 1. Quando o business pedir a lista de planos, retorne uma lista vazia
        List<PlanoTreino> planosAtuais = new ArrayList<>();
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(planosAtuais);

        // --- Act (Ação) ---
        // 2. Chame o método a ser testado
        planoTreinoBusiness.cadastrarPlanoDeTreino(mockUsuario, mockPlano1);

        // --- Assert (Verificação) ---
        // 3. Verifique se o usuário foi setado no plano
        verify(mockPlano1).setUsuario(mockUsuario);
        // 4. Verifique se o ID foi setado no plano
        verify(mockPlano1).setId(anyInt());
        // 5. Verifique se a lista de planos foi salva no repositório
        verify(mockPlanoRepository).salvarPlanos(planosListCaptor.capture(), eq(mockUsuario));
        // 6. Verifique se o usuário foi atualizado no seu repositório
        verify(mockUsuario).setPlanTreinos(anyList());
        verify(mockUsuarioRepository).update(mockUsuario);
        
        // 7. Verifique se o plano foi realmente adicionado à lista que foi salva
        List<PlanoTreino> listaSalva = planosListCaptor.getValue();
        assertEquals(1, listaSalva.size());
        assertSame(mockPlano1, listaSalva.get(0));
    }
    
    @Test
    @DisplayName("Não deve cadastrar se o usuário for nulo")
    void testCadastrarPlanoDeTreino_UsuarioNulo() {
        // --- Act ---
        planoTreinoBusiness.cadastrarPlanoDeTreino(null, mockPlano1);

        // --- Assert ---
        // Nenhum método dos repositórios deve ser chamado
        verifyNoInteractions(mockPlanoRepository);
        verifyNoInteractions(mockUsuarioRepository);
    }

    @Test
    @DisplayName("Deve carregar o primeiro plano do usuário")
    void testCarregarPlanoDoUsuario_EncontraPlano() {
        // --- Arrange ---
        List<PlanoTreino> listaDePlanos = List.of(mockPlano1, mockPlano2);
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(listaDePlanos);

        // --- Act ---
        PlanoTreino resultado = planoTreinoBusiness.carregarPlanoDoUsuario(mockUsuario);

        // --- Assert ---
        // Deve retornar o PRIMEIRO plano da lista
        assertNotNull(resultado);
        assertSame(mockPlano1, resultado);
    }

    @Test
    @DisplayName("Deve retornar nulo se a lista de planos estiver vazia")
    void testCarregarPlanoDoUsuario_ListaVazia() {
        // --- Arrange ---
        List<PlanoTreino> listaVazia = new ArrayList<>();
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(listaVazia);

        // --- Act ---
        PlanoTreino resultado = planoTreinoBusiness.carregarPlanoDoUsuario(mockUsuario);

        // --- Assert ---
        assertNull(resultado);
    }
    
    @Test
    @DisplayName("Deve retornar nulo se o usuário for nulo")
    void testCarregarPlanoDoUsuario_UsuarioNulo() {
        // --- Act ---
        PlanoTreino resultado = planoTreinoBusiness.carregarPlanoDoUsuario(null);

        // --- Assert ---
        assertNull(resultado);
        verifyNoInteractions(mockPlanoRepository);
    }

    @Test
    @DisplayName("Deve modificar um plano existente")
    void testModificarPlanoDeTreino_PlanoEncontrado() {
        // --- Arrange ---
        // Para testar a lógica de IDs, precisamos mockar o getId()
        when(mockPlano1.getId()).thenReturn(1);
        when(mockPlano1.getUsuario()).thenReturn(mockUsuario);

        // Criamos uma lista MUTÁVEL
        List<PlanoTreino> planosAtuais = new ArrayList<>(List.of(mockPlano1));
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(planosAtuais);

        // Este é o plano "novo" que substituirá o plano antigo
        // Vamos usar mockPlano2 como se fosse a versão modificada do mockPlano1
        // (ex: mockPlano1 com ID 1, mockPlano2 com ID 1 mas nome diferente)
        // Para simplificar: vamos "modificar" o plano 1 para ser o plano 1
        when(mockPlano1.getUsuario()).thenReturn(mockUsuario);

        // --- Act ---
        planoTreinoBusiness.modificarPlanoDeTreino(mockPlano1);

        // --- Assert ---
        // Deve ter salvo a lista
        verify(mockPlanoRepository).salvarPlanos(planosListCaptor.capture(), eq(mockUsuario));
        
        // A lista salva deve conter o plano modificado
        List<PlanoTreino> listaSalva = planosListCaptor.getValue();
        assertEquals(1, listaSalva.size());
        assertSame(mockPlano1, listaSalva.get(0));
    }
    
    @Test
    @DisplayName("Não deve salvar se o plano a modificar não for encontrado")
    void testModificarPlanoDeTreino_PlanoNaoEncontrado() {
        // --- Arrange ---
        when(mockPlano1.getId()).thenReturn(1);
        when(mockPlano2.getId()).thenReturn(99); // ID diferente
        when(mockPlano2.getUsuario()).thenReturn(mockUsuario);

        List<PlanoTreino> planosAtuais = new ArrayList<>(List.of(mockPlano1));
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(planosAtuais);

        // --- Act ---
        // Tentamos modificar com o mockPlano2 (ID 99), que não está na lista
        planoTreinoBusiness.modificarPlanoDeTreino(mockPlano2);

        // --- Assert ---
        // Não deve salvar nada, pois o plano não foi encontrado
        verify(mockPlanoRepository, never()).salvarPlanos(any(), any());
    }

    @Test
    @DisplayName("Deve listar planos e setar o usuário em cada um")
    void testListarPlanosPorUsuario_Sucesso() {
        // --- Arrange ---
        List<PlanoTreino> listaDoRepositorio = List.of(mockPlano1, mockPlano2);
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(listaDoRepositorio);

        // --- Act ---
        List<PlanoTreino> resultado = planoTreinoBusiness.listarPlanosPorUsuario(mockUsuario);

        // --- Assert ---
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        
        // Deve ter setado o usuário em cada plano
        verify(mockPlano1).setUsuario(mockUsuario);
        verify(mockPlano2).setUsuario(mockUsuario);
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia se repositório retornar nulo")
    void testListarPlanosPorUsuario_RepositorioRetornaNulo() {
        // --- Arrange ---
        when(mockPlanoRepository.listarPlanosPorUsuario(mockUsuario)).thenReturn(null);

        // --- Act ---
        List<PlanoTreino> resultado = planoTreinoBusiness.listarPlanosPorUsuario(mockUsuario);

        // --- Assert ---
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}