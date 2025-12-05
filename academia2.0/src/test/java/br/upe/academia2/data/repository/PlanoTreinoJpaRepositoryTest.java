package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanoTreinoJpaRepositoryTest {

    private PlanoTreinoJpaRepository planoRepository;
    private EntityManagerFactory emfTest;

    @BeforeEach
    void setUp() throws Exception {
        emfTest = Persistence.createEntityManagerFactory("academiaTestPU");
        planoRepository = new PlanoTreinoJpaRepository();
        Field field = PlanoTreinoJpaRepository.class.getDeclaredField("emf");
        field.setAccessible(true);
        field.set(planoRepository, emfTest);
    }

    @AfterEach
    void tearDown() {
        if (emfTest != null && emfTest.isOpen()) {
            emfTest.close();
        }
    }

    // Método auxiliar para persistir usuário antes de salvar planos
    private void persistirUsuario(Usuario usuario) {
        EntityManager em = emfTest.createEntityManager();
        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve salvar lista de planos e recarregar com sucesso")
    void testSalvarECarregarPlanos() {
        Usuario usuario = new Comum("Charles X", "999", "user@x.com",
                "s", 70.0, 1.80, 10.0);
        persistirUsuario(usuario);

        PlanoTreino p1 = new PlanoTreino(0, "Plano A", new Date(), new Date(), usuario);
        PlanoTreino p2 = new PlanoTreino(0, "Plano B", new Date(), new Date(), usuario);

        planoRepository.salvarPlanos(List.of(p1, p2), usuario);

        List<PlanoTreino> doBanco = planoRepository.carregarPlanos(usuario);

        assertEquals(2, doBanco.size());
        assertTrue(doBanco.stream().anyMatch(pt -> pt.getNomePlano().equals("Plano A")));
        assertTrue(doBanco.stream().anyMatch(pt -> pt.getNomePlano().equals("Plano B")));
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve salvar e depois atualizar um plano")
    void testSalvarOuAtualizarPlano() {
        Usuario usuario = new Comum("MariaMilena", "111", "maria@test.com",
                "s", 45.0, 1.65, 12.0);
        persistirUsuario(usuario);

        PlanoTreino plano = new PlanoTreino(0, "Plano Inicial", new Date(), new Date(), usuario);
        planoRepository.salvarOuAtualizarPlano(plano);

        assertTrue(plano.getId() > 0);

        plano.setNomePlano("Plano Atualizado");
        planoRepository.salvarOuAtualizarPlano(plano);

        PlanoTreino doBanco = planoRepository.buscarPorId(plano.getId());
        assertEquals("Plano Atualizado", doBanco.getNomePlano());
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve deletar plano existente")
    void testDeletarPlano() {
        Usuario usuario = new Comum("Xavier", "222", "c@t.com",
                "s", 80.0, 1.75, 8.0);
        persistirUsuario(usuario);

        PlanoTreino plano = new PlanoTreino(0, "Plano Z", new Date(), new Date(), usuario);
        planoRepository.salvarOuAtualizarPlano(plano);

        int id = plano.getId();
        assertTrue(id > 0);

        assertNotNull(planoRepository.buscarPorId(id));

        boolean removido = planoRepository.deletarPlano(id);

        assertTrue(removido);
        assertNull(planoRepository.buscarPorId(id));
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve listar planos por usuário")
    void testListarPlanosPorUsuario() {
        Usuario usuario = new Comum("Nivaldo", "333", "teste@t.com",
                "s", 85.0, 1.70, 9.0);
        persistirUsuario(usuario);

        PlanoTreino p1 = new PlanoTreino(0, "P1", new Date(), new Date(), usuario);
        PlanoTreino p2 = new PlanoTreino(0, "P2", new Date(), new Date(), usuario);

        planoRepository.salvarPlanos(List.of(p1, p2), usuario);

        List<PlanoTreino> lista = planoRepository.listarPlanosPorUsuario(usuario);

        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("INTEGRAÇÃO: Deve buscar por ID corretamente")
    void testBuscarPorId() {
        Usuario usuario = new Comum("Vitoria", "444", "joao@test.com",
                "s", 65.0, 1.78, 25.0);
        persistirUsuario(usuario);

        PlanoTreino plano = new PlanoTreino(0, "Plano João", new Date(), new Date(), usuario);
        planoRepository.salvarOuAtualizarPlano(plano);

        PlanoTreino encontrado = planoRepository.buscarPorId(plano.getId());

        assertNotNull(encontrado);
        assertEquals("Plano João", encontrado.getNomePlano());
    }
}
