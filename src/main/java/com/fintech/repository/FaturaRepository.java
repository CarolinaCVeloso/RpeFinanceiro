package com.fintech.repository;

import com.fintech.model.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, Long> {
    
    List<Fatura> findByClienteId(Long clienteId);
    
    List<Fatura> findByStatus(String status);
    
    @Query("SELECT f FROM Fatura f WHERE f.status = 'A' AND f.dataVencimento < CURRENT_DATE")
    List<Fatura> findFaturasAtrasadas();
    
    @Query("SELECT f FROM Fatura f WHERE f.cliente.id = :clienteId AND f.status = 'A'")
    List<Fatura> findFaturasAtrasadasPorCliente(@Param("clienteId") Long clienteId);
    
    @Query("SELECT f FROM Fatura f WHERE f.dataVencimento < :dataLimite AND f.status != 'P'")
    List<Fatura> findFaturasVencidas(@Param("dataLimite") LocalDate dataLimite);
    
    @Query("SELECT f FROM Fatura f WHERE f.cliente.id = :clienteId AND f.status = 'B'")
    List<Fatura> findFaturasAbertasPorCliente(@Param("clienteId") Long clienteId);
    
    @Query("SELECT f FROM Fatura f WHERE f.cliente.id = :clienteId AND f.status != 'P'")
    List<Fatura> findFaturasNaoPagasPorCliente(@Param("clienteId") Long clienteId);
} 