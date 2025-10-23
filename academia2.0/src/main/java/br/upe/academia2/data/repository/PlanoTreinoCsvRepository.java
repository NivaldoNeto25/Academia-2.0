package br.upe.academia2.data.repository;

import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanoTreinoCsvRepository {

    private static final Logger logger = Logger.getLogger(PlanoTreinoCsvRepository.class.getName());

    private final String baseDir;
    private final ExercicioBusiness exercicioBusiness = new ExercicioBusiness();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PlanoTreinoCsvRepository() {
        String projectDir = System.getProperty("user.dir");
        this.baseDir = projectDir + File.separator + "data" + File.separator + "planos" + File.separator;

        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getArquivoPlano(Usuario usuario) {
        return baseDir + "plano_" + usuario.getEmail().replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
    }

    public void salvarPlanos(List<PlanoTreino> planos, Usuario usuario) {
        String filePath = getArquivoPlano(usuario);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("id,nomePlano,inicioPlano,fimPlano,emailUsuario\n");

            for (PlanoTreino plano : planos) {
                writer.write(String.format("%d,%s,%s,%s,%s%n",
                        plano.getId(),
                        escape(plano.getNomePlano()),
                        dateFormat.format(plano.getInicioPlano()),
                        dateFormat.format(plano.getFimPlano()),
                        escape(plano.getUsuario().getEmail())));

                for (SecaoTreino secao : plano.getSecoes()) {
                    for (ItemPlanoTreino item : secao.getItensPlano()) {
                        writer.write(String.format("%s,%s,%d,%d,%d%n",
                                escape(secao.getNomeTreino()),
                                escape(item.getExercicio().getNome()),
                                item.getSeries(),
                                item.getRepeticoes(),
                                item.getCarga()));
                    }
                }

                writer.write("PLANO_END\n");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "Erro ao salvar plano de treino: " + e.getMessage());
        }
    }

    public List<PlanoTreino> carregarPlanos(Usuario usuario) {
        String arquivoPlano = getArquivoPlano(usuario);
        File file = new File(arquivoPlano);
        List<PlanoTreino> planos = new ArrayList<>();

        if (!file.exists()) {
            logger.log(Level.WARNING, () -> "Arquivo de plano não encontrado: " + arquivoPlano);
            return planos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoPlano))) {
            @SuppressWarnings("unused")
            String jump = reader.readLine();

            PlanoTreino planoAtual = null;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("PLANO_END")) {
                    if (planoAtual != null) {
                        planos.add(planoAtual);
                    }
                    planoAtual = null;
                    continue;
                }

                String[] partes = line.split(",", -1);

                if (planoAtual == null) {

                    if(partes.length >= 5) {
                        int id = Integer.parseInt(partes[0]);
                        String nomePlano = unescape(partes[1]);
                        Date inicioPlano = dateFormat.parse(partes[2]);
                        Date fimPlano = dateFormat.parse(partes[3]);

                        planoAtual = new PlanoTreino(id, nomePlano, inicioPlano, fimPlano, usuario);
                    }
                } else {
                    if (partes.length >= 5) {
                        String nomeSecao = unescape(partes[0]);
                        String nomeExercicio = unescape(partes[1]);
                        int series = Integer.parseInt(partes[2]);
                        int repeticoes = Integer.parseInt(partes[3]);
                        int carga = Integer.parseInt(partes[4]);

                        SecaoTreino secao = planoAtual.getOuCriarSecao(nomeSecao);
                        Exercicio exercicio = exercicioBusiness.buscarExercicioPorNome(nomeExercicio);

                        if (exercicio != null) {
                            ItemPlanoTreino item = new ItemPlanoTreino(exercicio, series, repeticoes, carga);
                            secao.addItemSecao(item);
                        } else {
                            logger.log(Level.WARNING, () -> "O exercício ''" + nomeExercicio + "'' listado no plano de treino não foi encontrado no arquivo de exercícios e será ignorado.");
                        }
                    }
                }
                
            }

            return planos;

        } catch (IOException | ParseException | NumberFormatException e) {
            logger.log(Level.SEVERE, e, () -> "Erro ao carregar plano de treino: " + e.getMessage());
            return planos;
        }
    }

    public List<PlanoTreino> listarPlanosPorUsuario(Usuario usuario) {
        return carregarPlanos(usuario);
    }

    private String escape(String campo) {
        return campo != null ? campo.replace(",", "\\,") : "";
    }

    private String unescape(String campo) {
        return campo != null ? campo.replace("\\,", ",") : "";
    }
}