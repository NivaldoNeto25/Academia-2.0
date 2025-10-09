package br.upe.academia2.ui;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

public class InterfacePrincipal {

    private final Scanner sc;
    private static UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
    private static UsuarioCsvRepository usuarioCsvRepository = new UsuarioCsvRepository();
    public static String email;
    private static final Logger logger = Logger.getLogger(InterfacePrincipal.class.getName());

    public InterfacePrincipal(Scanner scGlobal) {
        this.sc = scGlobal;
    }

    public void exibirMenuPrincipal() {
        boolean sair = false;
        while (!sair) {
            logger.info("=".repeat(20));
            logger.info("BEM-VINDO");
            logger.info("=".repeat(20));
            logger.info("1 - Entrar");
            logger.info("2 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = sc.nextInt();
                sc.nextLine();

                switch (opcao) {
                    case 1:
                        realizarLogin();
                        break;
                    case 2:
                        logger.info("Saindo...");
                        sair = true;
                        break;
                    default:
                        logger.info("Opção inválida! Tente novamente");
                }
            } catch (InputMismatchException e) {
                logger.info("Erro: Entrada inválida!");
                sc.nextLine();
            }
        }
    }

    private void realizarLogin() {
        System.out.print("Email: ");
        email = sc.nextLine();

        System.out.print("Senha: ");
        String senha = sc.nextLine();

        String tipoUsuario = usuarioBusiness.autenticar(email, senha);

        if (tipoUsuario != null) {
            Usuario usuarioLogado = usuarioCsvRepository.findByEmail(email);

            if (usuarioLogado == null) {
                logger.severe("Erro crítico: usuário autenticado mas não encontrado. Contate o suporte.");
                return;
            }

            logger.info("\nLogin realizado com sucesso!");

            if ("ADM".equals(tipoUsuario)) {
                InterfaceAdm interfaceAdm = new InterfaceAdm(usuarioLogado);
                interfaceAdm.exibirMenuAdm();
            } else if ("COMUM".equals(tipoUsuario)) {
                InterfaceAluno interfaceAluno = new InterfaceAluno(usuarioLogado);
                interfaceAluno.exibirMenuAlunos();
            }
        } else {
            logger.info("E-mail ou senha inválidos! Tente novamente.");
        }
    }
}