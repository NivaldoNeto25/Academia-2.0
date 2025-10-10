package br.upe.academia2.ui;

import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;

import java.util.Scanner;
import java.util.logging.Logger;

public class InterfaceAluno {
    private final Scanner sc = new Scanner(System.in);
    private Usuario aluno;
    private Exercicios exercicios = new Exercicios();
    private InterfaceBiomedico indicadores = new InterfaceBiomedico();
    private PlanosDeTreino planoTreino;
    private Relatorios relatorios = new Relatorios();
    private static final Logger logger = Logger.getLogger(InterfaceAluno.class.getName());

    public InterfaceAluno(Usuario aluno) {
        this.aluno = new Comum(
                aluno.getNome(),
                aluno.getTelefone(),
                aluno.getEmail(),
                aluno.getSenha(),
                aluno.getPesoAtual(),
                aluno.getAlturaAtual(),
                aluno.getPercGorduraAtual()
        );
        this.planoTreino = new PlanosDeTreino(this.aluno);
    }

    public void exibirMenuAlunos() {
        boolean sair = false;

        while (!sair) {
            logger.info("=".repeat(20));
            logger.info("MENU DO ALUNO");
            logger.info("=".repeat(20));
            logger.info("1 - Exercícios");
            logger.info("2 - Indicadores");
            logger.info("3 - Plano de treino");
            logger.info("4 - Relatório");
            logger.info("5 - Sair");
            logger.info("Escolha uma opção: ");

            try {
                int opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1:
                        exercicios.exibirMenuExercicios();
                        break;
                    case 2:
                        indicadores.exibirMenuIndicadores(aluno);
                        break;
                    case 3:
                        planoTreino.exibirMenuPlanosDeTreino();
                        break;
                    case 4:
                        relatorios.exibirMenuRelatorios(aluno);
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
}