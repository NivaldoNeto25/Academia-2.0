package br.upe.academia2.ui;

import br.upe.academia2.data.beans.*;
import br.upe.academia2.business.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

public class PlanosDeTreino {

    private UsuarioBusiness usuarioBusiness;
    private ExercicioBusiness exercicioBusiness;
    private PlanoTreinoBusiness planoTreinoBusiness;
    private SecaoTreinoBusiness secaoTreinoBusiness;
    private Scanner sc;
    private Usuario usuarioLogado;
    private static final Logger logger = Logger.getLogger(PlanosDeTreino.class.getName());

    public PlanosDeTreino(Usuario usuarioLogado) {
        this.usuarioBusiness = new UsuarioBusiness();
        this.exercicioBusiness = new ExercicioBusiness();
        this.planoTreinoBusiness = new PlanoTreinoBusiness();
        this.secaoTreinoBusiness = new SecaoTreinoBusiness();
        this.sc = new Scanner(System.in);
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenuPlanosDeTreino() {
        boolean sair = false;

        while (!sair) {
            logger.info("=".repeat(20));
            logger.info("PLANO DE TREINO");
            logger.info("=".repeat(20));
            logger.info("1 - Cadastrar plano de treino");
            logger.info("2 - Listar plano de treino");
            logger.info("3 - Modificar plano de treino");
            logger.info("4 - Seção Treino");
            logger.info("5 - Sair");
            System.out.print("Escolha uma opção: ");

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
                        logger.info("Opção inválida! Tente novamente");
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
            logger.info("\n--- Modificando Plano: " + plano.getNomePlano() + " ---");
            planoTreinoBusiness.exibirPlanoDeTreino(plano); // Mostra o estado atual do plano
            logger.info("\nOpções de Modificação:");
            logger.info("1 - Alterar nome do plano");
            logger.info("2 - Alterar datas");
            logger.info("3 - Adicionar exercício a uma seção");
            logger.info("4 - Modificar exercício existente");
            logger.info("5 - Remover exercício de uma seção");
            logger.info("6 - Salvar e Finalizar modificações");
            System.out.print("Escolha uma opção: ");

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
                        logger.info("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                logger.info("Erro: Digite um número válido.");
            }
        }
    }

    private void alterarNomePlano(PlanoTreino plano) {
        System.out.print("Novo nome do plano (atual: " + plano.getNomePlano() + "): ");
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            System.out.print("Nova data de início (dd/MM/yyyy) - atual: " + sdf.format(plano.getInicioPlano()) + ": ");
            String dataInicioStr = sc.nextLine();

            System.out.print("Nova data de fim (dd/MM/yyyy) - atual: " + sdf.format(plano.getFimPlano()) + ": ");
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
            logger.info("Erro: Formato de data inválido. Use dd/MM/yyyy");
        }
    }

    private void adicionarExercicioAoPlano(PlanoTreino plano) {
        logger.info("\n=== ADICIONAR EXERCÍCIO ===");
        coletarExerciciosParaPlano(plano);
    }

