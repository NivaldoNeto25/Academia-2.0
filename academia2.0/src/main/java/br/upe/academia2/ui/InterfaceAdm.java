package br.upe.academia2.ui;

import br.upe.academia2.business.UsuarioBusiness;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import java.util.logging.Level;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class InterfaceAdm {
    private final Scanner sc = new Scanner(System.in);
    private final Usuario adm;
    private final UsuarioBusiness usuarioBusiness = new UsuarioBusiness(UsuarioCsvRepository.getInstance());
    private static final Logger logger = Logger.getLogger(InterfaceAdm.class.getName());

    public InterfaceAdm(Usuario adm) {
        this.adm = adm;
    }

    public void exibirMenuAdm() {
        boolean sair = false;
        while (!sair) {
            if (logger.isLoggable(Level.INFO)){
            logger.info("=".repeat(20));
            logger.info("MENU ADMINISTRADOR");
            logger.info("=".repeat(20));
            logger.info("1 - Cadastrar alunos");
            logger.info("2 - Listar alunos");
            logger.info("3 - Modificar alunos");
            logger.info("4 - Excluir alunos");
            logger.info("5 - Sair");

            logger.info("Escolha uma opção: "); // prompt kept on System.out for immediate input
            }
            try {
                int opcao = Integer.parseInt(sc.nextLine());

                switch (opcao) {
                    case 1:
                        cadastrarAluno();
                        break;
                    case 2:
                        listarAlunos();
                        break;
                    case 3:
                        modificarAluno();
                        break;
                    case 4:
                        excluirAluno();
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
            }
        }
    }

    private void cadastrarAluno() {
        logger.info("\n--- Cadastro de Novo Aluno ---");
        logger.info("Nome: ");
        String nome = sc.nextLine();
        logger.info("Email: ");
        String email = sc.nextLine();
        logger.info("Senha: ");
        String senha = sc.nextLine();
        if (usuarioBusiness.listarUsuarios().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            logger.info("Erro: Já existe um aluno com esse email.");
            return;
        }

        Usuario novo = new Comum(nome, null, email, senha, null, null, null);
        usuarioBusiness.cadastrarUsuario(novo);
    }

    private void listarAlunos() {
        logger.info("\n--- Lista de Alunos ---");
        List<Comum> alunos = usuarioBusiness.listarUsuariosComuns();
        if (alunos.isEmpty()) {
            logger.info("Nenhum aluno cadastrado.");
        } else {
            for (Comum aluno : alunos) {
                logger.info("Nome: " + aluno.getNome() + " | Email: " + aluno.getEmail());
            }
        }
    }

    private void modificarAluno() {
        logger.info("\n--- Modificar Aluno ---");
        logger.info("Digite o email do aluno: ");
        String email = sc.nextLine();

        Usuario existente = usuarioBusiness.listarUsuarios()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            logger.info("Aluno não encontrado.");
            return;
        }

        logger.info("Novo nome (deixe vazio para manter): ");
        String nome = sc.nextLine();
        if (!nome.isBlank()) existente.setNome(nome);

        logger.info("Nova senha (deixe vazio para manter): ");
        String senha = sc.nextLine();
        if (!senha.isBlank()) existente.setSenha(senha);

        usuarioBusiness.atualizarUsuario(existente);
    }

    private void excluirAluno() {
        logger.info("\n--- Excluir Aluno ---");
        logger.info("Digite o email do aluno a ser removido: ");
        String email = sc.nextLine();

        if (this.adm.getEmail().equalsIgnoreCase(email)) {
            logger.info("Erro: Você não pode excluir sua própria conta de administrador.");
            return;
        }

        usuarioBusiness.deletarUsuario(email);
    }
}