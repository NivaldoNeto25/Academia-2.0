package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.repository.interfaces.IIndBioRepository;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class IndBioRepoImpl implements IIndBioRepository {

    private List<IndicadorBiomedico> indicadoresBiomedicos = new ArrayList<>();
    private Logger logger = Logger.getLogger(IndBioRepoImpl.class.getName());
    private final String caminhoArquivo;

    public IndBioRepoImpl() {
        this("db/usuario.csv");
    }

    public IndBioRepoImpl(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        loadFromCSV();
    }

    @Override
    public boolean save(IndicadorBiomedico indicadorBiomedico) {
        if (indicadorBiomedico == null) {
            logger.warning("Indicador Biomedico não pode ser nulo");
            return false;
        }

        indicadoresBiomedicos.add(indicadorBiomedico);
        return salvarCSV();  
    }

    @Override
    public List<IndicadorBiomedico> findAll() {
        return new ArrayList<>(indicadoresBiomedicos);  
    }

    @Override
    public boolean update(IndicadorBiomedico indicadorBiomedico) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < indicadoresBiomedicos.size(); i++) {
            IndicadorBiomedico atual = indicadoresBiomedicos.get(i);
            boolean emailIgual = atual.getEmail().equals(indicadorBiomedico.getEmail());
            boolean dataIgual = false;
            if (atual.getDataRegistro() != null && indicadorBiomedico.getDataRegistroOriginal() != null) {
                String dataAtualStr = sdf.format(atual.getDataRegistro());
                String dataOriginalStr = sdf.format(indicadorBiomedico.getDataRegistroOriginal());
                dataIgual = dataAtualStr.equals(dataOriginalStr);
            }
            if (emailIgual && dataIgual) {
                indicadoresBiomedicos.set(i, indicadorBiomedico);
                return salvarCSV();
            }
        }
        return false;
    }

    public void loadFromCSV() {
        indicadoresBiomedicos.clear();
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            logger.info("Arquivo não existe: " + caminhoArquivo);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    private boolean salvarCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (IndicadorBiomedico ind : indicadoresBiomedicos) {
                String linha = String.join(",",
                        ind.getEmail(),
                        String.valueOf(ind.getPeso()),
                        String.valueOf(ind.getAltura()),
                        String.valueOf(ind.getPercentualGordura()),    
                        String.valueOf(ind.getPercentualMassaMagra()),      
                        String.valueOf(ind.getImc()),
                        sdf.format(ind.getDataRegistro())
                );
                writer.write(linha);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            logger.warning("Erro ao salvar CSV: " + e.getMessage());
            return false;
        }
    }

    // limpa lista e arquivo pra n dar erro
    public void limparDados() {
        indicadoresBiomedicos.clear();
        File arquivo = new File(caminhoArquivo);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }
}
