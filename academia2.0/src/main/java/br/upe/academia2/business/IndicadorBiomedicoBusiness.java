package br.upe.academia2.business;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.IndBioRepoImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class IndicadorBiomedicoBusiness {
    private IndBioRepoImpl indBioRepository = new IndBioRepoImpl();
    private static final String CAMINHO_ARQUIVO = "db/usuario.csv";

    private Logger logger = Logger.getLogger(IndicadorBiomedicoBusiness.class.getName());

    private CSVManipBusiness fileManip = new CSVManipBusiness();

    public IndicadorBiomedicoBusiness() {
        // Construtor
    }

    public void cadastrarIndicador(Usuario usuario, IndicadorBiomedico indicador) {
        if (usuario != null && indicador != null) {
            indicador.setEmail(usuario.getEmail());
            indBioRepository.save(indicador);
            salvarTodosNoCSV();
        }
    }

    public boolean atualizarIndicador(IndicadorBiomedico indicador) {
        boolean atualizado = indBioRepository.update(indicador);
        if (atualizado) {
            salvarTodosNoCSV();
        }
        return atualizado;
    }

    private void salvarTodosNoCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO, false))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (IndicadorBiomedico ind : indBioRepository.findAll()) {
                writer.append(String.format(Locale.US,
                        "%s,%.2f,%.2f,%.2f,%.2f,%.2f,%s%n",
                        ind.getEmail(),
                        ind.getPeso(),
                        ind.getAltura(),
                        ind.getPercentualGordura(),
                        ind.getPercentualMassaMagra(),
                        ind.getImc(),
                        sdf.format(ind.getDataRegistro())
                ));
            }
        } catch (IOException e) {
            logger.warning("Erro ao salvar todos no CSV: " + e.getMessage());
        }
    }

    public boolean importarIndicadoresDeCSV(String caminhoArquivo) {
        try {
            List<String> linhas = fileManip.leitor(caminhoArquivo);

            for (String linha : linhas) {
                String[] dados = linha.split(",");
                if (dados.length < 7) continue;

                String email = dados[0];
                double peso = Double.parseDouble(dados[1]);
                double altura = Double.parseDouble(dados[2]);
                double gordura = Double.parseDouble(dados[3]);
                double massa = Double.parseDouble(dados[4]);
                double imc = Double.parseDouble(dados[5]);
                Date dataRegistro = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dados[6]);

                IndicadorBiomedico ind = new IndicadorBiomedico(email, peso, altura, gordura, massa, imc, dataRegistro);
                indBioRepository.save(ind);
            }
            salvarTodosNoCSV();
            return true;
        } catch (Exception e) {
            logger.warning("Erro ao importar indicadores: " + e.getMessage());
            return false;
        }
    }

    public List<IndicadorBiomedico> listarIndicadores(Usuario usuario) {
        List<IndicadorBiomedico> resultado = new ArrayList<>();
        for (IndicadorBiomedico ind : indBioRepository.findAll()) {
            if (ind.getEmail().equals(usuario.getEmail())) {
                resultado.add(ind);
            }
        }
        return resultado;
    }
}