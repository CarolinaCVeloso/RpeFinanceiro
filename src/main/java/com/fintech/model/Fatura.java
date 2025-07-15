package com.fintech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "faturas")
public class Fatura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Column(nullable = false)
    private LocalDate dataVencimento;
    
    @Column
    private LocalDate dataPagamento;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(nullable = false, length = 1)
    private String status = "B"; // P=Paga, A=Atrasada, B=Aberta
    
    // Construtores
    public Fatura() {}
    
    public Fatura(Cliente cliente, LocalDate dataVencimento, BigDecimal valor) {
        this.cliente = cliente;
        this.dataVencimento = dataVencimento;
        this.valor = valor;
        this.status = "B"; // Abertura
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public LocalDate getDataVencimento() {
        return dataVencimento;
    }
    
    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
    
    public LocalDate getDataPagamento() {
        return dataPagamento;
    }
    
    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Métodos auxiliares
    public boolean isVencida() {
        return LocalDate.now().isAfter(dataVencimento);
    }
    
    public boolean isAtrasada() {
        return isVencida() && !"P".equals(status);
    }
    
    public long getDiasAtraso() {
        if (!isAtrasada()) {
            return 0;
        }
        return LocalDate.now().toEpochDay() - dataVencimento.toEpochDay();
    }
    
    public void registrarPagamento() {
        this.dataPagamento = LocalDate.now();
        this.status = "P"; // Paga
    }
    
    public void marcarComoAtrasada() {
        if (isVencida() && !"P".equals(status)) {
            this.status = "A"; // Atrasada
        }
    }
    
    @Override
    public String toString() {
        return "Fatura{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getId() : null) +
                ", dataVencimento=" + dataVencimento +
                ", dataPagamento=" + dataPagamento +
                ", valor=" + valor +
                ", status=" + status +
                '}';
    }
} 