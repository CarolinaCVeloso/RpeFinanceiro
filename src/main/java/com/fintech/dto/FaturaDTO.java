package com.fintech.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FaturaDTO {
    
    private Long id;
    
    private Long clienteId;
    
    private String nomeCliente;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dataVencimento;
    
    private LocalDate dataPagamento;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    private String status; // P=Paga, A=Atrasada, B=Aberta
    
    private long diasAtraso;
    
    private boolean vencida;
    
    // Construtores
    public FaturaDTO() {}
    
    public FaturaDTO(Long id, Long clienteId, String nomeCliente, LocalDate dataVencimento,
                    LocalDate dataPagamento, BigDecimal valor, String status) {
        this.id = id;
        this.clienteId = clienteId;
        this.nomeCliente = nomeCliente;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.valor = valor;
        this.status = status;
        this.vencida = LocalDate.now().isAfter(dataVencimento);
        this.diasAtraso = this.vencida && !"P".equals(status) ? 
            LocalDate.now().toEpochDay() - dataVencimento.toEpochDay() : 0;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getNomeCliente() {
        return nomeCliente;
    }
    
    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
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
    
    public long getDiasAtraso() {
        return diasAtraso;
    }
    
    public void setDiasAtraso(long diasAtraso) {
        this.diasAtraso = diasAtraso;
    }
    
    public boolean isVencida() {
        return vencida;
    }
    
    public void setVencida(boolean vencida) {
        this.vencida = vencida;
    }
} 