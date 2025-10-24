// Em SecaoTreinoBusinessTest.java

package br.upe.academia2.business;

import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Habilita mocks do Mockito
class SecaoTreinoBusinessTest {

    // 1. Mocks para as dependências e dados
    @Mock
    private PlanoTreinoBusiness mockPlanoTreinoBusiness; // Dependência mockada
    @Mock
    private PlanoTreino mockPlano;                     // Objeto de dados mockado
    @Mock
    private ItemPlanoTreino mockItem;                  // Objeto de dados mockado
    @Mock
    private Exercicio mockExercicio;

    // 2. A classe sob teste
    private SecaoTreinoBusiness secaoTreinoBusiness;

    @BeforeEach
    void setUp() {
        // CORREÇÃO AQUI: Usa o novo construtor para injetar o mock
        secaoTreinoBusiness = new SecaoTreinoBusiness(mockPlanoTreinoBusiness);
    }

    @Test
    @DisplayName("Deve atualizar o item e chamar a modificação do plano")
    void testRegistrarPerformance_DeveAtualizarItemESalvarPlano() {
        // --- Arrange (Preparação) ---
        int novaCarga = 100;
        int novasRepeticoes = 8;
        int novasSeries = 4;

        when(mockItem.getExercicio()).thenReturn(mockExercicio);
        when(mockExercicio.getNome()).thenReturn("Exercício Mockado");
        // --- Act (Ação) ---
        // Chama o método que queremos testar

        secaoTreinoBusiness.registrarPerformance(mockPlano, mockItem, novaCarga, novasRepeticoes, novasSeries);

        // --- Assert (Verificação) ---
        // 1. Verifica se os setters do ItemPlanoTreino foram chamados com os valores corretos
        verify(mockItem).setCarga(novaCarga);
        verify(mockItem).setRepeticoes(novasRepeticoes);
        verify(mockItem).setSeries(novasSeries);
        verify(mockPlanoTreinoBusiness).modificarPlanoDeTreino(mockPlano);
    }
}