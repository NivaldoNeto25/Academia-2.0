package br.upe.academia2.data.beans;

import jakarta.persistence.*;

@Entity
@Table(name = "adm")
public class Adm extends Usuario {
    public Adm(String nome, String telefone, String email, String senha, Double pesoAtual, Double alturaAtual, Double percGorduraAtual) {
        super(nome, telefone, email, senha, pesoAtual, alturaAtual, percGorduraAtual);
    }
    public Adm() { super(); }
}
