package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.interfaces.IUsuarioRepository;
import jakarta.persistence.*;
import java.util.List;

public class UsuarioJpaRepository implements IUsuarioRepository {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("academiaPU");

    private static UsuarioJpaRepository instance;

    private UsuarioJpaRepository() { }

    public static UsuarioJpaRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioJpaRepository();
        }
        return instance;
    }

    @Override
    public Usuario create(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();
        em.close();
        return usuario;
    }

    @Override
    public Usuario findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        Usuario u = em.find(Usuario.class, email);
        em.close();
        return u;
    }

    @Override
    public Usuario update(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Usuario atualizado = em.merge(usuario);
        em.getTransaction().commit();
        em.close();
        return atualizado;
    }

    @Override
    public boolean delete(String email) {
        EntityManager em = emf.createEntityManager();
        Usuario usuario = em.find(Usuario.class, email);
        boolean removido = false;
        if (usuario != null) {
            em.getTransaction().begin();
            em.remove(usuario);
            em.getTransaction().commit();
            removido = true;
        }
        em.close();
        return removido;
    }

    @Override
    public List<Usuario> listarTodos() {
        EntityManager em = emf.createEntityManager();
        List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();
        em.close();
        return usuarios;
    }
}
