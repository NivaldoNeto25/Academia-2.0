package br.upe.academia2.business;

import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.UsuarioCsvRepository;

import java.util.ArrayList;
import java.util.List;

public class UsuarioBusiness {

    private UsuarioCsvRepository usuarioRepository;

    public UsuarioBusiness() {
        this.usuarioRepository = new UsuarioCsvRepository();
    }

    public String autenticar(String email, String senha) {
        usuarioRepository.persistirNoCsv();
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
        usuarioRepository.persistirNoCsv();
        
        usuarioRepository.create(usuario);
        System.out.println("Usuário cadastrado com sucesso!");
    }

    public List<Usuario> listarUsuarios() {
        usuarioRepository.persistirNoCsv();
        
        return usuarioRepository.listarTodos();
    }

    public List<Comum> listarUsuariosComuns() {
        usuarioRepository.persistirNoCsv();
       
        List<Comum> comuns = new ArrayList<>();
        for (Usuario u : usuarioRepository.listarTodos()) {
            if (u instanceof Comum) {
                comuns.add((Comum) u);
            }
        }
        return comuns;
    }

    public void deletarUsuario(String email) {
        usuarioRepository.persistirNoCsv();

        boolean deletado = usuarioRepository.delete(email);
        if (deletado) {
            System.out.println("Usuário removido com sucesso!");
        } else {
            System.out.println("Usuário não encontrado.");
        }
    }
    
    public void atualizarUsuario(Usuario usuario) {
        usuarioRepository.persistirNoCsv();
        
        Usuario atualizado = usuarioRepository.update(usuario);
        if (atualizado != null) {
            System.out.println("Dados do usuário atualizados com sucesso!");
        } else {
            System.out.println("Falha ao atualizar: usuário não encontrado.");
        }
    }
}