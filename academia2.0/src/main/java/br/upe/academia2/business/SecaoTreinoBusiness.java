package br.upe.academia2.business;

import java.util.logging.Logger;

import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

public class SecaoTreinoBusiness {

    private Logger logger = Logger.getLogger(SecaoTreinoBusiness.class.getName());

    private PlanoTreinoBusiness planoTreinoBusiness;

    public SecaoTreinoBusiness() {
        this.planoTreinoBusiness = new PlanoTreinoBusiness(
            UsuarioCsvRepository.getInstance(), 
            new PlanoTreinoCsvRepository()
        );
    }

    public SecaoTreinoBusiness(PlanoTreinoBusiness planoTreinoBusiness) {
        this.planoTreinoBusiness = planoTreinoBusiness;
    }

    public void iniciarSessao(PlanoTreino plano) {
        if (plano != null) {
            logger.info("Iniciando seção de treino para o plano: " + plano.getNomePlano());
        }
    }

    public void registrarPerformance(PlanoTreino planoTreino, ItemPlanoTreino itemOriginal, int cargaRealizada, int repeticoesRealizadas, int seriesRealizadas) {
        logger.info("Atualizando dados do exercício '" + itemOriginal.getExercicio().getNome() + "' em memória.");

        itemOriginal.setCarga(cargaRealizada);
        itemOriginal.setRepeticoes(repeticoesRealizadas);
        itemOriginal.setSeries(seriesRealizadas);

        planoTreinoBusiness.modificarPlanoDeTreino(planoTreino);
    }
}