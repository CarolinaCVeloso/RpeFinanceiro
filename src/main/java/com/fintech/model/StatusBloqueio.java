package com.fintech.model;

public enum StatusBloqueio {
    ATIVO("A"),
    BLOQUEADO("B");
    
    private final String codigo;
    
    StatusBloqueio(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public static StatusBloqueio fromCodigo(String codigo) {
        for (StatusBloqueio status : values()) {
            if (status.codigo.equals(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
} 