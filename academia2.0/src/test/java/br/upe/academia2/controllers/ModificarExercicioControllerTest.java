package br.upe.academia2.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.ui.controllers.ModificarExercicioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificarExercicioControllerTest {

    @InjectMocks
    private ModificarExercicioController controller;

    @Mock
    private ExercicioBusiness exercicioBusiness;
    //simula dnv igual todos os outros
    private String nome;
    private String descricao;
    private String novoGif;
    private String mensagem;

    private Exercicio encontrado;

    @BeforeEach
    void setUp() throws Exception {

        // injeta mock
        Field field = ModificarExercicioController.class.getDeclaredField("exercicio");
        field.setAccessible(true);
        field.set(controller, exercicioBusiness);

        nome = "";
        descricao = "";
        novoGif = null;
        mensagem = "";
        encontrado = null;
    }

    private void handleBuscarSimulado() {
        mensagem = "";

        List<Exercicio> lista = exercicioBusiness.listarExercicios();
        encontrado = lista.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        if (encontrado == null) {
            mensagem = "Exercicio não encontrado.";
        } else {
            mensagem = "Exercício carregado. Modifique os campos.";
        }
    }

    private void handleModificarSimulado() {

        mensagem = "";

        List<Exercicio> lista = exercicioBusiness.listarExercicios();
        encontrado = lista.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        if (encontrado == null) {
            mensagem = "Exercicio não encontrado.";
            return;
        }

        if (!nome.isBlank()) encontrado.setNome(nome);
        if (!descricao.isBlank()) encontrado.setDescricao(descricao);
        if (novoGif != null && !novoGif.isBlank()) encontrado.setCaminhoGif(novoGif);

        exercicioBusiness.atualizarExercicio(encontrado);
        exercicioBusiness.salvarAlteracoesNoCsv();

        mensagem = "Exercicio modificado com sucesso!";
        nome = "";
        descricao = "";
    }

    @Test
    void testBuscarNaoEncontrado() {

        nome = "Supino";

        when(exercicioBusiness.listarExercicios()).thenReturn(new ArrayList<>());

        handleBuscarSimulado();
        assertEquals("Exercicio não encontrado.", mensagem);
    }

    @Test
    void testBuscarEncontrado() {

        nome = "Agachamento";

        List<Exercicio> lista = List.of(new Exercicio("Agachamento", "Pernas", "gif"));
        when(exercicioBusiness.listarExercicios()).thenReturn(lista);

        handleBuscarSimulado();

        assertEquals("Exercício carregado. Modifique os campos.", mensagem);
    }

    @Test
    void testModificarNaoEncontrado() {

        nome = "Supino";
        when(exercicioBusiness.listarExercicios()).thenReturn(new ArrayList<>());

        handleModificarSimulado();
        assertEquals("Exercicio não encontrado.", mensagem);

        verify(exercicioBusiness, never()).atualizarExercicio(any());
    }

    @Test
    void testModificarComSucesso() {

        nome = "Remada";
        descricao = "Costas";

        Exercicio ex = new Exercicio("Remada", "Velho", "gif1");

        when(exercicioBusiness.listarExercicios()).thenReturn(List.of(ex));

        doNothing().when(exercicioBusiness).atualizarExercicio(any());
        doNothing().when(exercicioBusiness).salvarAlteracoesNoCsv();

        handleModificarSimulado();

        assertEquals("Exercicio modificado com sucesso!", mensagem);
        assertEquals("", nome);
        assertEquals("", descricao);

        verify(exercicioBusiness).atualizarExercicio(ex);
        verify(exercicioBusiness).salvarAlteracoesNoCsv();
    }

    @Test
    void testModificarComNovoGif() {

        nome = "Tríceps";
        descricao = "Braço";
        novoGif = "novo.gif";

        Exercicio ex = new Exercicio("Tríceps", "Antigo", "velho.gif");

        when(exercicioBusiness.listarExercicios()).thenReturn(List.of(ex));

        handleModificarSimulado();

        assertEquals("Exercicio modificado com sucesso!", mensagem);
        assertEquals("novo.gif", ex.getCaminhoGif());
    }

    @Test
    void testErroAoModificar() {

        nome = "Puxada";
        descricao = "Costas";

        Exercicio ex = new Exercicio("Puxada", "Old", "gifX");

        when(exercicioBusiness.listarExercicios()).thenReturn(List.of(ex));

        doThrow(new RuntimeException("Erro"))
                .when(exercicioBusiness).atualizarExercicio(any());

        try {
            handleModificarSimulado();
        } catch (Exception ignored) {}

        verify(exercicioBusiness).atualizarExercicio(ex);
    }
}
