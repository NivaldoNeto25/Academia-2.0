package br.upe.academia2.ui;

import br.upe.academia2.data.beans.*;
import br.upe.academia2.data.repository.PlanoTreinoCsvRepository;
import br.upe.academia2.data.repository.UsuarioCsvRepository;
import br.upe.academia2.business.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanosDeTreino {


    private ExercicioBusiness exercicioBusiness;
    private PlanoTreinoBusiness planoTreinoBusiness;
    private SecaoTreinoBusiness secaoTreinoBusiness;
    private Scanner sc;
    private Usuario usuarioLogado;
    private static final Logger logger = Logger.getLogger(PlanosDeTreino.class.getName());

    private static final String MSG_ESCOLHA_OPCAO = "Escolha uma opção: ";
    private static final String MSG_OPCAO_INVALIDA = "Opção inválida!";
    private static final String MSG_ERRO_NUMERO_VALIDO = "Erro: Digite um número válido.";
    private static final String FORMATO_DATA = "dd/MM/yyyy";

    public PlanosDeTreino(Usuario usuarioLogado) {
        this.exercicioBusiness = new ExercicioBusiness();
        this.planoTreinoBusiness = new PlanoTreinoBusiness(
                UsuarioCsvRepository.getInstance(),      // AQUI FOI ALTERADO
                new PlanoTreinoCsvRepository()
        );
        this.secaoTreinoBusiness = new SecaoTreinoBusiness();
        this.sc = new Scanner(System.in);
        this.usuarioLogado = usuarioLogado;
    }
    public void exibirMenuPlanosDeTreino() {
        boolean sair = false;

        while (!sair) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("=".repeat(20));
                logger.info("PLANO DE TREINO");
                logger.info("=".repeat(20));
                logger.info("1 - Cadastrar plano de treino");
                logger.info("2 - Listar plano de treino");
                logger.info("3 - Modificar plano de treino");
                logger.info("4 - Seção Treino");
                logger.info("5 - Sair");
                logger.info(MSG_ESCOLHA_OPCAO);
            }

            try {
                int opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1:
                        cadastrarPlanoTreino();
                        break;
                    case 2:
                        listarPlano();
                        break;
                    case 3:
                        modificarPlano();
                        break;
                    case 4:
                        secaoTreino();
                        sair = true;
                        break;
                    case 5:
                        logger.info("Saindo...");
                        sair = true;
                        break;
                    default:
                        logger.info(MSG_OPCAO_INVALIDA);
                }
            } catch (Exception e) {
                logger.info("Erro: Entrada inválida!");
                sc.nextLine();
            }
        }
    }

    private void modificarPlano() {
        logger.info("\n=== MODIFICAR PLANO DE TREINO ===");

        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);

        if (plano == null) {
            logger.info("Você não possui um plano de treino para modificar.");
            return;
        }

        boolean finalizar = false;
        while (!finalizar) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("\n--- Modificando Plano: " + plano.getNomePlano() + " ---");
                planoTreinoBusiness.exibirPlanoDeTreino(plano);
                logger.info("\nOpções de Modificação:");
                logger.info("1 - Alterar nome do plano");
                logger.info("2 - Alterar datas");
                logger.info("3 - Adicionar exercício a uma seção");
                logger.info("4 - Modificar exercício existente");
                logger.info("5 - Remover exercício de uma seção");
                logger.info("6 - Salvar e Finalizar modificações");
                logger.info(MSG_ESCOLHA_OPCAO);
            }

            try {
                int opcao = Integer.parseInt(sc.nextLine());

                switch (opcao) {
                    case 1:
                        alterarNomePlano(plano);
                        break;
                    case 2:
                        alterarDatasPlano(plano);
                        break;
                    case 3:
                        adicionarExercicioAoPlano(plano);
                        break;
                    case 4:
                        modificarExercicioExistente(plano);
                        break;
                    case 5:
                        removerExercicioDoPlano(plano);
                        break;
                    case 6:
                        planoTreinoBusiness.modificarPlanoDeTreino(plano);
                        finalizar = true;
                        break;
                    default:
                        logger.info(MSG_OPCAO_INVALIDA);
                }
            } catch (NumberFormatException e) {
                logger.info(MSG_ERRO_NUMERO_VALIDO);
            }
        }
    }

    private void alterarNomePlano(PlanoTreino plano) {
        logger.info("Novo nome do plano (atual: " + plano.getNomePlano() + "): ");
        String novoNome = sc.nextLine();

        if (!novoNome.trim().isEmpty()) {
            plano.setNomePlano(novoNome);
            logger.info("Nome alterado com sucesso!");
        } else {
            logger.info("Nome não pode ser vazio.");
        }
    }

    private void alterarDatasPlano(PlanoTreino plano) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Nova data de início ({0}) - atual: {1}: ", new Object[]{FORMATO_DATA, sdf.format(plano.getInicioPlano())});
            }
            String dataInicioStr = sc.nextLine();

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Nova data de fim ({0}) - atual: {1}: ", new Object[]{FORMATO_DATA, sdf.format(plano.getFimPlano())});
            }
            String dataFimStr = sc.nextLine();

            if (!dataInicioStr.trim().isEmpty() && !dataFimStr.trim().isEmpty()) {
                Date dataInicio = sdf.parse(dataInicioStr);
                Date dataFim = sdf.parse(dataFimStr);

                plano.setInicioPlano(dataInicio);
                plano.setFimPlano(dataFim);

                logger.info("Datas alteradas com sucesso!");
            } else {
                logger.info("Operação cancelada - datas não podem ser vazias.");
            }

        } catch (ParseException e) {
            logger.info("Erro: Formato de data inválido. Use " + FORMATO_DATA);
        }
    }

    private void adicionarExercicioAoPlano(PlanoTreino plano) {
        logger.info("\n=== ADICIONAR EXERCÍCIO ===");
        coletarExerciciosParaPlano(plano);
    }

    private void coletarExerciciosParaPlano(PlanoTreino plano) {
        boolean continuar = true;

        while (continuar) {
            logger.info("\nNome da Seção (ex: Treino A - Peito e Tríceps): ");
            String nomeSecao = sc.nextLine();
            SecaoTreino secao = plano.getOuCriarSecao(nomeSecao);

            logger.info("Nome do exercício a adicionar nesta seção: ");
            String nomeExercicio = sc.nextLine();

            Exercicio exercicio = exercicioBusiness.buscarExercicioPorNome(nomeExercicio);

            if (exercicio == null) {
                logger.info("Exercício não encontrado! Cadastre-o primeiro no menu de exercícios.");
            } else {
                try {
                    logger.info("Número de séries: ");
                    int series = Integer.parseInt(sc.nextLine());
                    logger.info("Número de repetições: ");
                    int repeticoes = Integer.parseInt(sc.nextLine());
                    logger.info("Carga (kg): ");
                    int carga = Integer.parseInt(sc.nextLine());

                    ItemPlanoTreino item = new ItemPlanoTreino(exercicio, series, repeticoes, carga);
                    secao.addItemSecao(item);
                    logger.log(Level.INFO, "'{0}' adicionado à seção '{1}'.", new Object[]{nomeExercicio, nomeSecao});

                } catch (NumberFormatException e) {
                    logger.info("Erro: Digite valores numéricos válidos para séries, repetições e carga.");
                }
            }
            logger.info("Deseja adicionar outro exercício a este plano? (s/n): ");
            continuar = sc.nextLine().equalsIgnoreCase("s");
        }
    }

    private void modificarExercicioExistente(PlanoTreino plano) {
        if (plano.getItens().isEmpty()) {
            logger.info("Este plano não possui exercícios para modificar.");
            return;
        }

        logger.info("Exercícios no plano:");
        for (int i = 0; i < plano.getItens().size(); i++) {
            ItemPlanoTreino item = plano.getItens().get(i);
            logger.log(Level.INFO, "{0}. {1} ({2}x{3}, {4}kg)", new Object[]{i + 1, item.getExercicio().getNome(), item.getSeries(), item.getRepeticoes(), item.getCarga()});
        }

        try {
            logger.info("Escolha o exercício para modificar (número): ");
            int escolha = sc.nextInt() - 1;
            sc.nextLine();

            if (escolha >= 0 && escolha < plano.getItens().size()) {
                ItemPlanoTreino item = plano.getItens().get(escolha);

                logger.log(Level.INFO, "Novas séries (atual: {0}): ", item.getSeries());
                int novasSeries = sc.nextInt();

                logger.log(Level.INFO, "Novas repetições (atual: {0}): ", item.getRepeticoes());
                int novasRepeticoes = sc.nextInt();

                logger.log(Level.INFO, "Nova carga (atual: {0}kg): ", item.getCarga());
                int novaCarga = sc.nextInt();
                sc.nextLine();

                item.setSeries(novasSeries);
                item.setRepeticoes(novasRepeticoes);
                item.setCarga(novaCarga);

                logger.info("Exercício modificado com sucesso!");

            } else {
                logger.info(MSG_OPCAO_INVALIDA);
            }
        } catch (InputMismatchException e) {
            logger.info(MSG_ERRO_NUMERO_VALIDO);
            sc.nextLine();
        }
    }

    private void removerExercicioDoPlano(PlanoTreino plano) {
        if (plano.getItens().isEmpty()) {
            logger.info("Este plano não possui exercícios para remover.");
            return;
        }

        logger.info("Exercícios no plano:");
        for (int i = 0; i < plano.getItens().size(); i++) {
            ItemPlanoTreino item = plano.getItens().get(i);
            logger.log(Level.INFO, "{0}. {1}", new Object[]{i + 1, item.getExercicio().getNome()});
        }

        try {
            logger.info("Escolha o exercício para remover (número): ");
            int escolha = sc.nextInt() - 1;
            sc.nextLine();

            if (escolha >= 0 && escolha < plano.getItens().size()) {
                ItemPlanoTreino itemRemovido = plano.getItens().remove(escolha);
                logger.log(Level.INFO, "Exercício '{0}' removido com sucesso!", itemRemovido.getExercicio().getNome());
            } else {
                logger.info(MSG_OPCAO_INVALIDA);
            }
        } catch (InputMismatchException e) {
            logger.info(MSG_ERRO_NUMERO_VALIDO);
            sc.nextLine();
        }
    }

    private void listarPlano() {
        PlanoTreino planoVisualizar = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);

        if (planoVisualizar != null) {
            planoTreinoBusiness.exibirPlanoDeTreino(planoVisualizar);
        } else {
            logger.info("Você ainda não possui um plano de treino cadastrado.");
        }
    }

    private void cadastrarPlanoTreino() {
        logger.info("\n=== CADASTRAR NOVO PLANO DE TREINO ===");

        try {
            logger.info("Nome do plano: ");
            String nomePlano = sc.nextLine();

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Data de início ({0}): ", FORMATO_DATA);
            }
            String dataInicioStr = sc.nextLine();

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Data de fim ({0}): ", FORMATO_DATA);
            }
            String dataFimStr = sc.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA);
            Date dataInicio = sdf.parse(dataInicioStr);
            Date dataFim = sdf.parse(dataFimStr);

            PlanoTreino novoPlano = new PlanoTreino(0, nomePlano, dataInicio, dataFim, usuarioLogado);

            coletarExerciciosParaPlano(novoPlano);

            planoTreinoBusiness.cadastrarPlanoDeTreino(usuarioLogado, novoPlano);

        } catch (ParseException e) {
            logger.info("Erro: Formato de data inválido. Use " + FORMATO_DATA);
        } catch (Exception e) {
            logger.info("Erro ao cadastrar plano: " + e.getMessage());
        }
    }

    public void secaoTreino() {
        logger.info("\n=== SEÇÃO DE TREINO ===");

        PlanoTreino plano = planoTreinoBusiness.carregarPlanoDoUsuario(usuarioLogado);

        if (plano == null || plano.getSecoes().isEmpty()) {
            logger.info("Você precisa ter um plano de treino com exercícios cadastrados para iniciar uma seção.");
            return;
        }

        iniciarSecaoTreino(plano);
    }

    private void iniciarSecaoTreino(PlanoTreino plano) {
        logger.info("\n=== INICIANDO SEÇÃO DE TREINO ===");
        logger.info("Plano: " + plano.getNomePlano());

        secaoTreinoBusiness.iniciarSessao(plano);

        logger.info("\n1 - Exibir seção como cartão (consulta)");
        logger.info("2 - Executar treino (preenchimento)");
        logger.info(MSG_ESCOLHA_OPCAO);

        try {
            int opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    exibirSecaoComoCartao(plano);
                    break;
                case 2:
                    executarTreino(plano);
                    break;
                default:
                    logger.info(MSG_OPCAO_INVALIDA);
            }
        } catch (InputMismatchException e) {
            logger.info(MSG_ERRO_NUMERO_VALIDO);
            sc.nextLine();
        }
    }

    private void exibirSecaoComoCartao(PlanoTreino plano) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("\n" + "=".repeat(50));
            logger.info("CARTÃO DE TREINO");
            logger.info("=".repeat(50));
            logger.log(Level.INFO, "Plano: {0}", plano.getNomePlano());
            logger.log(Level.INFO, "Usuário: {0}", usuarioLogado.getNome());
            logger.log(Level.INFO, "Data: {0}", new SimpleDateFormat(FORMATO_DATA).format(new Date()));
            logger.info("=".repeat(50));
        }

        for (int i = 0; i < plano.getItens().size(); i++) {
            ItemPlanoTreino item = plano.getItens().get(i);
            logger.log(Level.INFO, "{0}. {1}", new Object[]{i + 1, item.getExercicio().getNome()});
            logger.log(Level.INFO, "Séries: {0}", item.getSeries());
            logger.log(Level.INFO, "Repetições: {0}", item.getRepeticoes());
            logger.log(Level.INFO, "Carga: {0} kg", item.getCarga());
            logger.log(Level.INFO, "Descrição: {0}", item.getExercicio().getDescricao());
            logger.info("");
        }

        if (logger.isLoggable(Level.INFO)) {
            logger.info("=".repeat(50));
            logger.info("Observações");
            logger.info("=".repeat(50));
        }
    }

    private void executarTreino(PlanoTreino plano) {
        logger.info("\n=== EXECUTANDO TREINO: " + plano.getNomePlano() + " ===");

        boolean houveAlteracoesNoPlano = false;

        for (SecaoTreino secao : plano.getSecoes()) {
            logger.log(Level.INFO, "\n--- INICIANDO SEÇÃO: {0} ---", secao.getNomeTreino());
            for (ItemPlanoTreino item : secao.getItensPlano()) {
                logger.log(Level.INFO, "\n--- Exercício: {0} ---", item.getExercicio().getNome());
                logger.log(Level.INFO, "Planejado: {0} séries x {1} repetições com {2} kg", new Object[]{item.getSeries(), item.getRepeticoes(), item.getCarga()});

                try {
                    logger.info("Quantas séries você fez? ");
                    int seriesRealizadas = Integer.parseInt(sc.nextLine());
                    logger.info("Quantas repetições por série? ");
                    int repeticoesRealizadas = Integer.parseInt(sc.nextLine());
                    logger.info("Qual carga você usou (kg)? ");
                    int cargaRealizada = Integer.parseInt(sc.nextLine());

                    boolean houveDiferenca = (item.getSeries() != seriesRealizadas) || (item.getRepeticoes() != repeticoesRealizadas) || (item.getCarga() != cargaRealizada);

                    if (houveDiferenca) {
                        logger.info("Performance diferente! Deseja atualizar o plano com os novos parâmetros? (s/n): ");
                        String resposta = sc.nextLine();

                        if (resposta.equalsIgnoreCase("s")) {
                            secaoTreinoBusiness.registrarPerformance(item, cargaRealizada, repeticoesRealizadas, seriesRealizadas);
                            houveAlteracoesNoPlano = true;
                            logger.info("Parâmetros do exercício atualizados.");
                        } else {
                            logger.info("Plano mantido sem alterações para este exercício.");
                        }
                    } else {
                        logger.info("Performance conforme o planejado!");
                    }
                } catch (NumberFormatException e) {
                    logger.info("Erro: Digite valores numéricos válidos.");
                }
            }
        }

        if (houveAlteracoesNoPlano) {
            logger.info("\nSalvando todas as alterações no plano de treino...");
            planoTreinoBusiness.modificarPlanoDeTreino(plano);
        }

        logger.info("\nSeção de treino concluída!");
    }
}