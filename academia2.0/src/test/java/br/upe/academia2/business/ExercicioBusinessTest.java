package br.upe.academia2.business;

import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.repository.ExercicioRepoImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExercicioBusinessTest {

    // Cria um "mock" (objeto falso) do repositório.
    // Nós vamos controlar o comportamento deste objeto em cada teste.
    @Mock
    private ExercicioRepoImpl exercicioRepository;

    // Injeta os mocks (neste caso, exercicioRepository) na classe que queremos testar.
    @InjectMocks
    private ExercicioBusiness exercicioBusiness;


    @Test
    @DisplayName("Deve salvar um exercício com sucesso quando ele é válido e não existe")
    void testSalvar_ComExercicioValido_DeveChamarCreate() {
        // Arrange (Organização)
        Exercicio novoExercicio = new Exercicio("Supino Reto", "Peitoral", "Halteres");
        // Quando o método findByNome for chamado com "Supino Reto", deve retornar null (finge que não existe)
        when(exercicioRepository.findByNome("Supino Reto")).thenReturn(null);
        // Quando o método create for chamado, retorna o próprio exercício (finge que salvou com sucesso)
        when(exercicioRepository.create(novoExercicio)).thenReturn(novoExercicio);

        // Act (Ação)
        exercicioBusiness.salvar(novoExercicio);

        // Assert (Verificação)
        // Verifica se o método persistirNoCsv foi chamado 1 vez.
        verify(exercicioRepository, times(1)).persistirNoCsv();
        // Verifica se o método findByNome foi chamado 1 vez.
        verify(exercicioRepository, times(1)).findByNome("Supino Reto");
        // Verifica se o método create foi chamado 1 vez com o objeto correto.
        verify(exercicioRepository, times(1)).create(novoExercicio);
    }

    @Test
    @DisplayName("Não deve salvar um exercício que já existe")
    void testSalvar_ComExercicioExistente_NaoDeveChamarCreate() {
        // Arrange
        Exercicio exercicioExistente = new Exercicio("Agachamento", "Pernas", "Barra");
        when(exercicioRepository.findByNome("Agachamento")).thenReturn(exercicioExistente);

        // Act
        exercicioBusiness.salvar(exercicioExistente);

        // Assert
        // Verifica se o método create NUNCA foi chamado
        verify(exercicioRepository, never()).create(any(Exercicio.class));
    }

    @Test
    @DisplayName("Não deve salvar um exercício com nome nulo ou vazio")
    void testSalvar_ComNomeVazio_NaoDeveChamarCreate() {
        // Arrange
        Exercicio exercicioInvalido = new Exercicio(" ", "Pernas", "Barra");

        // Act
        exercicioBusiness.salvar(exercicioInvalido);

        // Assert
        verify(exercicioRepository, never()).findByNome(anyString());
        verify(exercicioRepository, never()).create(any(Exercicio.class));
    }

    @Test
    @DisplayName("Deve listar todos os exercícios retornados pelo repositório")
    void testListarExercicios_DeveRetornarListaDoRepositorio() {
        // Arrange
        Exercicio e1 = new Exercicio("Remada Curvada", "Costas", "Barra");
        Exercicio e2 = new Exercicio("Desenvolvimento", "Ombros", "Halteres");
        List<Exercicio> listaEsperada = Arrays.asList(e1, e2);
        when(exercicioRepository.findAll()).thenReturn(listaEsperada);

        // Act
        List<Exercicio> resultado = exercicioBusiness.listarExercicios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Remada Curvada", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar e retornar um exercício pelo nome quando ele existe")
    void testBuscarExercicioPorNome_QuandoEncontrado_DeveRetornarExercicio() {
        // Arrange
        Exercicio exercicio = new Exercicio("Leg Press", "Pernas", "Máquina");
        when(exercicioRepository.findByNome("Leg Press")).thenReturn(exercicio);

        // Act
        Exercicio resultado = exercicioBusiness.buscarExercicioPorNome("Leg Press");

        // Assert
        verify(exercicioRepository, times(1)).carregarDoCsv();
        assertNotNull(resultado);
        assertEquals("Leg Press", resultado.getNome());
    }

    @Test
    @DisplayName("Deve retornar nulo ao buscar um exercício que não existe")
    void testBuscarExercicioPorNome_QuandoNaoEncontrado_DeveRetornarNulo() {
        // Arrange
        when(exercicioRepository.findByNome("Inexistente")).thenReturn(null);

        // Act
        Exercicio resultado = exercicioBusiness.buscarExercicioPorNome("Inexistente");

        // Assert
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve chamar o método de update do repositório ao atualizar um exercício")
    void testAtualizarExercicio_ComExercicioValido_DeveChamarUpdate() {
        // Arrange
        Exercicio exercicio = new Exercicio("Rosca Direta", "Bíceps", "Halteres");

        // Act
        exercicioBusiness.atualizarExercicio(exercicio);

        // Assert
        verify(exercicioRepository, times(1)).carregarDoCsv();
        // Verifica que o método update foi chamado com o objeto correto
        verify(exercicioRepository, times(1)).update(exercicio);
    }

    @Test
    @DisplayName("Não deve chamar o método update ao tentar atualizar um exercício nulo")
    void testAtualizarExercicio_ComExercicioNulo_NaoDeveChamarUpdate() {
        // Act
        exercicioBusiness.atualizarExercicio(null);

        // Assert
        verify(exercicioRepository, never()).update(any(Exercicio.class));
    }

    @Test
    @DisplayName("Deve chamar o método de delete do repositório ao deletar um exercício")
    void testDeletarExercicio_ComNomeValido_DeveChamarDelete() {
        // Arrange
        String nomeParaDeletar = "Abdominal";
        // O método 'delete' no repositório provavelmente é void, então não precisa de 'when'.
        // O Mockito vai aceitar a chamada por padrão.

        // Act
        exercicioBusiness.deletarExercicio(nomeParaDeletar);

        // Assert
        verify(exercicioRepository, times(1)).delete(nomeParaDeletar);
    }

    @Test
    @DisplayName("Não deve chamar o método delete ao tentar deletar com nome vazio")
    void testDeletarExercicio_ComNomeVazio_NaoDeveChamarDelete() {
        // Act
        exercicioBusiness.deletarExercicio("");

        // Assert
        verify(exercicioRepository, never()).delete(anyString());
    }
}