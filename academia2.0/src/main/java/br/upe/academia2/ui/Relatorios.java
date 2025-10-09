package br.upe.academia2.ui;

import br.upe.academia2.business.CSVManipBusiness;
import br.upe.academia2.data.beans.IndicadorBiomedico;
import br.upe.academia2.data.beans.Usuario;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Relatorios {
    private final Scanner sc = new Scanner(System.in);
    private final String caminhoCSV = "data/indicadores.csv";
    private final CSVManipBusiness csvManip = new CSVManipBusiness();
    private static final Logger logger = Logger.getLogger(Relatorios.class.getName());

    public void exibirMenuRelatorios(Usuario usuario) {
        boolean sair = false;

        while (!sair) {
            logger.info("=".repeat(20));
            logger.info("RELATÓRIOS");
            logger.info("=".repeat(20));
            logger.info("1 - Relatório Geral");
            logger.info("2 - Relatório Comparativo");
            logger.info("3 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1:
                        relatorioGeral(usuario);
                        break;
                    case 2:
                        relatorioComparativo(usuario);
                        break;
                    case 3:
                        sair = true;
                        break;
                    default:
                        logger.info("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                logger.info("Erro: Entrada inválida!");
                sc.nextLine();
            }
        }
    }

    private void relatorioGeral(Usuario usuario) {
        List<IndicadorBiomedico> lista = carregarIndicadores(usuario.getEmail());

        if (lista.isEmpty()) {
            logger.info("Nenhum indicador encontrado para este usuário.");
            return;
        }

        logger.info("\nRELATÓRIO GERAL:");
        List<List<String>> dadosParaCSV = new ArrayList<>();

        for (IndicadorBiomedico ind : lista) {
            imprimirIndicador(ind);
            dadosParaCSV.add(Arrays.asList(
                    ind.getEmail(),
                    String.format(Locale.US, "%.2f", ind.getPeso()),
                    String.format(Locale.US, "%.2f", ind.getAltura()),
                    String.format(Locale.US, "%.2f", ind.getPercentualGordura()),
                    String.format(Locale.US, "%.2f", ind.getPercentualMassaMagra()),
                    String.format(Locale.US, "%.2f", ind.getImc()),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ind.getDataRegistro())
            ));
        }

        salvarCSV("relatorio_geral_" + usuario.getEmail().replace("@", "_") + ".csv",
                Arrays.asList("Email", "Peso", "Altura", "Gordura", "Massa Magra", "IMC", "Data Registro"),
                dadosParaCSV);
    }

    private void relatorioComparativo(Usuario usuario) {
        List<IndicadorBiomedico> lista = carregarIndicadores(usuario.getEmail());

        if (lista.isEmpty()) {
            logger.info("Nenhum indicador encontrado para este usuário.");
            return;
        }

        if (lista.size() == 1) {
            logger.info("Apenas um registro encontrado. Exibindo:");
            imprimirIndicador(lista.get(0));
            return;
        }

        // Mostrar todos os registros numerados
        logger.info("\nRegistros disponíveis:");
        for (int i = 0; i < lista.size(); i++) {
            logger.info(String.format("%d - Data: %s | IMC: %.2f | Peso: %.2fkg",
                    i + 1,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lista.get(i).getDataRegistro()),
                    lista.get(i).getImc(),
                    lista.get(i).getPeso()));
        }

        try {
            System.out.print("Digite o número do primeiro registro para comparar: ");
            int primeiroIdx = Integer.parseInt(sc.nextLine()) - 1;
            System.out.print("Digite o número do segundo registro para comparar: ");
            int segundoIdx = Integer.parseInt(sc.nextLine()) - 1;

            if (primeiroIdx < 0 || segundoIdx < 0 || primeiroIdx >= lista.size() || segundoIdx >= lista.size()) {
                logger.info("Índices inválidos.");
                return;
            }

            IndicadorBiomedico primeiro = lista.get(primeiroIdx);
            IndicadorBiomedico segundo = lista.get(segundoIdx);

            logger.info("\nRELATÓRIO COMPARATIVO:");
            logger.info("- Registro " + (primeiroIdx + 1) + ":");
            imprimirIndicador(primeiro);

            logger.info("- Registro " + (segundoIdx + 1) + ":");
            imprimirIndicador(segundo);

            List<List<String>> dados = List.of(
                    Arrays.asList("Registro " + (primeiroIdx + 1),
                            String.format(Locale.US, "%.2f", primeiro.getPeso()),
                            String.format(Locale.US, "%.2f", primeiro.getAltura()),
                            String.format(Locale.US, "%.2f", primeiro.getPercentualGordura()),
                            String.format(Locale.US, "%.2f", primeiro.getPercentualMassaMagra()),
                            String.format(Locale.US, "%.2f", primeiro.getImc()),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(primeiro.getDataRegistro())),
                    Arrays.asList("Registro " + (segundoIdx + 1),
                            String.format(Locale.US, "%.2f", segundo.getPeso()),
                            String.format(Locale.US, "%.2f", segundo.getAltura()),
                            String.format(Locale.US, "%.2f", segundo.getPercentualGordura()),
                            String.format(Locale.US, "%.2f", segundo.getPercentualMassaMagra()),
                            String.format(Locale.US, "%.2f", segundo.getImc()),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(segundo.getDataRegistro()))
            );

            salvarCSV("relatorio_comparativo_" + usuario.getEmail().replace("@", "_") + ".csv",
                    Arrays.asList("Registro", "Peso", "Altura", "Gordura", "Massa Magra", "IMC", "Data Registro"),
                    dados);

        } catch (NumberFormatException e) {
            logger.info("Entrada inválida. Digite apenas números.");
        }
    }

    private List<IndicadorBiomedico> carregarIndicadores(String emailUsuario) {
        List<IndicadorBiomedico> resultado = new ArrayList<>();

        try {
            ArrayList<String> linhas = csvManip.leitor(caminhoCSV);
            for (String linha : linhas) {
                String[] dados = linha.split(",");
                if (dados.length >= 7 && dados[0].equals(emailUsuario)) {
                    String email = dados[0];
                    double peso = Double.parseDouble(dados[1]);
                    double altura = Double.parseDouble(dados[2]);
                    double gordura = Double.parseDouble(dados[3]);
                    double massa = Double.parseDouble(dados[4]);
                    double imc = Double.parseDouble(dados[5]);
                    Date data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dados[6]);

                    resultado.add(new IndicadorBiomedico(email, peso, altura, gordura, massa, imc, data));
                }
            }
        } catch (Exception e) {
            logger.info("Erro ao carregar dados do CSV: " + e.getMessage());
        }

        return resultado;
    }

    private void imprimirIndicador(IndicadorBiomedico ind) {
        logger.info("-".repeat(50));
        logger.info("Data: " + ind.getDataRegistro());
        logger.info("Peso: " + ind.getPeso() + " kg");
        logger.info("Altura: " + ind.getAltura() + " m");
        logger.info("IMC: " + String.format("%.2f", ind.getImc()));
        logger.info("Gordura: " + ind.getPercentualGordura() + "%");
        logger.info("Massa Magra: " + ind.getPercentualMassaMagra() + "%");
    }

    private void salvarCSV(String nomeArquivo, List<String> cabecalho, List<List<String>> linhas) {
        try (FileWriter writer = new FileWriter("data/" + nomeArquivo)) {
            writer.append(String.join(",", cabecalho)).append("\n");
            for (List<String> linha : linhas) {
                writer.append(String.join(",", linha)).append("\n");
            }
            logger.info("Arquivo gerado: data/" + nomeArquivo);
        } catch (Exception e) {
            logger.info("Erro ao salvar CSV: " + e.getMessage());
        }
    }
}