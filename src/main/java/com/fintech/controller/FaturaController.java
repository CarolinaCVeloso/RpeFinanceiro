package com.fintech.controller;

import com.fintech.dto.FaturaDTO;
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
import java.util.Map;
import com.fintech.dto.ClienteDTO;
import com.fintech.service.ClienteService;

@RestController
@RequestMapping("/faturas")
@Tag(name = "Faturas", description = "API para gerenciamento de faturas")
@CrossOrigin(origins = "*")
public class FaturaController {
    
    @Autowired
    private FaturaService faturaService;
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Lista todas as faturas de um cliente")
    public ResponseEntity<List<FaturaDTO>> listarPorCliente(@PathVariable Long id) {
        List<FaturaDTO> faturas = faturaService.listarPorCliente(id);
        return ResponseEntity.ok(faturas);
    }
    
    @PostMapping
    @Operation(summary = "Cria nova fatura")
    public ResponseEntity<FaturaDTO> criar(@Valid @RequestBody FaturaDTO faturaDTO) {
        try {
            FaturaDTO faturaCriada = faturaService.criar(faturaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(faturaCriada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/detalhe/{id}")
    @Operation(summary = "Consulta fatura por ID")
    public ResponseEntity<FaturaDTO> buscarPorId(@PathVariable Long id) {
        Optional<FaturaDTO> fatura = faturaService.buscarPorId(id);
        return fatura.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/pagamento")
    @Operation(summary = "Registra pagamento para uma fatura")
    public ResponseEntity<?> registrarPagamento(@PathVariable Long id) {
        try {
            FaturaDTO faturaAtualizada = faturaService.registrarPagamento(id);
            
            // Retorna informações da fatura e do cliente atualizado
            return ResponseEntity.ok(Map.of(
                "fatura", faturaAtualizada,
                "mensagem", "Pagamento registrado com sucesso"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao registrar pagamento: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro inesperado ao registrar pagamento: " + e.getMessage());
        }
    }
    
    @GetMapping("/atrasadas")
    @Operation(summary = "Lista faturas em atraso")
    public ResponseEntity<List<FaturaDTO>> listarAtrasadas() {
        List<FaturaDTO> faturasAtrasadas = faturaService.listarAtrasadas();
        return ResponseEntity.ok(faturasAtrasadas);
    }
    
    @PostMapping("/verificar-vencidas")
    @Operation(summary = "Executa verificação manual de faturas vencidas")
    public ResponseEntity<String> verificarFaturasVencidas() {
        try {
            faturaService.verificarEAtualizarStatusFaturas();
            return ResponseEntity.ok("Verificação de faturas vencidas executada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao verificar faturas vencidas: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}/status")
    @Operation(summary = "Obtém status atualizado do cliente")
    public ResponseEntity<?> obterStatusCliente(@PathVariable Long clienteId) {
        try {
            // Busca informações atualizadas do cliente
            Optional<ClienteDTO> cliente = clienteService.buscarPorId(clienteId);
            if (cliente.isPresent()) {
                return ResponseEntity.ok(cliente.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao obter status do cliente: " + e.getMessage());
        }
    }
} 