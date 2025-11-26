package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.repository.interfaces.IIndBioRepository;
import jakarta.persistence.*;
import java.util.List;

public class IndBioJpaRepository implements IIndBioRepository {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("academiaPU");

    @Override
    public boolean save(IndicadorBiomedico indicadorBiomedico) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(indicadorBiomedico);
        em.getTransaction().commit();
        em.close();
        return true;
    }

    @Override
    public List<IndicadorBiomedico> findAll() {
        EntityManager em = emf.createEntityManager();
        List<IndicadorBiomedico> lista = em.createQuery("SELECT i FROM IndicadorBiomedico i", IndicadorBiomedico.class).getResultList();
        em.close();
        return lista;
    }

    @Override
    public boolean update(IndicadorBiomedico indicadorBiomedico) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(indicadorBiomedico);
        em.getTransaction().commit();
        em.close();
        return true;
    }
}
