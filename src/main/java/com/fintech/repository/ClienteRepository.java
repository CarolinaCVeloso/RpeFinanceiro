package com.fintech.repository;

import com.fintech.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByCpf(String cpf);
    
    List<Cliente> findByStatusBloqueio(String statusBloqueio);
    
    @Query("SELECT c FROM Cliente c WHERE c.statusBloqueio = 'B' AND EXISTS " +
           "(SELECT f FROM Fatura f WHERE f.cliente = c AND f.status = 'A' AND " +
           "f.dataVencimento < :dataLimite)")
    List<Cliente> findClientesBloqueadosComAtraso(@Param("dataLimite") java.time.LocalDate dataLimite);
    
    @Query("SELECT c FROM Cliente c WHERE c.statusBloqueio = 'B' AND EXISTS " +
           "(SELECT f FROM Fatura f WHERE f.cliente = c AND f.status = 'A' AND " +
           "(CURRENT_DATE - f.dataVencimento) > 3)")
    List<Cliente> findClientesBloqueadosComMaisDe3DiasAtraso();
    
    boolean existsByCpf(String cpf);
} 