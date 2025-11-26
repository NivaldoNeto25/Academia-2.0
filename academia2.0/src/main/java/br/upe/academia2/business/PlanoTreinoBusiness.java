package br.upe.academia2.business;

import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoJpaRepository;
import br.upe.academia2.data.repository.interfaces.IUsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PlanoTreinoBusiness {
    private final IUsuarioRepository usuarioRepository;
    private final PlanoTreinoJpaRepository planoRepository;
    private final Logger logger = Logger.getLogger(PlanoTreinoBusiness.class.getName());

    public PlanoTreinoBusiness(IUsuarioRepository usuarioRepository, PlanoTreinoJpaRepository planoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.planoRepository = planoRepository;
    }

    public void cadastrarPlanoDeTreino(Usuario usuario, PlanoTreino plano) {
        if (usuario == null || plano == null) {
            logger.warning("Usuário ou Plano de Treino nulos.");
            return;
        }
        plano.setUsuario(usuario);
        planoRepository.salvarOuAtualizarPlano(plano);
        usuario.getPlanTreinos().add(plano);
        usuarioRepository.update(usuario);
        logger.info("Plano de treino '" + plano.getNomePlano() + "' cadastrado para o usuário " + usuario.getNome());
    }

    public PlanoTreino carregarPlanoDoUsuario(Usuario usuario) {
        if (usuario == null) {
            logger.warning("Usuário nulo.");
            return null;
        }
        List<PlanoTreino> planos = planoRepository.listarPlanosPorUsuario(usuario);
        return (planos != null && !planos.isEmpty()) ? planos.get(0) : null;
    }

    public void modificarPlanoDeTreino(PlanoTreino plano) {
        if (plano == null || plano.getUsuario() == null) {
            logger.warning("Plano ou usuário nulos.");
            return;
        }
        planoRepository.salvarOuAtualizarPlano(plano);
        logger.info("Plano de treino atualizado com sucesso!");
    }

    public void deletarPlanoDeTreino(int id) {
        if (planoRepository.deletarPlano(id)) {
            logger.info("Plano de treino deletado com sucesso!");
        } else {
            logger.warning("Plano de treino não encontrado para deletar!");
        }
    }

    public List<PlanoTreino> listarPlanosPorUsuario(Usuario usuario) {
        if (usuario == null) {
            logger.warning("Usuário nulo ao tentar listar planos.");
            return new ArrayList<>();
        }
        return planoRepository.listarPlanosPorUsuario(usuario);
    }
}
