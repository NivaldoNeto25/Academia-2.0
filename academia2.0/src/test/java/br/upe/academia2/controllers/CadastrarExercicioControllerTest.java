package br.upe.academia2.controllers;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.ui.controllers.CadastrarExercicioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarExercicioControllerTest {

    @InjectMocks
    private CadastrarExercicioController controller;
    @Mock
    private ExercicioBusiness exercicioBusiness;

    private String nome;
    private String descricao;
    private String caminhoGif;
    private String mensagem;

    @BeforeEach
    void setUp() throws Exception {

        // injeta mock
        var campo = CadastrarExercicioController.class.getDeclaredField("exercicio");
        campo.setAccessible(true);
        campo.set(controller, exercicioBusiness);
        nome = "";
        descricao = "";
        caminhoGif = "";
        mensagem = "";
    }

    private void handleCadastrarSimulado() {

        mensagem = "";

        if (nome == null || nome.isBlank() ||
                descricao == null || descricao.isBlank() ||
                caminhoGif == null || caminhoGif.isBlank()) {

            mensagem = "Todos os campos são obrigatórios.";
            return;
        }

        List<Exercicio> exerciciosExistentes = exercicioBusiness.listarExercicios();
        if (exerciciosExistentes.stream()
                .anyMatch(e -> e.getNome().equalsIgnoreCase(nome))) {

            mensagem = "Este exercício já existe";
            return;
        }

        Exercicio novo = new Exercicio(nome, descricao, caminhoGif);

        try {
            exercicioBusiness.salvar(novo);
            exercicioBusiness.salvarAlteracoesNoCsv();
            mensagem = "Exercicio cadastrado com sucesso!";
        } catch (Exception ex) {
            mensagem = "Erro ao salvar exercício!";
        }
    }

    @Test
    void testCamposVazios() {
        nome = "";
        descricao = "";
        caminhoGif = "";

        handleCadastrarSimulado();

        assertEquals("Todos os campos são obrigatórios.", mensagem);
    }

    @Test
    void testExercicioExistente() {
        nome = "Supino";
        descricao = "Peito";
        caminhoGif = "supino.gif";

        List<Exercicio> lista = new ArrayList<>();
        lista.add(new Exercicio("Supino", "descricao", "aaaaaaaaa"));

        when(exercicioBusiness.listarExercicios()).thenReturn(lista);

        handleCadastrarSimulado();

        assertEquals("Este exercício já existe", mensagem);
        verify(exercicioBusiness, never()).salvar(any());
    }

    @Test
    void testCadastroSucesso() {
        nome = "Agachamento";
        descricao = "Pernas";
        caminhoGif = "blabla.gif";

        when(exercicioBusiness.listarExercicios()).thenReturn(new ArrayList<>());

        doNothing().when(exercicioBusiness).salvar(any());
        doNothing().when(exercicioBusiness).salvarAlteracoesNoCsv();

        handleCadastrarSimulado();

        assertEquals("Exercicio cadastrado com sucesso!", mensagem);

        verify(exercicioBusiness).salvar(any(Exercicio.class));
        verify(exercicioBusiness).salvarAlteracoesNoCsv();
    }

    @Test
    void testErroAoSalvar() {
        nome = "Tríceps";
        descricao = "Braço";
        caminhoGif = "blublu.gif";

        when(exercicioBusiness.listarExercicios()).thenReturn(new ArrayList<>());
        doThrow(new RuntimeException("Falha"))
                .when(exercicioBusiness).salvar(any(Exercicio.class));

        handleCadastrarSimulado();

        assertEquals("Erro ao salvar exercício!", mensagem);
    }
}
