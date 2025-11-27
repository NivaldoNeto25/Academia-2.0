package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;

import jakarta.persistence.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanoTreinoJpaRepository {
    private static final Logger logger = Logger.getLogger(PlanoTreinoJpaRepository.class.getName());
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("academiaPU");

    public void salvarPlanos(List<PlanoTreino> planos, Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Remove todos os planos antigos do usuário
            em.createQuery("DELETE FROM PlanoTreino pt WHERE pt.usuario.email = :email")
                    .setParameter("email", usuario.getEmail())
                    .executeUpdate();
            // Salva os novos planos na base
            for (PlanoTreino plano : planos) {
                plano.setUsuario(usuario);
                em.persist(plano);
            }
            tx.commit();
            logger.info("Planos de treino salvos com sucesso para o usuário " + usuario.getEmail());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar planos de treino (JPA): " + e.getMessage(), e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    public List<PlanoTreino> carregarPlanos(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        List<PlanoTreino> lista = List.of();
        try {
            lista = em.createQuery(
                            "SELECT pt FROM PlanoTreino pt WHERE pt.usuario.email = :email",
                            PlanoTreino.class)
                    .setParameter("email", usuario.getEmail())
                    .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar planos de treino (JPA): " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return lista;
    }

    public void salvarOuAtualizarPlano(PlanoTreino plano) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (plano.getId() == 0) {
                em.persist(plano);
            } else {
                em.merge(plano);
            }
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar/atualizar plano de treino (JPA): " + e.getMessage(), e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
    }

    public boolean deletarPlano(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean removido = false;
        try {
            tx.begin();
            PlanoTreino plano = em.find(PlanoTreino.class, id);
            if (plano != null) {
                em.remove(plano);
                removido = true;
            }
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao remover plano de treino (JPA): " + e.getMessage(), e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
        return removido;
    }

    public List<PlanoTreino> listarPlanosPorUsuario(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        List<PlanoTreino> lista = List.of();
        try {
            lista = em.createQuery(
                            "SELECT pt FROM PlanoTreino pt WHERE pt.usuario.email = :email",
                            PlanoTreino.class)
                    .setParameter("email", usuario.getEmail())
                    .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao listar planos por usuário (JPA): " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return lista;
    }


    public PlanoTreino buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        PlanoTreino plano = null;
        try {
            plano = em.find(PlanoTreino.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar plano de treino (JPA): " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return plano;
    }
}
