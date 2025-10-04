package br.upe.academia2.business;

import java.util.logging.Logger;

import br.upe.academia2.data.beans.ItemPlanoTreino;
import br.upe.academia2.data.beans.PlanoTreino;

public class SecaoTreinoBusiness {

    private PlanoTreinoBusiness planoTreinoBusiness;

    private Logger logger = Logger.getLogger(SecaoTreinoBusiness.class.getName());

    public SecaoTreinoBusiness() {
        this.planoTreinoBusiness = new PlanoTreinoBusiness();
    }

    public void iniciarSessao(PlanoTreino plano) {
        if (plano != null) {
            logger.info("Iniciando seção de treino para o plano: " + plano.getNomePlano());
        }
    }

    public void registrarPerformance(ItemPlanoTreino itemOriginal, int cargaRealizada, int repeticoesRealizadas, int seriesRealizadas) {
        logger.info("Atualizando dados do exercício '" + itemOriginal.getExercicio().getNome() + "' em memória.");

        itemOriginal.setCarga(cargaRealizada);
        itemOriginal.setRepeticoes(repeticoesRealizadas);
        itemOriginal.setSeries(seriesRealizadas);
    }
}