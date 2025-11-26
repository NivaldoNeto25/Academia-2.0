package br.upe.academia2.data.beans;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "secoes_treino")
public class SecaoTreino {
    @Id
    private String id;
    private String nomeTreino;

    @ManyToOne
    @JoinColumn(name = "plano_treino_id")
    private PlanoTreino planoTreino;

    @OneToMany(mappedBy = "secaoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPlanoTreino> itensPlano = new ArrayList<>();

    public SecaoTreino(String id, String nomeTreino, PlanoTreino planoTreino){
        this.id = id;
        this.nomeTreino = nomeTreino;
        this.planoTreino = planoTreino;
        this.itensPlano = new ArrayList<>();
    }
    public SecaoTreino() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNomeTreino() { return nomeTreino; }
    public void setNomeTreino(String nomeTreino) { this.nomeTreino = nomeTreino; }
    public PlanoTreino getPlanoTreino() { return planoTreino; }
    public void setPlanoTreino(PlanoTreino planoTreino) { this.planoTreino = planoTreino; }
    public List<ItemPlanoTreino> getItensPlano() { return itensPlano; }
    public void setItensPlano(List<ItemPlanoTreino> itensPlano) { this.itensPlano = itensPlano; }
    public void addItemSecao(ItemPlanoTreino item) { this.itensPlano.add(item); }
}
