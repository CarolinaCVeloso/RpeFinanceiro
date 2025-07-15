package com.fintech.scheduler;

import com.fintech.service.ClienteService;
import com.fintech.service.FaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FaturaScheduler {
    
    @Autowired
    private FaturaService faturaService;
    
    @Autowired
    private ClienteService clienteService;
    
    // Executa todos os dias às 00:00
    @Scheduled(cron = "0 0 0 * * ?")
    public void verificarFaturasVencidas() {
        faturaService.verificarEAtualizarStatusFaturas();
        clienteService.verificarEAtualizarStatusBloqueio();
        clienteService.atualizarLimiteCreditoBloqueados();
    }
} 