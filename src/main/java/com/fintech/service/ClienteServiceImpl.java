package com.fintech.service;

import com.fintech.dto.ClienteDTO;
import com.fintech.model.Cliente;
import com.fintech.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Override
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ClienteDTO> buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::converterParaDTO);
    }
    
    @Override
    public ClienteDTO criar(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByCpf(clienteDTO.getCpf())) {
            throw new RuntimeException("CPF já cadastrado: " + clienteDTO.getCpf());
        }
        
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setDataNascimento(clienteDTO.getDataNascimento());
        cliente.setLimiteCredito(clienteDTO.getLimiteCredito());
        cliente.setStatusBloqueio("A"); // A=Ativo
        // Ajuste: se limiteDisponivel vier nulo, usar o valor do limiteCredito
        if (clienteDTO.getLimiteDisponivel() == null) {
            cliente.setLimiteDisponivel(clienteDTO.getLimiteCredito());
        } else {
            cliente.setLimiteDisponivel(clienteDTO.getLimiteDisponivel());
        }
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return converterParaDTO(clienteSalvo);
    }
    
    @Override
    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + id));
        
        // Verifica se o CPF já existe em outro cliente
        Optional<Cliente> clienteComCpf = clienteRepository.findByCpf(clienteDTO.getCpf());
        if (clienteComCpf.isPresent() && !clienteComCpf.get().getId().equals(id)) {
            throw new RuntimeException("CPF já cadastrado: " + clienteDTO.getCpf());
        }
        
        cliente.setNome(clienteDTO.getNome());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setDataNascimento(clienteDTO.getDataNascimento());
        cliente.setLimiteCredito(clienteDTO.getLimiteCredito());
        
        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return converterParaDTO(clienteAtualizado);
    }
    
    @Override
    @Transactional
    public ClienteDTO bloquear(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + id));
        // Bloqueia o cliente e zera o limite de crédito
        cliente.bloquear();
        Cliente clienteBloqueado = clienteRepository.save(cliente);
        clienteRepository.flush(); // Garante persistência imediata
        System.out.println("[DEBUG] Service: Cliente " + id + " bloqueado e limite zerado.");
        return converterParaDTO(clienteBloqueado);
    }
    
    @Override
    @Transactional
    public ClienteDTO desbloquear(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + id));
                cliente.desbloquear();
        Cliente clienteDesbloqueado = clienteRepository.save(cliente);
        
        System.out.println("Cliente " + id + " desbloqueado com sucesso.");
        
        return converterParaDTO(clienteDesbloqueado);
    }
    
    @Override
    public List<ClienteDTO> listarBloqueados() {
        return clienteRepository.findByStatusBloqueio("B").stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ClienteDTO> listarBloqueadosComAtraso() {
        return clienteRepository.findClientesBloqueadosComMaisDe3DiasAtraso().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void atualizarLimiteCreditoBloqueados() {
        List<Cliente> clientesBloqueados = clienteRepository.findByStatusBloqueio("B");
        for (Cliente cliente : clientesBloqueados) {
            cliente.setLimiteCredito(BigDecimal.ZERO);
            clienteRepository.save(cliente);
        }
    }
    
    @Override
    public void verificarEAtualizarStatusBloqueio() {
        LocalDate dataLimite = LocalDate.now().minusDays(3);
        List<Cliente> clientesComAtraso = clienteRepository.findClientesBloqueadosComAtraso(dataLimite);
        
        for (Cliente cliente : clientesComAtraso) {
            cliente.bloquear();
            clienteRepository.save(cliente);
        }
    }
    
    private ClienteDTO converterParaDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getDataNascimento(),
                cliente.getStatusBloqueio(),
                cliente.getLimiteCredito(),
                cliente.getLimiteDisponivel()
        );
    }
} 