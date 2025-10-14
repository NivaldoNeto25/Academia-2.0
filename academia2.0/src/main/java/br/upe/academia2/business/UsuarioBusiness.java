package br.upe.academia2.business;

import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UsuarioBusiness {

    private final UsuarioCsvRepository usuarioRepository = UsuarioCsvRepository.getInstance();
    private Logger logger = Logger.getLogger(UsuarioBusiness.class.getName());

    public enum ResultadoExclusao {
        SUCESSO,
        NAO_ENCONTRADO,
        NAO_PERMITIDO_ADM
    }

    public UsuarioBusiness() {}

    public String autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            if (usuario instanceof Adm) {
                return "ADM";
            } else if (usuario instanceof Comum) {
                return "COMUM";
            }
        }
        return null;
    }

    public void cadastrarUsuario(Usuario usuario) {
        usuarioRepository.create(usuario);
        logger.info("Usuário cadastrado com sucesso!");
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listarTodos();
    }

    public List<Comum> listarUsuariosComuns() {
        List<Comum> comuns = new ArrayList<>();
        for (Usuario u : usuarioRepository.listarTodos()) {
            if (u instanceof Comum comum) {
                comuns.add(comum);
            }
        }
        return comuns;
    }

    public ResultadoExclusao deletarUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            return ResultadoExclusao.NAO_ENCONTRADO;
        }
        if (usuario instanceof Adm) {
            return ResultadoExclusao.NAO_PERMITIDO_ADM;
        }
        boolean deletado = usuarioRepository.delete(email);
        if (deletado) {
            logger.info("Usuário removido com sucesso!");
            return ResultadoExclusao.SUCESSO;
        } else {
            logger.warning("Falha ao remover usuário.");
            return ResultadoExclusao.NAO_ENCONTRADO;
        }
    }

    public void atualizarUsuario(Usuario usuario) {
        Usuario atualizado = usuarioRepository.update(usuario);
        if (atualizado != null) {
            logger.info("Dados do usuário atualizados com sucesso!");
        } else {
            logger.warning("Falha ao atualizar: usuário não encontrado.");
        }
    }

    public void salvarAlteracoesNoCsv() {
        usuarioRepository.persistirNoCsv();
        logger.info("Dados de usuario foram salvos no CSV."); //debug
    }

}