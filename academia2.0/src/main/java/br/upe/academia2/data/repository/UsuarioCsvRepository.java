package br.upe.academia2.data.repository;

import br.upe.academia2.data.beans.Adm;
import br.upe.academia2.data.beans.Comum;
import br.upe.academia2.data.beans.Usuario;
import br.upe.academia2.data.repository.interfaces.IUsuarioRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioCsvRepository implements IUsuarioRepository {

    private static final Logger logger = Logger.getLogger(UsuarioCsvRepository.class.getName());
    private final String filePath;
    private static final String CSV_HEADER = "tipo,email,nome,senha,telefone,peso,altura,gordura";
    private List<Usuario> usuarios;

    public UsuarioCsvRepository() {
        this.filePath = obterCaminhoCsv();
        criarDiretorioSeNecessario();
        this.usuarios = new ArrayList<>();
        carregarDoCsv();
    }

    public UsuarioCsvRepository(String caminhoPersonalizado) {
        this.filePath = caminhoPersonalizado;
        criarDiretorioSeNecessario();
        this.usuarios = new ArrayList<>();
        carregarDoCsv();
    }

    private String obterCaminhoCsv() {
        String basePath = System.getProperty("user.dir");
        String relativePath = "/data/usuarios.csv";
        return basePath + relativePath;
    }

    private void criarDiretorioSeNecessario() {
        File file = new File(this.filePath);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            boolean criada = parent.mkdirs();
            if (criada) {
                System.out.println(" Pasta 'data/' criada: " + parent.getAbsolutePath()); // unchanged
            } else {
                logger.severe(" Falha ao criar pasta 'data/': " + parent.getAbsolutePath()); // replaces System.err
            }
        }
    }

    @Override
    public Usuario create(Usuario usuario) {
        if (findByEmail(usuario.getEmail()) == null) {
            this.usuarios.add(usuario);
            persistirNoCsv();
            System.out.println(" Usuário criado: " + usuario.getEmail()); // unchanged
            return usuario;
        }
        System.out.println(" Usuário já existe: " + usuario.getEmail()); // unchanged
        return null;
    }

    @Override
    public Usuario findByEmail(String email) {
        return this.usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Usuario update(Usuario usuario) {
        this.usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()));
        this.usuarios.add(usuario);
        persistirNoCsv();
        System.out.println(" Usuário atualizado: " + usuario.getEmail()); // unchanged
        return usuario;
    }

    @Override
    public boolean delete(String email) {
        boolean removido = this.usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
        if (removido) {
            persistirNoCsv();
            System.out.println(" Usuário removido: " + email); // unchanged
        } else {
            System.out.println(" Usuário não encontrado para remoção: " + email); // unchanged
        }
        return removido;
    }

    @Override
    public List<Usuario> listarTodos() {
        System.out.println(" Listando usuários... Total: " + this.usuarios.size()); // unchanged
        for (Usuario u : this.usuarios) {
            System.out.println("   - " + u.getEmail() + " (" + u.getNome() + ")"); // unchanged
        }
        return new ArrayList<>(this.usuarios);
    }

    public void persistirNoCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            for (Usuario u : this.usuarios) {
                String tipo = (u instanceof Adm) ? "ADM" : "COMUM";
                String linha = String.join(",",
                        tipo,
                        u.getEmail(),
                        u.getNome(),
                        u.getSenha(),
                        u.getTelefone() != null ? u.getTelefone() : "",
                        u.getPesoAtual() != null ? u.getPesoAtual().toString() : "",
                        u.getAlturaAtual() != null ? u.getAlturaAtual().toString() : "",
                        u.getPercGorduraAtual() != null ? u.getPercGorduraAtual().toString() : ""
                );
                writer.write(linha);
                writer.newLine();
            }
            System.out.println("CSV salvo com sucesso: " + this.filePath); // unchanged
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar CSV: " + e.getMessage(), e); // replaces System.err
        }
    }

    public void carregarDoCsv() {
        File file = new File(this.filePath);
        if (!file.exists()) {
            System.out.println(" Arquivo CSV não encontrado. Criando novo."); // unchanged
            persistirNoCsv();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
            @SuppressWarnings("unused")
            String header = reader.readLine();
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",", -1);

                if (dados.length < 4) continue;

                Usuario usuario = "ADM".equals(dados[0]) ? new Adm() : new Comum();

                usuario.setEmail(dados[1]);
                usuario.setNome(dados[2]);
                usuario.setSenha(dados[3]);

                if (dados.length > 4 && !dados[4].isEmpty()) usuario.setTelefone(dados[4]);
                if (dados.length > 5 && !dados[5].isEmpty()) usuario.setPesoAtual(Double.parseDouble(dados[5]));
                if (dados.length > 6 && !dados[6].isEmpty()) usuario.setAlturaAtual(Double.parseDouble(dados[6]));
                if (dados.length > 7 && !dados[7].isEmpty()) usuario.setPercGorduraAtual(Double.parseDouble(dados[7]));

                this.usuarios.add(usuario);

            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, " Erro ao carregar CSV: " + e.getMessage(), e); // replaces System.err
        }
    }
}