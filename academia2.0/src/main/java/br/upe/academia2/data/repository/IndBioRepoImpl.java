package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.repository.interfaces.IIndBioRepository;

import java.util.*;
import java.util.logging.Logger;


public class IndBioRepoImpl implements IIndBioRepository {

    private List<IndicadorBiomedico> indicadoresBiomedicos = new ArrayList<>();
    private Logger logger = Logger.getLogger(IndBioRepoImpl.class.getName());

    @Override
    public boolean save(IndicadorBiomedico indicadorBiomedico) {
        try{
            if(indicadorBiomedico == null){
                throw new IllegalArgumentException("Indicador Bio não pode ser nulo");
            }else {
                return indicadoresBiomedicos.add(indicadorBiomedico);
            }
        } catch (Exception e) {
            logger.warning("O método de salvar falhou");
        }
        return false;
    }

    @Override
    public List<IndicadorBiomedico> findAll() {
        return indicadoresBiomedicos;
    }

}

