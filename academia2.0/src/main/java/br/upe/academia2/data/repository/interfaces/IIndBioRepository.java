package br.upe.academia2.data.repository.interfaces;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import java.util.List;
import java.util.Optional;

public interface IIndBioRepository {
    boolean save(IndicadorBiomedico indicadorBiomedico);
    List<IndicadorBiomedico> findAll();
}

