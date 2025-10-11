package br.upe.academia2.business;

import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.interfaces.IUsuarioRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanoTreinoBusiness {

    private final IUsuarioRepository usuarioRepository;
    private final PlanoTreinoCsvRepository planoRepository;

    private Logger logger = Logger.getLogger(PlanoTreinoBusiness.class.getName());

    public PlanoTreinoBusiness(IUsuarioRepository usuarioRepository, PlanoTreinoCsvRepository planoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.planoRepository = planoRepository;
    }

    public void cadastrarPlanoDeTreino(Usuario usuario, PlanoTreino plano) {
        if (usuario == null || plano == null) {
            logger.warning("Usuário ou Plano de Treino nulos.");
            return;
        }

        plano.setUsuario(usuario);
        usuario.setPlanTreinos(List.of(plano));

        usuarioRepository.update(usuario);
        planoRepository.salvarPlano(plano);

        logger.info("Plano de treino '" + plano.getNomePlano() + "' cadastrado para o usuário " + usuario.getNome());
    }

    public PlanoTreino carregarPlanoDoUsuario(Usuario usuario) {
        if (usuario == null) {
            logger.warning("Usuário nulo.");
            return null;
        }

        PlanoTreino plano = planoRepository.carregarPlano(usuario);

        if (plano != null) {
            plano.setUsuario(usuario);
        }

        return plano;
    }

    public void modificarPlanoDeTreino(PlanoTreino plano) {
        if (plano == null || plano.getUsuario() == null) {
            logger.warning("Plano ou usuário nulos.");
            return;
        }

        planoRepository.salvarPlano(plano);
        logger.info("Plano de treino atualizado com sucesso!");
    }

    public void exibirPlanoDeTreino(PlanoTreino plano) {
        logger.info("Plano: " + plano.getNomePlano());

        for (var secao : plano.getSecoes()) {
            logger.info(" - Seção: " + secao.getNomeTreino());
            for (var item : secao.getItensPlano()) {
                if (logger.isLoggable(Level.INFO)){
                logger.info(String.format(
                        "     - %s: %d séries x %d reps (carga: %dkg)",
                                item.getExercicio().getNome(),
                                item.getSeries(),
                                item.getRepeticoes(),
                                item.getCarga()
                        )
                );
                }
            }
        }
    }
}
