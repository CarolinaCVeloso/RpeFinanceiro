package com.fintech.model;

public enum StatusFatura {
    PAGA("PAGA"),
    ATRASADA("ATRASADA"),
    ABERTA("ABERTA");
    
    private final String codigo;
    
    StatusFatura(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public static StatusFatura fromCodigo(String codigo) {
        for (StatusFatura status : values()) {
            if (status.codigo.equals(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
} 