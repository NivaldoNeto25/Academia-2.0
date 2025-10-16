package br.upe.academia2.business;

import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.repository.ExercicioRepoImpl;

import java.util.InputMismatchException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExercicioBusiness {
    private ExercicioRepoImpl exercicioRepository;

    private Logger logger = Logger.getLogger(ExercicioBusiness.class.getName());

    public ExercicioBusiness() {
        this.exercicioRepository = new ExercicioRepoImpl();
    }

    public void salvar(Exercicio exercicio) {
        exercicioRepository.persistirNoCsv();

        if (exercicio == null || exercicio.getNome() == null || exercicio.getNome().trim().isEmpty()) {
            logger.log(Level.WARNING, "Exercício inválido para cadastro.");
            return;
        }

        Exercicio existente = exercicioRepository.findByNome(exercicio.getNome());
        if (existente != null) {
            logger.log(Level.WARNING,"Exercício com este nome já existe.");
            return;
        }

        Exercicio exercicioCriado = exercicioRepository.create(exercicio);
        if (exercicioCriado != null) {
            logger.info("Exercício '" + exercicio.getNome() + "' cadastrado com sucesso!");
        } else {
            logger.warning("Erro ao cadastrar exercício.");
        }
    }

    public List<Exercicio> listarExercicios() {

        return exercicioRepository.findAll();
    }

    public Exercicio buscarExercicioPorNome(String nome) {
        
        exercicioRepository.carregarDoCsv();

        return exercicioRepository.findByNome(nome);
    }

    public void atualizarExercicio(Exercicio exercicio) {
        
        exercicioRepository.carregarDoCsv();
        try {
            if(exercicio == null){
                throw new InputMismatchException();
            }
            exercicioRepository.update(exercicio);
        } catch (InputMismatchException ime){
            logger.warning("Algum campo acabou ficando em branco, tente novamente");
        } catch (Exception e) {
            logger.warning("Algo deu errado. Por favor, tente novamente");
        }
    }

    public void deletarExercicio(String nome) {
        try {
            if (nome.isEmpty() || nome.equals(" ")) {
                throw new InputMismatchException();
            }
            exercicioRepository.delete(nome);
        } catch (InputMismatchException ime) {
            logger.warning("nome vazio, por favor escreva novamente");
        } catch (Exception e) {
            logger.warning("Algo deu errado. Por favor, tente novamente");
        }
    }
        // eu preciso pra salvar as coisas assim q clicar em atualizar / cadastrar
        public void salvarAlteracoesNoCsv(){
            exercicioRepository.persistirNoCsv();
            logger.info("Dados de exercícios foram salvos no CSV.");
        }
    }
