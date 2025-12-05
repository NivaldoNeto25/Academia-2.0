package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Exercicio;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExercicioJpaRepositoryTest {

    private ExercicioJpaRepository exercicioRepository;
    private EntityManagerFactory emfTest;

    @BeforeEach
    void setUp() throws Exception {

        emfTest = Persistence.createEntityManagerFactory("academiaTestPU");

        exercicioRepository = new ExercicioJpaRepository();

        Field field = ExercicioJpaRepository.class.getDeclaredField("emf");
        field.setAccessible(true);
        field.set(exercicioRepository, emfTest);
    }

    @AfterEach
    void tearDown() {
        if (emfTest != null && emfTest.isOpen()) {
            emfTest.close();
        }
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve salvar um exercício no banco H2 e recuperar corretamente")
    void testCreate_NovoExercicio() {

        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Supino Reto");
        exercicio.setDescricao("Exercício para peitoral maior");

        Exercicio salvo = exercicioRepository.create(exercicio);

        assertNotNull(salvo);

        Exercicio encontrado = exercicioRepository.findByNome("Supino Reto");
        assertNotNull(encontrado);
        assertEquals("Exercício para peitoral maior", encontrado.getDescricao());
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve retornar null ao buscar exercício inexistente")
    void testFindByNome_NaoEncontrado() {
        Exercicio resultado = exercicioRepository.findByNome("Inexistente");
        assertNull(resultado);
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve atualizar os dados de um exercício salvo")
    void testUpdate() {

        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Agachamento");
        exercicio.setDescricao("Agachamento tradicional");
        exercicioRepository.create(exercicio);

        exercicio.setDescricao("Exercício para glúteos e quadríceps");

        Exercicio atualizado = exercicioRepository.update(exercicio);

        assertEquals("Exercício para glúteos e quadríceps", atualizado.getDescricao());

        Exercicio doBanco = exercicioRepository.findByNome("Agachamento");
        assertEquals("Exercício para glúteos e quadríceps", doBanco.getDescricao());
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve deletar exercício existente no banco")
    void testDelete_ExercicioExistente() {

        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Remada");
        exercicio.setDescricao("Remada baixa");
        exercicioRepository.create(exercicio);

        assertNotNull(exercicioRepository.findByNome("Remada"));

        boolean deletado = exercicioRepository.delete("Remada");

        assertTrue(deletado);
        assertNull(exercicioRepository.findByNome("Remada"));
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve listar todos os exercícios salvos")
    void testFindAll() {

        Exercicio e1 = new Exercicio();
        e1.setNome("Puxada");
        e1.setDescricao("Puxada no pulley");
        exercicioRepository.create(e1);

        Exercicio e2 = new Exercicio();
        e2.setNome("Tríceps Corda");
        e2.setDescricao("Extensão com corda");
        exercicioRepository.create(e2);

        List<Exercicio> lista = exercicioRepository.findAll();

        assertNotNull(lista);
        assertEquals(2, lista.size());
    }
}
