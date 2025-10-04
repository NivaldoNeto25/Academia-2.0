package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.repository.interfaces.IIndBioRepository;

import java.util.*;
import java.util.logging.Logger;


public class IndBioRepoImpl implements IIndBioRepository {

    private ArrayList<IndicadorBiomedico> indicadoresBiomedicos = new ArrayList<IndicadorBiomedico>();
    private Logger logger = Logger.getLogger(IndBioRepoImpl.class.getName());

    @Override
    public boolean save(IndicadorBiomedico indicadorBiomedico) {
        try{
            if(indicadorBiomedico == null){
                throw new Exception();
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

