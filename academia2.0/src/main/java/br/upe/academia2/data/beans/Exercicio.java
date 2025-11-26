package br.upe.academia2.data.beans;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "exercicios")
public class Exercicio {
    @Id
    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;
    private String caminhoGif;

    public Exercicio(String nome, String descricao, String caminhoGif) {
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoGif = caminhoGif;
    }
    public Exercicio() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCaminhoGif() { return caminhoGif; }
    public void setCaminhoGif(String caminhoGif) { this.caminhoGif = caminhoGif; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercicio exercicio = (Exercicio) o;
        return Objects.equals(nome, exercicio.nome);
    }
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
