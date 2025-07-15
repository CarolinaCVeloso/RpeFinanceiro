// Configuração da API
const API_BASE_URL = 'http://localhost:8080';

// Elementos da interface
let currentClienteId = null;

// Funções de navegação
function showClientes() {
    document.getElementById('clientes-section').style.display = 'block';
    document.getElementById('faturas-section').style.display = 'none';
    document.getElementById('faturas-atrasadas-section').style.display = 'none';
    loadClientes();
    loadDashboardStats();
}

function showFaturasAtrasadas() {
    document.getElementById('clientes-section').style.display = 'none';
    document.getElementById('faturas-section').style.display = 'none';
    document.getElementById('faturas-atrasadas-section').style.display = 'block';
    loadFaturasAtrasadas();
}

function showFaturasCliente(clienteId, clienteNome) {
    currentClienteId = clienteId;
    document.getElementById('clientes-section').style.display = 'none';
    document.getElementById('faturas-section').style.display = 'block';
    document.getElementById('faturas-atrasadas-section').style.display = 'none';
    
    document.getElementById('cliente-nome').textContent = clienteNome;
    loadFaturasCliente(clienteId);
}

// Funções de carregamento de dados
async function loadClientes() {
    showLoading();
    try {
        const response = await fetch(`${API_BASE_URL}/clientes`);
        if (!response.ok) throw new Error('Erro ao carregar clientes');
        
        const clientes = await response.json();
        displayClientes(clientes);
    } catch (error) {
        showError('Erro ao carregar clientes: ' + error.message);
    } finally {
        hideLoading();
    }
}

async function loadFaturasCliente(clienteId) {
    showLoading();
    try {
        const response = await fetch(`${API_BASE_URL}/faturas/${clienteId}`);
        if (!response.ok) throw new Error('Erro ao carregar faturas');
        
        const faturas = await response.json();
        displayFaturas(faturas);
    } catch (error) {
        showError('Erro ao carregar faturas: ' + error.message);
    } finally {
        hideLoading();
    }
}

async function loadFaturasAtrasadas() {
    showLoading();
    try {
        const response = await fetch(`${API_BASE_URL}/faturas/atrasadas`);
        if (!response.ok) throw new Error('Erro ao carregar faturas em atraso');
        
        const faturas = await response.json();
        displayFaturasAtrasadas(faturas);
    } catch (error) {
        showError('Erro ao carregar faturas em atraso: ' + error.message);
    } finally {
        hideLoading();
    }
}

async function loadDashboardStats() {
    try {
        const [clientesResponse, faturasAtrasadasResponse] = await Promise.all([
            fetch(`${API_BASE_URL}/clientes`),
            fetch(`${API_BASE_URL}/faturas/atrasadas`)
        ]);

        if (!clientesResponse.ok || !faturasAtrasadasResponse.ok) {
            throw new Error('Erro ao carregar estatísticas');
        }

        const clientes = await clientesResponse.json();
        const faturasAtrasadas = await faturasAtrasadasResponse.json();
        
        const clientesBloqueados = clientes.filter(c => c.statusBloqueio === 'BLOQUEADO').length;
        
        // Calcular total de faturas (aproximação)
        const totalFaturas = clientes.length * 2; // Estimativa
        
        document.getElementById('total-clientes').textContent = clientes.length;
        document.getElementById('total-faturas').textContent = totalFaturas;
        document.getElementById('faturas-atrasadas').textContent = faturasAtrasadas.length;
        document.getElementById('clientes-bloqueados').textContent = clientesBloqueados;
        
    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
    }
}

