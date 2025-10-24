package br.upe.academia2.data.repository;

import br.upe.academia2.business.SecaoTreinoBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecaoTreinoBusinessTest {

    private SecaoTreinoBusiness secaoTreinoBusiness;

    // O método com @BeforeEach é executado antes de cada teste,
    // garantindo uma instância limpa da classe para cada cenário.
    @BeforeEach
    void setUp() {
        secaoTreinoBusiness = new SecaoTreinoBusiness();
    }

    @Test
    @DisplayName("Deve iniciar a sessão de treino sem lançar exceções para um plano válido")
    void testIniciarSessao_ComPlanoValido_NaoDeveLancarExcecao() {
        // Arrange (Organização)
        PlanoTreino plano = new PlanoTreino(0, "Treino de Peito", null, null, null);

        // Act & Assert (Ação e Verificação)
        // Como o método apenas loga uma mensagem, o teste mais útil é
        // garantir que ele executa sem erros ou exceções inesperadas.
        assertDoesNotThrow(() -> secaoTreinoBusiness.iniciarSessao(plano));
    }

    @Test
    @DisplayName("Deve registrar a performance atualizando os dados do item de treino")
    void testRegistrarPerformance_ComNovosValores_DeveAtualizarItemOriginal() {
        // Arrange
        // Cria os objetos necessários para o teste
        Exercicio exercicio = new Exercicio("Supino Reto", "Peitoral", "barra.gif");
        ItemPlanoTreino itemOriginal = new ItemPlanoTreino(exercicio, 3, 10, 80); // 3 séries, 10 repetições, 80kg de carga

        // Define os novos valores de performance que serão registrados
        int cargaRealizada = 85;
        int repeticoesRealizadas = 8;
        int seriesRealizadas = 3;

        // Act (Ação)
        // Chama o método que queremos testar
        secaoTreinoBusiness.registrarPerformance(itemOriginal, cargaRealizada, repeticoesRealizadas, seriesRealizadas);

        // Assert (Verificação)
        // Verifica se os valores do objeto 'itemOriginal' foram realmente alterados
        assertEquals(cargaRealizada, itemOriginal.getCarga(), "A carga do exercício deveria ter sido atualizada.");
        assertEquals(repeticoesRealizadas, itemOriginal.getRepeticoes(), "As repetições deveriam ter sido atualizadas.");
        assertEquals(seriesRealizadas, itemOriginal.getSeries(), "As séries deveriam ter sido atualizadas.");
    }
}