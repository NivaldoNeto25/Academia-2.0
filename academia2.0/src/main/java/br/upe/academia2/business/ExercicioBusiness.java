package br.upe.academia2.business;

import br.upe.academia2.data.beans.Exercicio;
import br.upe.academia2.data.repository.ExercicioJpaRepository;
import java.util.InputMismatchException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExercicioBusiness {
    private final ExercicioJpaRepository exercicioRepository = new ExercicioJpaRepository();
    private final Logger logger = Logger.getLogger(ExercicioBusiness.class.getName());

    public void salvar(Exercicio exercicio) {
        if (exercicio == null || exercicio.getNome() == null || exercicio.getNome().trim().isEmpty()) {
            logger.log(Level.WARNING, "Exercício inválido para cadastro.");
            return;
        }
        Exercicio existente = exercicioRepository.findByNome(exercicio.getNome());
        if (existente != null) {
            logger.log(Level.WARNING,"Exercício com este nome já existe.");
            return;
        }
        exercicioRepository.create(exercicio);
        logger.info("Exercício '" + exercicio.getNome() + "' cadastrado com sucesso!");
    }

    public List<Exercicio> listarExercicios() {
        return exercicioRepository.findAll();
    }

    public Exercicio buscarExercicioPorNome(String nome) {
        return exercicioRepository.findByNome(nome);
    }

    public void atualizarExercicio(Exercicio exercicio) {
        try {
            if(exercicio == null) throw new InputMismatchException();
            exercicioRepository.update(exercicio);
            logger.info("Exercício atualizado com sucesso!");
        } catch (InputMismatchException ime){
            logger.warning("Algum campo ficou em branco, tente novamente");
        } catch (Exception e) {
            logger.warning("Algo deu errado. Por favor, tente novamente");
        }
    }

    public void deletarExercicio(String nome) {
        try {
            if (nome == null || nome.isBlank()) throw new InputMismatchException();
            if (exercicioRepository.delete(nome))
                logger.info("Exercício deletado com sucesso!");
            else
                logger.warning("Exercício não encontrado para deletar.");
        } catch (InputMismatchException ime) {
            logger.warning("nome vazio, por favor escreva novamente");
        } catch (Exception e) {
            logger.warning("Algo deu errado. Por favor, tente novamente");
        }
    }
}
