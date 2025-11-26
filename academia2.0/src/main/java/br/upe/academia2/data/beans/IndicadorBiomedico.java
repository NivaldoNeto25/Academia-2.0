package br.upe.academia2.data.beans;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "indicadores_biomedicos")
public class IndicadorBiomedico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_email")
    private Usuario usuario;

    private Double peso;
    private Double altura;
    private Double percentualGordura;
    private Double percentualMassaMagra;
    private Double imc;
    private Date dataRegistro;
    private Date dataRegistroOriginal;

    public IndicadorBiomedico(Double peso, Double altura, Double percentualGordura, Double percentualMassaMagra, Double imc, Date dataRegistro) {
        this.peso = peso;
        this.altura = altura;
        this.percentualGordura = percentualGordura;
        this.percentualMassaMagra = percentualMassaMagra;
        this.imc = imc;
        this.dataRegistro = dataRegistro;
        this.dataRegistroOriginal = dataRegistro;
    }
    public IndicadorBiomedico() {}

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }
    public Double getPercentualGordura() { return percentualGordura; }
    public void setPercentualGordura(Double percentualGordura) { this.percentualGordura = percentualGordura; }
    public Double getPercentualMassaMagra() { return percentualMassaMagra; }
    public void setPercentualMassaMagra(Double percentualMassaMagra) { this.percentualMassaMagra = percentualMassaMagra; }
    public Double getImc() { return imc; }
    public void setImc(Double imc) { this.imc = imc; }
    public Date getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(Date dataRegistro) { this.dataRegistro = dataRegistro; }
    public Date getDataRegistroOriginal() { return dataRegistroOriginal; }
    public void setDataRegistroOriginal(Date dataRegistroOriginal) { this.dataRegistroOriginal = dataRegistroOriginal; }
}
