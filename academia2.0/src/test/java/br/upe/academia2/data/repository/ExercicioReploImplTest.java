package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Exercicio;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExercicioRepoImplTest {

    private ExercicioJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ExercicioJpaRepository();
        
        List<Exercicio> exerciciosExistentes = repository.findAll();
        for (Exercicio ex : exerciciosExistentes) {
            repository.delete(ex.getNome());
        }

        assertEquals(0, repository.findAll().size(), "O repositório deve estar vazio no início de cada teste.");
    }

    @Test
    @DisplayName("Deve criar um novo exercício")
    void testCreateExercicio() {
        Exercicio exercicio = new Exercicio("Flexão", "Flexão de braço", "caminho/flexao.gif");
        repository.create(exercicio);

        List<Exercicio> exercicios = repository.findAll();
        assertEquals(1, exercicios.size());
        assertEquals("Flexão", exercicios.get(0).getNome());
    }

    @Test
    @DisplayName("Deve encontrar exercício por nome")
    void testFindByNome() {
        repository.create(new Exercicio("Agachamento", "Agachamento livre", "agach.gif"));

        Exercicio found = repository.findByNome("Agachamento");
        assertNotNull(found);
        assertEquals("Agachamento", found.getNome());
        assertNull(repository.findByNome("Caminhada"));
    }

    @Test
    @DisplayName("Deve atualizar um exercício existente")
    void testUpdateExercicio() {
        Exercicio original = new Exercicio("Supino", "Supino Reto", "supino.gif");
        repository.create(original);

        original.setDescricao("Supino Inclinado");
        repository.update(original);

        Exercicio loaded = repository.findByNome("Supino");
        assertEquals("Supino Inclinado", loaded.getDescricao());
    }

    @Test
    @DisplayName("Deve deletar um exercício existente")
    void testDeleteExercicio() {
        repository.create(new Exercicio("Remada", "Remada", "remada.gif"));
        
        assertTrue(repository.delete("Remada"));
        assertNull(repository.findByNome("Remada"));
    }

    @Test
    @DisplayName("Deve listar todos os exercícios")
    void testFindAll() {
        repository.create(new Exercicio("Ex1", "D1", "C1"));
        repository.create(new Exercicio("Ex2", "D2", "C2"));

        List<Exercicio> all = repository.findAll();
        assertEquals(2, all.size());
    }
}