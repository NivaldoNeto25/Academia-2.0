package br.upe.academia2.ui;

import br.upe.academia2.business.*;
import br.upe.academia2.data.beans.*;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class InterfaceBiomedico {

    private UsuarioBusiness usuarioBusiness;

    private IndicadorBiomedicoBusiness indicadorBiomedicoBusiness;
    private Scanner sc = new Scanner(System.in);
    private Usuario usuarioLogado;
    private static final Logger logger = Logger.getLogger(InterfaceBiomedico.class.getName());

    public InterfaceBiomedico() {
        indicadorBiomedicoBusiness = new IndicadorBiomedicoBusiness();
    }

    public void exibirMenuIndicadores(Usuario aluno) {
        usuarioLogado = new Comum(
                aluno.getNome(),
                aluno.getTelefone(),
                aluno.getEmail(),
                aluno.getSenha(),
                aluno.getPesoAtual(),
                aluno.getAlturaAtual(),
                aluno.getPercGorduraAtual()
        );
        boolean voltar = false;
        while (!voltar) {
            logger.info("\n=== INDICADORES BIOMÉDICOS ===");
            logger.info("1 - Cadastrar indicadores");
            logger.info("2 - Listar indicadores");
            logger.info("3 - Importar indicadores (CSV)");
            logger.info("4 - Voltar");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1:
                        cadastrarIndicadores();
                        break;
                    case 2:
                        listarIndicadores();
                        break;
                    case 3:
                        importarIndicadoresCSV();
                        break;
                    case 4:
                        voltar = true;
                        break;
                    default:
                        logger.info("Opção inválida! Tente novamente.");
                }
            } catch (InputMismatchException e) {
                logger.info("Erro: Entrada inválida. Por favor, digite um número.");
                sc.nextLine();
            }
        }
    }

    private void cadastrarIndicadores() {
        logger.info("\n=== CADASTRAR INDICADORES BIOMÉDICOS ===");

        try {
            System.out.print("Peso (kg): ");
            double peso = sc.nextDouble();
            sc.nextLine();
            System.out.print("Altura (m): ");
            double altura = sc.nextDouble();
            sc.nextLine();
            System.out.print("Percentual de gordura (%): ");
            double percGordura = sc.nextDouble();
            sc.nextLine();
            System.out.print("Percentual de massa magra (%): ");
            double percMassaMagra = sc.nextDouble();
            sc.nextLine();

            String id = usuarioLogado.getEmail();
            double imc = peso / (altura * altura);
            IndicadorBiomedico indicador = new IndicadorBiomedico(id, peso, altura, percGordura, percMassaMagra, imc, new Date());

            indicadorBiomedicoBusiness.cadastrarIndicador(usuarioLogado, indicador);

            logger.info("Indicador cadastrado com sucesso!");

        } catch (InputMismatchException e) {
            logger.info("Erro: Digite valores numéricos válidos.");
            sc.nextLine();
        }
    }

    private void listarIndicadores() {
        logger.info("\n=== SEUS INDICADORES BIOMÉDICOS ===");

        List<IndicadorBiomedico> indicadores = indicadorBiomedicoBusiness.listarIndicadores(usuarioLogado);

        if (indicadores.isEmpty()) {
            logger.info("Nenhum indicador biomédico cadastrado.");
            return;
        }

        logger.info("Total de registros: " + indicadores.size());
        logger.info("-".repeat(60));

        for (int i = 0; i < indicadores.size(); i++) {
            IndicadorBiomedico ind = indicadores.get(i);
            logger.info("Registro " + (i + 1) + " (Usuário: " + ind.getEmail() + ")");
            logger.info("Peso: " + ind.getPeso() + " kg");
            logger.info("Altura: " + ind.getAltura() + " m");
            logger.info("IMC: " + String.format("%.2f", ind.getImc()));
            logger.info("Gordura: " + ind.getPercentualGordura() + "%");
            logger.info("Massa Magra: " + ind.getPercentualMassaMagra() + "%");
            logger.info("Data: " + ind.getDataRegistro());
            logger.info("-".repeat(60));
        }
    }

    private void importarIndicadoresCSV() {
        logger.info("\n=== IMPORTAR INDICADORES BIOMÉDICOS (CSV) ===");
        logger.info("Formato esperado do CSV:");
        logger.info("peso,altura,percentualGordura,percentualMassaMagra");
        logger.info("Exemplo: 70.5,1.75,15.2,84.8");
        logger.info("");

        System.out.print("Digite o caminho completo do arquivo CSV: ");
        String caminhoArquivo = sc.nextLine();

        boolean importado = indicadorBiomedicoBusiness.importarIndicadoresDeCSV(caminhoArquivo);

        if (importado) {
            logger.info("Indicadores importados com sucesso!");
        } else {
            logger.info("Erro ao importar indicadores. Verifique o formato do arquivo.");
        }
    }

    public void relatorioEvolucao() {
        // Implementação futura
    }
}