// Funções de exibição
function displayClientes(clientes) {
    const tbody = document.getElementById('clientes-tbody');
    tbody.innerHTML = '';
    
    if (clientes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">
                    <div class="empty-state">
                        <i class="fas fa-users"></i>
                        <p>Nenhum cliente cadastrado</p>
                        <button class="btn btn-primary" onclick="showNovoClienteModal()">
                            <i class="fas fa-plus me-1"></i>Cadastrar Primeiro Cliente
                        </button>
                    </div>
                </td>
            </tr>
        `;
        return;
    }
    
    clientes.forEach(cliente => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>${cliente.nome}</strong></td>
            <td><code>${formatCPF(cliente.cpf)}</code></td>
            <td>${cliente.idade} anos</td>
            <td>
                <span class="status-badge status-${getStatusClienteTexto(cliente.statusBloqueio).toLowerCase()}">
                    <i class="fas fa-${getStatusClienteIcon(cliente.statusBloqueio)}"></i>
                    ${getStatusClienteTexto(cliente.statusBloqueio)}
                </span>
            </td>
            <td><strong>R$ ${formatCurrency(cliente.limiteCredito)}</strong></td>
            <td>
                <div class="btn-group" role="group">
                    <button class="btn btn-sm btn-info" onclick="showFaturasCliente(${cliente.id}, '${cliente.nome}')" title="Ver Faturas">
                        <i class="fas fa-file-invoice"></i>
                    </button>
                    ${cliente.statusBloqueio === 'ATIVO' ? 
                        `<button class="btn btn-sm btn-warning" onclick="bloquearCliente(${cliente.id})" title="Bloquear Cliente">
                            <i class="fas fa-ban"></i>
                        </button>` :
                        `<button class="btn btn-sm btn-success" onclick="desbloquearCliente(${cliente.id})" title="Desbloquear Cliente">
                            <i class="fas fa-check"></i>
                        </button>`
                    }
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function displayFaturas(faturas) {
    const tbody = document.getElementById('faturas-tbody');
    tbody.innerHTML = '';
    
    if (faturas.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center">
                    <div class="empty-state">
                        <i class="fas fa-file-invoice"></i>
                        <p>Nenhuma fatura encontrada para este cliente</p>
                    </div>
                </td>
            </tr>
        `;
        return;
    }
    
    faturas.forEach(fatura => {
        const isPaga = fatura.status === 'P';
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>R$ ${formatCurrency(fatura.valor)}</strong></td>
            <td>${formatDate(fatura.dataVencimento)}</td>
            <td>
                <span class="status-badge status-${getStatusFaturaTexto(fatura.status).toLowerCase()}">
                    <i class="fas fa-${getStatusFaturaIcon(fatura.status)}"></i>
                    ${getStatusFaturaTexto(fatura.status)}
                </span>
            </td>
            <td>${fatura.dataPagamento ? formatDate(fatura.dataPagamento) : '<em class="text-muted">Não paga</em>'}</td>
            <td>
                <button class="btn btn-sm btn-success" onclick="registrarPagamento(${fatura.id})" title="Registrar Pagamento" ${isPaga ? 'disabled' : ''}>
                    <i class="fas fa-money-bill"></i> Registrar Pagamento
                </button>
                ${isPaga ? '<span class="text-success ms-2"><i class="fas fa-check-circle"></i> Paga</span>' : ''}
            </td>
        `;
        tbody.appendChild(row);
    });
}

function displayFaturasAtrasadas(faturas) {
    const tbody = document.getElementById('faturas-atrasadas-tbody');
    tbody.innerHTML = '';
    
    if (faturas.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center">
                    <div class="empty-state">
                        <i class="fas fa-check-circle"></i>
                        <p>Nenhuma fatura em atraso encontrada</p>
                        <small class="text-muted">Excelente! Todos os pagamentos estão em dia.</small>
                    </div>
                </td>
            </tr>
        `;
        return;
    }
    
    faturas.forEach(fatura => {
        const isPaga = fatura.status === 'P';
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>${fatura.nomeCliente}</strong></td>
            <td><strong>R$ ${formatCurrency(fatura.valor)}</strong></td>
            <td>${formatDate(fatura.dataVencimento)}</td>
            <td><span class="badge bg-danger">${fatura.diasAtraso} dias</span></td>
            <td>
                <span class="status-badge status-${getStatusFaturaTexto(fatura.status).toLowerCase()}">
                    <i class="fas fa-${getStatusFaturaIcon(fatura.status)}"></i>
                    ${getStatusFaturaTexto(fatura.status)}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-success" onclick="registrarPagamento(${fatura.id})" title="Registrar Pagamento" ${isPaga ? 'disabled' : ''}>
                    <i class="fas fa-money-bill"></i> Registrar Pagamento
                </button>
                ${isPaga ? '<span class="text-success ms-2"><i class="fas fa-check-circle"></i> Paga</span>' : ''}
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Funções de ações
async function criarCliente() {
    const form = document.getElementById('novoClienteForm');
    
    // Validação básica
    const nome = document.getElementById('nome').value.trim();
    const cpf = document.getElementById('cpf').value.trim();
    const dataNascimento = document.getElementById('dataNascimento').value;
    const limiteCredito = parseFloat(document.getElementById('limiteCredito').value);
    
    if (!nome || !cpf || !dataNascimento || !limiteCredito) {
        alert('Por favor, preencha todos os campos obrigatórios.');
        return;
    }
    
    const cliente = {
        nome: nome,
        cpf: cpf,
        dataNascimento: dataNascimento,
        limiteCredito: limiteCredito
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/clientes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(cliente)
        });
        
        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData || 'Erro ao criar cliente');
        }
        
        // Fecha o modal e recarrega a lista
        const modal = bootstrap.Modal.getInstance(document.getElementById('novoClienteModal'));
        modal.hide();
        form.reset();
        loadClientes();
        
        showSuccessMessage('Cliente criado com sucesso!');
    } catch (error) {
        showErrorMessage('Erro ao criar cliente: ' + error.message);
    }
}

