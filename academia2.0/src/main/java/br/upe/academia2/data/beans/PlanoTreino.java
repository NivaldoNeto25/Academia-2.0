package br.upe.academia2.data.beans;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "planos_treino")
public class PlanoTreino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nomePlano;
    private Date inicioPlano;
    private Date fimPlano;

    @ManyToOne
    @JoinColumn(name = "usuario_email")
    private Usuario usuario;

    @OneToMany(mappedBy = "planoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecaoTreino> secoes = new ArrayList<>();

    public PlanoTreino(int id, String nomePlano, Date inicioPlano, Date fimPlano, Usuario usuario) {
        this.id = id;
        this.nomePlano = nomePlano;
        this.inicioPlano = inicioPlano;
        this.fimPlano = fimPlano;
        this.usuario = usuario;
        this.secoes = new ArrayList<>();
    }
    public PlanoTreino() {}

    public List<ItemPlanoTreino> getItens() {
        List<ItemPlanoTreino> todosOsItens = new ArrayList<>();
        if (this.secoes != null) {
            for (SecaoTreino secao : this.secoes) {
                todosOsItens.addAll(secao.getItensPlano());
            }
        }
        return todosOsItens;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomePlano() { return nomePlano; }
    public void setNomePlano(String nomePlano) { this.nomePlano = nomePlano; }
    public Date getInicioPlano() { return inicioPlano; }
    public void setInicioPlano(Date inicioPlano) { this.inicioPlano = inicioPlano; }
    public Date getFimPlano() { return fimPlano; }
    public void setFimPlano(Date fimPlano) { this.fimPlano = fimPlano; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<SecaoTreino> getSecoes() { return secoes; }
    public void setSecoes(List<SecaoTreino> secoes) { this.secoes = secoes; }

    public SecaoTreino getOuCriarSecao(String nomeSecao) {
        for (SecaoTreino secao : secoes) {
            if (secao.getNomeTreino().equalsIgnoreCase(nomeSecao)) {
                return secao;
            }
        }
        String novoId = "sec_" + System.currentTimeMillis();
        SecaoTreino nova = new SecaoTreino(novoId, nomeSecao, this);
        secoes.add(nova);
        return nova;
    }

    public SecaoTreino getSecaoPorNome(String nomeSecao) {
        for (SecaoTreino secao : secoes) {
            if (secao.getNomeTreino().equalsIgnoreCase(nomeSecao)) {
                return secao;
            }
        }
        return null;
    }
}
