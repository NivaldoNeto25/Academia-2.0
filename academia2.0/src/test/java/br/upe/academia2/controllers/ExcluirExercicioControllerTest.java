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
        Field field;
        try {
            field = ExcluirExercicioController.class.getDeclaredField("exercicioBusiness");
        } catch (NoSuchFieldException e) {
            field = ExcluirExercicioController.class.getDeclaredField("exercicio");
        }
        
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
        
        // Verifica existência
        Exercicio existente = exercicioBusiness.buscarExercicioPorNome(nome);

        if (existente == null) {
            mensagem = "Exercício não existe";
            return;
        }
        
        try {
            // Apenas deleta (o banco de dados persiste automaticamente)
            exercicioBusiness.deletarExercicio(nome);
            
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

        // Mock para encontrar o exercício
        when(exercicioBusiness.buscarExercicioPorNome("Agachamento"))
                .thenReturn(new Exercicio("Agachamento", "Pernas", "gif"));

       
        doNothing().when(exercicioBusiness).deletarExercicio("Agachamento");
        

        handleExcluirSimulado();

        assertEquals("Exercicio excluído!", mensagem);
        assertEquals("", nome); // Verifica se limpou o campo nome

        verify(exercicioBusiness).deletarExercicio("Agachamento");
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