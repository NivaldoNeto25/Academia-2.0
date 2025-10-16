package br.upe.academia2.data.repository.interfaces;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import java.util.List;

public interface IIndBioRepository {
    boolean save(IndicadorBiomedico indicadorBiomedico);
    List<IndicadorBiomedico> findAll();
    boolean update(IndicadorBiomedico indicadorBiomedico);
}