async function bloquearCliente(clienteId) {
    if (!confirm('Tem certeza que deseja bloquear este cliente?\n\nEsta ação irá:\n• Zerar o limite de crédito\n• Impedir novas transações')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/clientes/${clienteId}/bloquear`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Erro ao bloquear cliente');
        
        loadClientes();
        showSuccessMessage('Cliente bloqueado com sucesso!');
    } catch (error) {
        showErrorMessage('Erro ao bloquear cliente: ' + error.message);
    }
}

async function desbloquearCliente(clienteId) {
    if (!confirm('Tem certeza que deseja desbloquear este cliente?\n\nEsta ação irá:\n• Restaurar o acesso às transações\n• Verificar se há faturas em atraso')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/clientes/${clienteId}/desbloquear`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Erro ao desbloquear cliente');
        
        loadClientes();
        showSuccessMessage('Cliente desbloqueado com sucesso!');
    } catch (error) {
        showErrorMessage('Erro ao desbloquear cliente: ' + error.message);
    }
}

async function registrarPagamento(faturaId) {
    if (!confirm('Tem certeza que deseja registrar o pagamento desta fatura?\n\nEsta ação irá:\n• Marcar a fatura como paga\n• Atualizar a data de pagamento\n• Verificar se o cliente pode ser desbloqueado')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/faturas/${faturaId}/pagamento`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Erro ao registrar pagamento');
        
        // Recarrega a lista apropriada
        if (currentClienteId) {
            loadFaturasCliente(currentClienteId);
        } else {
            loadFaturasAtrasadas();
        }
        
        showSuccessMessage('Pagamento registrado com sucesso!');
    } catch (error) {
        showErrorMessage('Erro ao registrar pagamento: ' + error.message);
    }
}

// Funções auxiliares
function showNovoClienteModal() {
    const modal = new bootstrap.Modal(document.getElementById('novoClienteModal'));
    modal.show();
}

function showLoading() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('error').style.display = 'none';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

function showError(message) {
    const errorDiv = document.getElementById('error');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function showSuccessMessage(message) {
    // Criar uma notificação de sucesso
    const notification = document.createElement('div');
    notification.className = 'alert alert-success alert-dismissible fade show position-fixed';
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `
        <i class="fas fa-check-circle me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(notification);
    
    // Remover automaticamente após 5 segundos
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

function showErrorMessage(message) {
    // Criar uma notificação de erro
    const notification = document.createElement('div');
    notification.className = 'alert alert-danger alert-dismissible fade show position-fixed';
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    notification.innerHTML = `
        <i class="fas fa-exclamation-triangle me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(notification);
    
    // Remover automaticamente após 8 segundos
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 8000);
}

function formatCPF(cpf) {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatCurrency(value) {
    return parseFloat(value).toFixed(2).replace('.', ',');
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

function getStatusClienteTexto(status) {
    switch (status) {
        case 'A': return 'ATIVO';
        case 'B': return 'BLOQUEADO';
        default: return status;
    }
}

function getStatusClienteIcon(status) {
    switch (status) {
        case 'A': return 'check-circle';
        case 'B': return 'ban';
        default: return 'question-circle';
    }
}

function getStatusFaturaTexto(status) {
    switch (status) {
        case 'P': return 'PAGA';
        case 'A': return 'ATRASADA';
        case 'B': return 'ABERTA';
        default: return status;
    }
}

function getStatusFaturaIcon(status) {
    switch (status) {
        case 'P': return 'check-circle';
        case 'A': return 'exclamation-triangle';
        case 'B': return 'clock';
        default: return 'question-circle';
    }
}

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    showClientes();
}); 