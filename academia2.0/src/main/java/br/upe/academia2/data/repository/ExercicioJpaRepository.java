package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.repository.interfaces.IExercicioRepository;
import jakarta.persistence.*;
import java.util.List;

public class ExercicioJpaRepository implements IExercicioRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("academiaPU");

    @Override
    public Exercicio create(Exercicio exercicio) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(exercicio);
        em.getTransaction().commit();
        em.close();
        return exercicio;
    }

    @Override
    public Exercicio findByNome(String nome) {
        EntityManager em = emf.createEntityManager();
        Exercicio e = em.find(Exercicio.class, nome);
        em.close();
        return e;
    }

    @Override
    public List<Exercicio> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Exercicio> exercicios = em.createQuery("SELECT e FROM Exercicio e", Exercicio.class).getResultList();
        em.close();
        return exercicios;
    }

    @Override
    public Exercicio update(Exercicio exercicio) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Exercicio atualizado = em.merge(exercicio);
        em.getTransaction().commit();
        em.close();
        return atualizado;
    }

    @Override
    public boolean delete(String nome) {
        EntityManager em = emf.createEntityManager();
        Exercicio exercicio = em.find(Exercicio.class, nome);
        boolean removido = false;
        if (exercicio != null) {
            em.getTransaction().begin();
            em.remove(exercicio);
            em.getTransaction().commit();
            removido = true;
        }
        em.close();
        return removido;
    }
}
