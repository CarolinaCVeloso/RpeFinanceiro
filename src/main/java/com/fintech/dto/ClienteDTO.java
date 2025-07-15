package com.fintech.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ClienteDTO {
    
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    private String cpf;
    
    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;
    
    private String statusBloqueio; // A=Ativo, B=Bloqueado
    
    @NotNull(message = "Limite de crédito é obrigatório")
    @DecimalMin(value = "0.0", message = "Limite de crédito deve ser maior ou igual a zero")
    private BigDecimal limiteCredito;
    
    private int idade;
    
    // Construtores
    public ClienteDTO() {}
    
    public ClienteDTO(Long id, String nome, String cpf, LocalDate dataNascimento, 
                     String statusBloqueio, BigDecimal limiteCredito) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.statusBloqueio = statusBloqueio;
        this.limiteCredito = limiteCredito;
        this.idade = LocalDate.now().getYear() - dataNascimento.getYear();
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
    
    public int getIdade() {
        return idade;
    }
    
    public void setIdade(int idade) {
        this.idade = idade;
    }
} 