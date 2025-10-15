package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.repository.interfaces.IIndBioRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class IndBioRepoImpl implements IIndBioRepository {

    private List<IndicadorBiomedico> indicadoresBiomedicos = new ArrayList<>();
    private Logger logger = Logger.getLogger(IndBioRepoImpl.class.getName());

    private static final String CAMINHO_ARQUIVO = "db/usuario.csv";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public IndBioRepoImpl() {
        loadFromCSV();
    }

    @Override
    public boolean save(IndicadorBiomedico indicadorBiomedico) {
        try{
            if(indicadorBiomedico == null){
                throw new IllegalArgumentException("Indicador Bio não pode ser nulo");
            } else {
                return indicadoresBiomedicos.add(indicadorBiomedico);
            }
        } catch (Exception e) {
            logger.warning("Método salvar falhou: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<IndicadorBiomedico> findAll() {
        return indicadoresBiomedicos;
    }

    @Override
    public boolean update(IndicadorBiomedico indicadorBiomedico) {
        try {
            for (int i = 0; i < indicadoresBiomedicos.size(); i++) {
                IndicadorBiomedico atual = indicadoresBiomedicos.get(i);
                if (atual.getEmail().equals(indicadorBiomedico.getEmail())) {
                    // Critério para substituir - usar dataRegistro igual ou estratégia desejada
                    // Aqui substituímos o indicador com dataRegistro mais antiga
                    if (atual.getDataRegistro().before(indicadorBiomedico.getDataRegistro())) {
                        indicadoresBiomedicos.set(i, indicadorBiomedico);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Falha ao atualizar indicador: " + e.getMessage());
        }
        return false;
    }

    public void loadFromCSV() {
        indicadoresBiomedicos.clear();
        try(BufferedReader reader = new BufferedReader(new FileReader(CAMINHO_ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length < 7) continue;
                String email = dados[0];
                double peso = Double.parseDouble(dados[1]);
                double altura = Double.parseDouble(dados[2]);
                double gordura = Double.parseDouble(dados[3]);
                double massa = Double.parseDouble(dados[4]);
                double imc = Double.parseDouble(dados[5]);
                Date dataRegistro = sdf.parse(dados[6]);
                IndicadorBiomedico ind = new IndicadorBiomedico(email, peso, altura, gordura, massa, imc, dataRegistro);
                indicadoresBiomedicos.add(ind);
            }
            logger.info("Dados carregados: " + indicadoresBiomedicos.size());
        } catch (Exception e) {
            logger.warning("Erro ao carregar CSV: " + e.getMessage());
        }
    }
}