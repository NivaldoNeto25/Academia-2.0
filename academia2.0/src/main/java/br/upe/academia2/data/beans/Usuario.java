package br.upe.academia2.data.beans;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuarios")
public abstract class Usuario {
    @Id
    @Column(nullable = false, unique = true)
    private String email;

    private String nome;
    private String telefone;
    private String senha;
    private Double pesoAtual;
    private Double alturaAtual;
    private Double percGorduraAtual;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoTreino> planTreinos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndicadorBiomedico> indicaBio = new ArrayList<>();

    protected Usuario(String nome, String telefone, String email, String senha, Double pesoAtual, Double alturaAtual, Double percGorduraAtual) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.pesoAtual = pesoAtual;
        this.alturaAtual = alturaAtual;
        this.percGorduraAtual = percGorduraAtual;
    }

    protected Usuario() {}

    public void addPlanoTreino(PlanoTreino plano) {
        if (this.planTreinos == null) {
            this.planTreinos = new ArrayList<>();
        }
        plano.setUsuario(this);
        this.planTreinos.add(plano);
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Double getPesoAtual() { return pesoAtual; }
    public void setPesoAtual(Double pesoAtual) { this.pesoAtual = pesoAtual; }
    public Double getAlturaAtual() { return alturaAtual; }
    public void setAlturaAtual(Double alturaAtual) { this.alturaAtual = alturaAtual; }
    public Double getPercGorduraAtual() { return percGorduraAtual; }
    public void setPercGorduraAtual(Double percGorduraAtual) { this.percGorduraAtual = percGorduraAtual; }
    public List<PlanoTreino> getPlanTreinos() { return planTreinos; }
    public void setPlanTreinos(List<PlanoTreino> planTreinos) { this.planTreinos = planTreinos; }
    public List<IndicadorBiomedico> getIndicaBio() { return indicaBio; }
    public void setIndicaBio(List<IndicadorBiomedico> indicaBio) { this.indicaBio = indicaBio; }
}
