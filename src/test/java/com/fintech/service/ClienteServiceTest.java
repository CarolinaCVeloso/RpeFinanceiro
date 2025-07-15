package com.fintech.service;

import com.fintech.dto.ClienteDTO;
import com.fintech.model.Cliente;
import com.fintech.repository.ClienteRepository;
import com.fintech.repository.FaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FaturaRepository faturaRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setDataNascimento(LocalDate.of(1985, 3, 15));
        cliente.setStatusBloqueio("A"); // A=Ativo
        cliente.setLimiteCredito(new BigDecimal("5000.00"));

        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Silva");
        clienteDTO.setCpf("12345678901");
        clienteDTO.setDataNascimento(LocalDate.of(1985, 3, 15));
        clienteDTO.setLimiteCredito(new BigDecimal("5000.00"));
    }

    @Test
    void testListarTodos() {
        // Given
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));

        // When
        List<ClienteDTO> result = clienteService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("João Silva", result.get(0).getNome());
        verify(clienteRepository).findAll();
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        Optional<ClienteDTO> result = clienteService.buscarPorId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("João Silva", result.get().getNome());
        verify(clienteRepository).findById(1L);
    }

    @Test
    void testCriar() {
        // Given
        when(clienteRepository.existsByCpf("12345678901")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // When
        ClienteDTO result = clienteService.criar(clienteDTO);

        // Then
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(clienteRepository).existsByCpf("12345678901");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void testCriarComCpfDuplicado() {
        // Given
        when(clienteRepository.existsByCpf("12345678901")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> clienteService.criar(clienteDTO));
        verify(clienteRepository).existsByCpf("12345678901");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testBloquear() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // When
        ClienteDTO result = clienteService.bloquear(1L);

        // Then
        assertNotNull(result);
        assertEquals("B", result.getStatusBloqueio()); // B=Bloqueado
        assertEquals(BigDecimal.ZERO, result.getLimiteCredito());
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void testDesbloquear() {
        // Given
        cliente.setStatusBloqueio("B"); // B=Bloqueado
        cliente.setLimiteCredito(BigDecimal.ZERO);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // When
        ClienteDTO result = clienteService.desbloquear(1L);

        // Then
        assertNotNull(result);
        assertEquals("A", result.getStatusBloqueio()); // A=Ativo
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(any(Cliente.class));
    }
} 