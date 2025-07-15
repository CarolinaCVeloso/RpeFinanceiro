-- Script para popular o banco de dados com dados de exemplo

-- Inserir clientes de exemplo
INSERT INTO clientes (nome, cpf, data_nascimento, limite_credito, limite_disponivel, status_bloqueio) VALUES
('João Silva', '12345678901', '1985-03-15', 5000.00, 5000.00, 'A'),
('Maria Santos', '98765432100', '1990-07-22', 3000.00, 3000.00, 'A'),
('Pedro Oliveira', '11122233344', '1988-11-10', 7500.00, 7500.00, 'A'),
('Ana Costa', '55566677788', '1992-05-18', 4000.00, 4000.00, 'A'),
('Carlos Ferreira', '99988877766', '1987-09-25', 6000.00, 6000.00, 'A'),
('Lucia Mendes', '12312312312', '1995-12-03', 3850.00, 0.00, 'B'),
('Roberto Alves', '45645645645', '1983-08-14', 4500.00, 0.00, 'B');

-- Inserir faturas de exemplo
-- Faturas pagas
INSERT INTO faturas (cliente_id, data_vencimento, data_pagamento, valor, status) VALUES
(1, '2024-12-15', '2024-12-10', 500.00, 'P'),
(2, '2024-12-20', '2024-12-18', 300.00, 'P'),
(3, '2024-12-10', '2024-12-08', 1000.00, 'P'),
(5, '2024-12-25', '2024-12-22', 900.00, 'P');

-- Faturas abertas (não vencidas)
INSERT INTO faturas (cliente_id, data_vencimento, valor, status) VALUES
(1, '2025-01-15', 750.00, 'B'),
(2, '2025-01-20', 450.00, 'B'),
(3, '2025-01-10', 1200.00, 'B'),
(5, '2025-01-25', 1100.00, 'B');

-- CASO 1: Cliente com faturas de 2 dias de atraso (NÃO deve ser bloqueado)
-- Ana Costa - faturas vencidas há 2 dias
INSERT INTO faturas (cliente_id, data_vencimento, valor, status) VALUES
(4, CURRENT_DATE - INTERVAL '2 days', 800.00, 'A'),
(4, CURRENT_DATE - INTERVAL '2 days', 600.00, 'A');

-- CASO 2: Cliente com faturas de 3 dias de atraso (DEVE ser bloqueado automaticamente)
-- Lucia Mendes - faturas vencidas há 3 dias
INSERT INTO faturas (cliente_id, data_vencimento, valor, status) VALUES
(6, CURRENT_DATE - INTERVAL '3 days', 1200.00, 'A'),
(6, CURRENT_DATE - INTERVAL '3 days', 950.00, 'A');

-- CASO 3: Cliente com faturas de mais de 3 dias de atraso (DEVE ser bloqueado)
-- Roberto Alves - faturas vencidas há 5 dias
INSERT INTO faturas (cliente_id, data_vencimento, valor, status) VALUES
(7, CURRENT_DATE - INTERVAL '5 days', 1500.00, 'A'),
(7, CURRENT_DATE - INTERVAL '5 days', 1100.00, 'A'); 