    private void coletarExerciciosParaPlano(PlanoTreino plano) {
        boolean continuar = true;

        while (continuar) {
            System.out.print("\nNome da Seção (ex: Treino A - Peito e Tríceps): ");
            String nomeSecao = sc.nextLine();
            SecaoTreino secao = plano.getOuCriarSecao(nomeSecao);

            System.out.print("Nome do exercício a adicionar nesta seção: ");
            String nomeExercicio = sc.nextLine();

            Exercicio exercicio = exercicioBusiness.buscarExercicioPorNome(nomeExercicio);

            if (exercicio == null) {
                logger.info("Exercício não encontrado! Cadastre-o primeiro no menu de exercícios.");
            } else {
                try {
                    System.out.print("Número de séries: ");
                    int series = Integer.parseInt(sc.nextLine());
                    System.out.print("Número de repetições: ");
                    int repeticoes = Integer.parseInt(sc.nextLine());
                    System.out.print("Carga (kg): ");
                    int carga = Integer.parseInt(sc.nextLine());

                    ItemPlanoTreino item = new ItemPlanoTreino(exercicio, series, repeticoes, carga);
                    secao.addItemSecao(item);
                    logger.info("'" + nomeExercicio + "' adicionado à seção '" + nomeSecao + "'.");

                } catch (NumberFormatException e) {
                    logger.info("Erro: Digite valores numéricos válidos para séries, repetições e carga.");
                }
            }
            System.out.print("Deseja adicionar outro exercício a este plano? (s/n): ");
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
            logger.info((i + 1) + ". " + item.getExercicio().getNome() +
                    " (" + item.getSeries() + "x" + item.getRepeticoes() +
                    ", " + item.getCarga() + "kg)");
        }

        try {
            System.out.print("Escolha o exercício para modificar (número): ");
            int escolha = sc.nextInt() - 1;
            sc.nextLine();

            if (escolha >= 0 && escolha < plano.getItens().size()) {
                ItemPlanoTreino item = plano.getItens().get(escolha);

                System.out.print("Novas séries (atual: " + item.getSeries() + "): ");
                int novasSeries = sc.nextInt();

                System.out.print("Novas repetições (atual: " + item.getRepeticoes() + "): ");
                int novasRepeticoes = sc.nextInt();

                System.out.print("Nova carga (atual: " + item.getCarga() + "kg): ");
                int novaCarga = sc.nextInt();
                sc.nextLine();

                item.setSeries(novasSeries);
                item.setRepeticoes(novasRepeticoes);
                item.setCarga(novaCarga);

                logger.info("Exercício modificado com sucesso!");

            } else {
                logger.info("Opção inválida!");
            }
        } catch (InputMismatchException e) {
            logger.info("Erro: Digite um número válido.");
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
            logger.info((i + 1) + ". " + item.getExercicio().getNome());
        }

        try {
            System.out.print("Escolha o exercício para remover (número): ");
            int escolha = sc.nextInt() - 1;
            sc.nextLine();

            if (escolha >= 0 && escolha < plano.getItens().size()) {
                ItemPlanoTreino itemRemovido = plano.getItens().remove(escolha);
                logger.info("Exercício '" + itemRemovido.getExercicio().getNome() + "' removido com sucesso!");
            } else {
                logger.info("Opção inválida!");
            }
        } catch (InputMismatchException e) {
            logger.info("Erro: Digite um número válido.");
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
            System.out.print("Nome do plano: ");
            String nomePlano = sc.nextLine();

            System.out.print("Data de início (dd/MM/yyyy): ");
            String dataInicioStr = sc.nextLine();

            System.out.print("Data de fim (dd/MM/yyyy): ");
            String dataFimStr = sc.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dataInicio = sdf.parse(dataInicioStr);
            Date dataFim = sdf.parse(dataFimStr);

            PlanoTreino novoPlano = new PlanoTreino(0, nomePlano, dataInicio, dataFim, usuarioLogado);

            coletarExerciciosParaPlano(novoPlano);

            planoTreinoBusiness.cadastrarPlanoDeTreino(usuarioLogado, novoPlano);

        } catch (ParseException e) {
            logger.info("Erro: Formato de data inválido. Use dd/MM/yyyy");
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
        System.out.print("Escolha uma opção: ");

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
                    logger.info("Opção inválida!");
            }
        } catch (InputMismatchException e) {
            logger.info("Erro: Digite um número válido.");
            sc.nextLine();
        }
    }

    private void exibirSecaoComoCartao(PlanoTreino plano) {
        logger.info("\n" + "=".repeat(50));
        logger.info("CARTÃO DE TREINO");
        logger.info("=".repeat(50));
        logger.info("Plano: " + plano.getNomePlano());
        logger.info("Usuário: " + usuarioLogado.getNome());
        logger.info("Data: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        logger.info("=".repeat(50));

        for (int i = 0; i < plano.getItens().size(); i++) {
            ItemPlanoTreino item = plano.getItens().get(i);
            logger.info((i + 1) + ". " + item.getExercicio().getNome());
            logger.info("Séries: " + item.getSeries());
            logger.info("Repetições: " + item.getRepeticoes());
            logger.info("Carga: " + item.getCarga() + " kg");
            logger.info("Descrição: " + item.getExercicio().getDescricao());
            logger.info("");
        }

        logger.info("=".repeat(50));
        logger.info("Observações");
        logger.info("=".repeat(50));
    }

    private void executarTreino(PlanoTreino plano) {
        logger.info("\n=== EXECUTANDO TREINO: " + plano.getNomePlano() + " ===");

        boolean houveAlteracoesNoPlano = false;

        for (SecaoTreino secao : plano.getSecoes()) {
            logger.info("\n--- INICIANDO SEÇÃO: " + secao.getNomeTreino() + " ---");
            for (ItemPlanoTreino item : secao.getItensPlano()) {
                logger.info("\n--- Exercício: " + item.getExercicio().getNome() + " ---");
                logger.info("Planejado: " + item.getSeries() + " séries x " + item.getRepeticoes() + " repetições com " + item.getCarga() + " kg");

                try {
                    System.out.print("Quantas séries você fez? ");
                    int seriesRealizadas = Integer.parseInt(sc.nextLine());
                    System.out.print("Quantas repetições por série? ");
                    int repeticoesRealizadas = Integer.parseInt(sc.nextLine());
                    System.out.print("Qual carga você usou (kg)? ");
                    int cargaRealizada = Integer.parseInt(sc.nextLine());

                    boolean houveDiferenca = (item.getSeries() != seriesRealizadas) || (item.getRepeticoes() != repeticoesRealizadas) || (item.getCarga() != cargaRealizada);

                    if (houveDiferenca) {
                        System.out.print("Performance diferente! Deseja atualizar o plano com os novos parâmetros? (s/n): ");
                        String resposta = sc.nextLine();

                        if (resposta.equalsIgnoreCase("s")) {
                            secaoTreinoBusiness.registrarPerformance(item, cargaRealizada, repeticoesRealizadas, seriesRealizadas);
                            houveAlteracoesNoPlano = true; // Marca que o plano foi modificado.
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
            planoTreinoBusiness.modificarPlanoDeTreino(plano); // Salva o estado final do plano UMA ÚNICA VEZ.
        }

        logger.info("\nSeção de treino concluída!");
    }
}