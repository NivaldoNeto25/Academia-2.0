package br.upe.academia2.business;

import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecaoTreinoBusinessTest {

    private SecaoTreinoBusiness secaoTreinoBusiness;

    // Este método é executado antes de cada teste.
    @BeforeEach
    void setUp() {
        secaoTreinoBusiness = new SecaoTreinoBusiness();
    }

    @Test
    @DisplayName("Deve iniciar a sessão de treino sem lançar exceções")
    void testIniciarSessao_ComPlanoValido_NaoDeveLancarExcecao() {
        // Arrange (Organização)
        PlanoTreino plano = new PlanoTreino(0, "Treino A", null, null, null);

        // Act & Assert (Ação e Verificação)
        // O teste verifica se a chamada do método ocorre sem erros.
        // Testar a saída do logger geralmente não é feito em testes unitários.
        assertDoesNotThrow(() -> secaoTreinoBusiness.iniciarSessao(plano));
    }

    @Test
    @DisplayName("Deve registrar a performance atualizando os dados do item de treino")
    void testRegistrarPerformance_DeveAtualizarValoresDoItem() {
        // Arrange
        // Cria os objetos necessários para o teste
        Exercicio exercicio = new Exercicio("Supino Reto", "Peitoral", "Barra");
        ItemPlanoTreino itemOriginal = new ItemPlanoTreino(exercicio, 3, 10, 50); // series, reps, carga

        // Define os novos valores de performance
        int cargaRealizada = 55;
        int repeticoesRealizadas = 8;
        int seriesRealizadas = 3;

        // Verifica os valores iniciais (opcional, mas bom para garantir)
        assertEquals(50, itemOriginal.getCarga());
        assertEquals(10, itemOriginal.getRepeticoes());
        assertEquals(3, itemOriginal.getSeries());

        // Act
        secaoTreinoBusiness.registrarPerformance(itemOriginal, cargaRealizada, repeticoesRealizadas, seriesRealizadas);

        // Assert
        // Verifica se os valores do objeto 'itemOriginal' foram alterados para os novos valores
        assertEquals(cargaRealizada, itemOriginal.getCarga(), "A carga deveria ter sido atualizada.");
        assertEquals(repeticoesRealizadas, itemOriginal.getRepeticoes(), "As repetições deveriam ter sido atualizadas.");
        assertEquals(seriesRealizadas, itemOriginal.getSeries(), "As séries deveriam ter sido atualizadas.");
    }
}