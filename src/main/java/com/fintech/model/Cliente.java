package com.fintech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false)
    private String nome;
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    @Column(unique = true, nullable = false, length = 11)
    private String cpf;
    
    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(nullable = false)
    private LocalDate dataNascimento;
    
    @Column(nullable = false, length = 1)
    private String statusBloqueio = "A"; // A=Ativo, B=Bloqueado
    
    @NotNull(message = "Limite de crédito é obrigatório")
    @DecimalMin(value = "0.0", message = "Limite de crédito deve ser maior ou igual a zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteCredito;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteDisponivel;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Fatura> faturas;
    
    // Construtores
    public Cliente() {}
    
    public Cliente(String nome, String cpf, LocalDate dataNascimento, BigDecimal limiteCredito) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.limiteCredito = limiteCredito;
        this.limiteDisponivel = limiteCredito;
        this.statusBloqueio = "A"; // A=Ativo, B=Bloqueado
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public String getStatusBloqueio() {
        return statusBloqueio;
    }
    
    public void setStatusBloqueio(String statusBloqueio) {
        this.statusBloqueio = statusBloqueio;
    }
    
    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }
    
    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }
    
    public BigDecimal getLimiteDisponivel() { return limiteDisponivel; }
    public void setLimiteDisponivel(BigDecimal limiteDisponivel) { this.limiteDisponivel = limiteDisponivel; }
    
    public List<Fatura> getFaturas() {
        return faturas;
    }
    
    public void setFaturas(List<Fatura> faturas) {
        this.faturas = faturas;
    }
    
    // Métodos auxiliares
    public int getIdade() {
        return LocalDate.now().getYear() - dataNascimento.getYear();
    }
    
    public boolean isBloqueado() {
        return "B".equals(statusBloqueio);
    }
    
    public void bloquear() {
        this.statusBloqueio = "B";
        this.limiteDisponivel = BigDecimal.ZERO; // Zera o limite de crédito
        System.out.println("[DEBUG] Cliente bloqueado: id=" + this.id + ", limiteDisponivel zerado.");
    }
    
    public void desbloquear() {
        this.statusBloqueio = "A";
        this.limiteDisponivel = this.limiteCredito;
        System.out.println("[DEBUG] Cliente desbloqueado: id=" + this.id + ", limiteDisponivel restaurado para " + this.limiteCredito);
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", statusBloqueio=" + statusBloqueio +
                ", limiteCredito=" + limiteCredito +
                '}';
    }
} 