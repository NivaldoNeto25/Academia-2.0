package br.upe.academia2.business;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.IndBioJpaRepository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class IndicadorBiomedicoBusiness {
    private IndBioJpaRepository indBioRepository = new IndBioJpaRepository();
    private Logger logger = Logger.getLogger(IndicadorBiomedicoBusiness.class.getName());
    private CSVManipBusiness fileManip = new CSVManipBusiness();

    public void cadastrarIndicador(Usuario usuario, IndicadorBiomedico indicador) {
        if (usuario != null && indicador != null) {
            indicador.setUsuario(usuario);
            indBioRepository.save(indicador);
        }
    }

    public boolean atualizarIndicador(IndicadorBiomedico indicador) {
        return indBioRepository.update(indicador);
    }

    public List<IndicadorBiomedico> listarIndicadores(Usuario usuario) {
        List<IndicadorBiomedico> resultado = new ArrayList<>();
        for (IndicadorBiomedico ind : indBioRepository.findAll()) {
            if (ind.getUsuario() != null && usuario.getEmail().equals(ind.getUsuario().getEmail())) {
                resultado.add(ind);
            }
        }
        return resultado;
    }


    public boolean importarIndicadoresDeCSV(String caminhoArquivo, Usuario usuario) {
        try {
            List<String> linhas = fileManip.leitor(caminhoArquivo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (String linha : linhas) {
                String[] dados = linha.split(",");
                if (dados.length < 7) continue;
                double peso = Double.parseDouble(dados[1]);
                double altura = Double.parseDouble(dados[2]);
                double gordura = Double.parseDouble(dados[3]);
                double massa = Double.parseDouble(dados[4]);
                double imc = Double.parseDouble(dados[5]);
                Date dataRegistro = sdf.parse(dados[6]);
                IndicadorBiomedico ind = new IndicadorBiomedico(peso, altura, gordura, massa, imc, dataRegistro);
                ind.setUsuario(usuario);
                indBioRepository.save(ind);
            }
            logger.info("Importação realizada com sucesso!");
            return true;
        } catch (Exception e) {
            logger.warning("Erro ao importar indicadores: " + e.getMessage());
            return false;
        }
    }
}
