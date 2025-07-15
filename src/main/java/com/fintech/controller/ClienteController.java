package com.fintech.controller;

import com.fintech.dto.ClienteDTO;
import com.fintech.dto.FaturaDTO;
import com.fintech.service.ClienteService;
import com.fintech.service.FaturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
@CrossOrigin(origins = "*")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private FaturaService faturaService;
    
    @GetMapping
    @Operation(summary = "Lista todos os clientes")
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        List<ClienteDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }
    
    @PostMapping
    @Operation(summary = "Cadastra novo cliente")
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO clienteCriado = clienteService.criar(clienteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Consulta cliente por ID")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        Optional<ClienteDTO> cliente = clienteService.buscarPorId(id);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/faturas")
    @Operation(summary = "Lista faturas de um cliente específico")
    public ResponseEntity<List<FaturaDTO>> listarFaturasCliente(@PathVariable Long id) {
        try {
            List<FaturaDTO> faturas = faturaService.listarPorCliente(id);
            return ResponseEntity.ok(faturas);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza/bloqueia cliente")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable Long id, 
                                              @Valid @RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO clienteAtualizado = clienteService.atualizar(id, clienteDTO);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/bloqueados")
    @Operation(summary = "Lista clientes bloqueados")
    public ResponseEntity<List<ClienteDTO>> listarBloqueados() {
        List<ClienteDTO> clientesBloqueados = clienteService.listarBloqueados();
        return ResponseEntity.ok(clientesBloqueados);
    }
    
    @PutMapping("/{id}/bloquear")
    @Operation(summary = "Bloqueia um cliente")
    public ResponseEntity<ClienteDTO> bloquear(@PathVariable Long id) {
        try {
            ClienteDTO clienteBloqueado = clienteService.bloquear(id);
            return ResponseEntity.ok(clienteBloqueado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/desbloquear")
    @Operation(summary = "Desbloqueia um cliente")
    public ResponseEntity<ClienteDTO> desbloquear(@PathVariable Long id) {
        try {
            ClienteDTO clienteDesbloqueado = clienteService.desbloquear(id);
            return ResponseEntity.ok(clienteDesbloqueado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/bloqueados/atraso")
    @Operation(summary = "Lista clientes bloqueados com mais de 3 dias de atraso")
    public ResponseEntity<List<ClienteDTO>> listarBloqueadosComAtraso() {
        List<ClienteDTO> clientes = clienteService.listarBloqueadosComAtraso();
        return ResponseEntity.ok(clientes);
    }
} 