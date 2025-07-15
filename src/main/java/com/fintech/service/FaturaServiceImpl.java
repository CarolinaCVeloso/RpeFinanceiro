package com.fintech.service;

import com.fintech.dto.FaturaDTO;
import com.fintech.model.Cliente;
import com.fintech.model.Fatura;
import com.fintech.repository.ClienteRepository;
import com.fintech.repository.FaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FaturaServiceImpl implements FaturaService {
    
    @Autowired
    private FaturaRepository faturaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ClienteService clienteService;
    
    @Override
    public List<FaturaDTO> listarPorCliente(Long clienteId) {
        return faturaRepository.findByClienteId(clienteId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<FaturaDTO> buscarPorId(Long id) {
        return faturaRepository.findById(id)
                .map(this::converterParaDTO);
    }
    
    @Override
    public FaturaDTO criar(FaturaDTO faturaDTO) {
        Cliente cliente = clienteRepository.findById(faturaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + faturaDTO.getClienteId()));
        
        Fatura fatura = new Fatura();
        fatura.setCliente(cliente);
        fatura.setDataVencimento(faturaDTO.getDataVencimento());
        fatura.setValor(faturaDTO.getValor());
        fatura.setStatus("B"); // B=Aberta
        
        Fatura faturaSalva = faturaRepository.save(fatura);
        return converterParaDTO(faturaSalva);
    }
    
    @Override
    @Transactional
    public FaturaDTO registrarPagamento(Long id) {
        Fatura fatura = faturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada: " + id));
        
        if ("P".equals(fatura.getStatus())) {
            throw new RuntimeException("Fatura já foi paga");
        }
        
        // Registra o pagamento - muda status para PAGA
        fatura.registrarPagamento();
        Fatura faturaAtualizada = faturaRepository.save(fatura);
        
        // Força o flush para garantir que as mudanças sejam persistidas
        faturaRepository.flush();
        
        System.out.println("Pagamento registrado para fatura " + id + " do cliente " + fatura.getCliente().getId());
        
        // Verifica se o cliente pode ser desbloqueado após o pagamento
        verificarDesbloqueioCliente(fatura.getCliente().getId());
        
        return converterParaDTO(faturaAtualizada);
    }
    
    @Override
    public List<FaturaDTO> listarAtrasadas() {
        return faturaRepository.findFaturasAtrasadas().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void verificarEAtualizarStatusFaturas() {
        LocalDate hoje = LocalDate.now();
        List<Fatura> faturasVencidas = faturaRepository.findFaturasVencidas(hoje);
        
        for (Fatura fatura : faturasVencidas) {
            // Marca como atrasada se não foi paga
            if (!"P".equals(fatura.getStatus())) {
                fatura.marcarComoAtrasada();
                faturaRepository.save(fatura);
                
                // Se a fatura está atrasada há mais de 3 dias, bloqueia o cliente
                if (fatura.getDiasAtraso() > 3) {
                    System.out.println("[DEBUG] Bloqueando cliente por atraso: id=" + fatura.getCliente().getId() + ", dias atraso=" + fatura.getDiasAtraso());
                    clienteService.bloquear(fatura.getCliente().getId());
                }
            }
        }
    }
    
    private void verificarDesbloqueioCliente(Long clienteId) {
        try {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clienteId));
            
            System.out.println("Verificando desbloqueio do cliente " + clienteId + " - Status atual: " + cliente.getStatusBloqueio());
            
            // Verifica se o cliente está bloqueado
            if (!cliente.isBloqueado()) {
                System.out.println("Cliente " + clienteId + " já está desbloqueado");
                return; // Cliente já está desbloqueado
            }
            
            // Verifica se o cliente tem faturas não pagas (em aberto ou atrasadas)
            List<Fatura> faturasNaoPagas = faturaRepository.findFaturasNaoPagasPorCliente(clienteId);
            
            System.out.println("Cliente " + clienteId + " possui " + faturasNaoPagas.size() + " faturas não pagas");
            
            // Só desbloqueia se não há faturas não pagas
            if (faturasNaoPagas.isEmpty()) {
                System.out.println("Desbloqueando cliente " + clienteId + " - não há faturas pendentes");
                clienteService.desbloquear(clienteId);
                System.out.println("Cliente " + clienteId + " desbloqueado e limite normalizado para R$ 2.000,00 após pagamento");
            } else {
                System.out.println("Cliente " + clienteId + " mantido bloqueado - ainda possui " + faturasNaoPagas.size() + " faturas pendentes");
                for (Fatura fatura : faturasNaoPagas) {
                    System.out.println("  - Fatura " + fatura.getId() + " (Status: " + fatura.getStatus() + ")");
                }
            }
        } catch (Exception e) {
            // Log do erro mas não interrompe o fluxo
            System.err.println("Erro ao verificar desbloqueio do cliente " + clienteId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private FaturaDTO converterParaDTO(Fatura fatura) {
        return new FaturaDTO(
                fatura.getId(),
                fatura.getCliente().getId(),
                fatura.getCliente().getNome(),
                fatura.getDataVencimento(),
                fatura.getDataPagamento(),
                fatura.getValor(),
                fatura.getStatus()
        );
    }
} 