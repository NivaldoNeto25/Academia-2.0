package br.upe.academia2.data.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SecaoTreino implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nomeTreino;
    private String descricao;
    private List<ItemPlanoTreino> itensPlano = new ArrayList<>();

    public SecaoTreino() {}

    public SecaoTreino(String id, String nomeTreino, String descricao) {
        this.id = id;
        this.nomeTreino = nomeTreino;
        this.descricao = descricao;
    }

    public SecaoTreino(String novoId, String nomeSecao, PlanoTreino planoTreino) {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNomeTreino() { return nomeTreino; }
    public void setNomeTreino(String nomeTreino) { this.nomeTreino = nomeTreino; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<ItemPlanoTreino> getItensPlano() {
        if (itensPlano == null) itensPlano = new ArrayList<>();
        return itensPlano;
    }
    public void setItensPlano(List<ItemPlanoTreino> itensPlano) {
        this.itensPlano = (itensPlano != null) ? itensPlano : new ArrayList<>();
    }
    public void addItemSecao(ItemPlanoTreino item) {
        if (itensPlano == null) itensPlano = new ArrayList<>();
        if (item != null) itensPlano.add(item);
    }

    // utilidades
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecaoTreino)) return false;
        SecaoTreino that = (SecaoTreino) o;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
    @Override public String toString() { return nomeTreino != null ? nomeTreino : "Seção"; }
}