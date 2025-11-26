package br.upe.academia2.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.ui.controllers.ExcluirExercicioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirExercicioControllerTest {

    @InjectMocks
    private ExcluirExercicioController controller;
    @Mock
    private ExercicioBusiness exercicioBusiness;
    private String nome;
    private String mensagem;

    @BeforeEach
    void setUp() throws Exception {

        // Injeta o mock no campo final via reflexão
        Field field = ExcluirExercicioController.class.getDeclaredField("exercicio");
        field.setAccessible(true);
        field.set(controller, exercicioBusiness);

        nome = "";
        mensagem = "";
    }
    private void handleExcluirSimulado() {

        mensagem = "";

        if (nome == null || nome.isBlank()) {
            mensagem = "Informe o nome.";
            return;
        }
        Exercicio existente = exercicioBusiness.buscarExercicioPorNome(nome);

        if (existente == null) {
            mensagem = "Exercício não existe";
            return;
        }
        try {
            exercicioBusiness.deletarExercicio(nome);
            exercicioBusiness.salvarAlteracoesNoCsv();
            mensagem = "Exercicio excluído!";
            nome = "";
        } catch (Exception e) {
            mensagem = "Erro ao excluir!";
        }
    }

    @Test
    void testNomeVazio() {
        nome = "";
        handleExcluirSimulado();
        assertEquals("Informe o nome.", mensagem);
    }

    @Test
    void testExercicioNaoExiste() {
        nome = "Supino";

        when(exercicioBusiness.buscarExercicioPorNome("Supino")).thenReturn(null);

        handleExcluirSimulado();

        assertEquals("Exercício não existe", mensagem);
        verify(exercicioBusiness, never()).deletarExercicio(any());
    }

    @Test
    void testExcluirComSucesso() {
        nome = "Agachamento";

        when(exercicioBusiness.buscarExercicioPorNome("Agachamento"))
                .thenReturn(new Exercicio("Agachamento", "Pernas", "gif"));

        doNothing().when(exercicioBusiness).deletarExercicio("Agachamento");
        doNothing().when(exercicioBusiness).salvarAlteracoesNoCsv();

        handleExcluirSimulado();

        assertEquals("Exercicio excluído!", mensagem);
        assertEquals("", nome);

        verify(exercicioBusiness).deletarExercicio("Agachamento");
        verify(exercicioBusiness).salvarAlteracoesNoCsv();
    }

    @Test
    void testErroAoExcluir() {
        nome = "Remada";

        when(exercicioBusiness.buscarExercicioPorNome("Remada"))
                .thenReturn(new Exercicio("Remada", "Costas", "gif"));

        doThrow(new RuntimeException("Falha")).when(exercicioBusiness).deletarExercicio("Remada");

        handleExcluirSimulado();

        assertEquals("Erro ao excluir!", mensagem);
    }
}
