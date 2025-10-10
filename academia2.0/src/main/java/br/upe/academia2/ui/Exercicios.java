package br.upe.academia2.ui;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import br.upe.academia2.business.ExercicioBusiness;
import br.upe.academia2.data.beans.Exercicio;

public class Exercicios {
    private final Scanner sc = new Scanner(System.in);
    private final ExercicioBusiness exercicioBusiness = new ExercicioBusiness();
    private static final Logger logger = Logger.getLogger(Exercicios.class.getName());

    public void exibirMenuExercicios() {
        boolean sair = false;

        while (!sair) {
            logger.info("=".repeat(20));
            logger.info("MENU EXERCÍCIOS");
            logger.info("=".repeat(20));
            logger.info("1 - Cadastrar exercício");
            logger.info("2 - Listar exercícios");
            logger.info("3 - Excluir exercício");
            logger.info("4 - Modificar exercício");
            logger.info("5 - Sair");

            logger.info("Escolha uma opção: ");  // User input prompts better remain on System.out

            try {
                int opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1:
                        cadastrarNovoExercicio();
                        break;
                    case 2:
                        listarTodosExercicios();
                        break;
                    case 3:
                        excluirExercicio();
                        break;
                    case 4:
                        atualizarExercicioExistente();
                        break;
                    case 5:
                        logger.info("Saindo...");
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

    private void listarTodosExercicios() {
        logger.info("\n=== LISTA DE EXERCÍCIOS ===");

        List<Exercicio> exercicios = exercicioBusiness.listarExercicios();

        if (exercicios.isEmpty()) {
            logger.info("Nenhum exercício cadastrado.");
            return;
        }

        for (int i = 0; i < exercicios.size(); i++) {
            Exercicio ex = exercicios.get(i);
            logger.info((i + 1) + ". " + ex.getNome());
            logger.info("Descrição: " + ex.getDescricao());
            if (ex.getCaminhoGif() != null && !ex.getCaminhoGif().trim().isEmpty()) {
                logger.info("Arquivo GIF: " + ex.getCaminhoGif());
            }
            logger.info("");
        }
    }

    private void cadastrarNovoExercicio() {
        logger.info("\n=== CADASTRAR EXERCÍCIO FÍSICO ===");

        try {
            logger.info("Nome do exercício: ");
            String nome = sc.nextLine();

            logger.info("Descrição detalhada: ");
            String descricao = sc.nextLine();

            logger.info("Caminho do arquivo GIF: ");
            String caminhoGif = sc.nextLine();

            Exercicio novoExercicio = new Exercicio(nome, descricao, caminhoGif);
            exercicioBusiness.salvar(novoExercicio);
            logger.info("Exercício cadastrado com sucesso!");

        } catch (Exception e) {
            logger.info("Erro ao cadastrar exercício: " + e.getMessage());
        }
    }

    private void atualizarExercicioExistente() {
        logger.info("\n=== ATUALIZAR EXERCÍCIO ===");

        logger.info("Digite o nome do exercício a ser atualizado: ");
        String nome = sc.nextLine().trim();

        if (nome.isEmpty()) {
            logger.info("Nome não pode ser vazio!");
            return;
        }

        Exercicio exercicioExistente = exercicioBusiness.buscarExercicioPorNome(nome);

        if (exercicioExistente == null) {
            logger.info("Exercício não encontrado: '" + nome + "'");
            return;
        }

        logger.info("\nDigite os novos dados (deixe vazio para manter o atual):");

        logger.info("Nova descrição: ");
        String novaDescricao = sc.nextLine().trim();

        logger.info("Novo caminho do GIF: ");
        String novoCaminhoGif = sc.nextLine().trim();

        boolean houveMudanca = false;

        if (!novaDescricao.isEmpty()) {
            exercicioExistente.setDescricao(novaDescricao);
            houveMudanca = true;
        }

        if (!novoCaminhoGif.isEmpty()) {
            exercicioExistente.setCaminhoGif(novoCaminhoGif);
            houveMudanca = true;
        }

        if (houveMudanca) {
            exercicioBusiness.atualizarExercicio(exercicioExistente);
            logger.info("Exercício atualizado com sucesso!");
        } else {
            logger.info("Nenhuma alteração foi feita.");
        }
    }

    private void excluirExercicio() {
        logger.info("\n=== EXCLUIR EXERCÍCIO ===");

        logger.info("Digite o nome do exercício a ser excluído: ");
        String nome = sc.nextLine().trim();

        if (nome.isEmpty()) {
            logger.info("Nome não pode ser vazio.");
            return;
        }

        Exercicio exercicio = exercicioBusiness.buscarExercicioPorNome(nome);

        if (exercicio == null) {
            logger.info("Exercício não encontrado: " + nome);
            return;
        }

        logger.info("\nExercício encontrado:");
        logger.info("Nome: " + exercicio.getNome());
        logger.info("Descrição: " + exercicio.getDescricao());

        exercicioBusiness.deletarExercicio(nome);
        logger.info("Exercício excluído com sucesso!");
    }
}