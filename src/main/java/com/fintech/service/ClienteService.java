package com.fintech.service;

import com.fintech.dto.ClienteDTO;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    
    List<ClienteDTO> listarTodos();
    
    Optional<ClienteDTO> buscarPorId(Long id);
    
    ClienteDTO criar(ClienteDTO clienteDTO);
    
    ClienteDTO atualizar(Long id, ClienteDTO clienteDTO);
    
    ClienteDTO bloquear(Long id);
    
    ClienteDTO desbloquear(Long id);
    
    List<ClienteDTO> listarBloqueados();
    
    List<ClienteDTO> listarBloqueadosComAtraso();
    
    void atualizarLimiteCreditoBloqueados();
    
    void verificarEAtualizarStatusBloqueio();
} 