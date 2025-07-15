package com.fintech.service;

import com.fintech.dto.FaturaDTO;

import java.util.List;
import java.util.Optional;

public interface FaturaService {
    
    List<FaturaDTO> listarPorCliente(Long clienteId);
    
    Optional<FaturaDTO> buscarPorId(Long id);
    
    FaturaDTO criar(FaturaDTO faturaDTO);
    
    FaturaDTO registrarPagamento(Long id);
    
    List<FaturaDTO> listarAtrasadas();
    
    void verificarEAtualizarStatusFaturas();
} 