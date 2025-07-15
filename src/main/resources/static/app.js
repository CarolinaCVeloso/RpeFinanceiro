// Configuração da API
const API_BASE_URL = 'http://localhost:8080';

// Estado da aplicação
let currentPage = 'clientes';
let currentClienteId = null;

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    updateCurrentDate();
    setupNavigation();
    loadClientes();
    setupButtonEffects();
});

// Atualizar data atual
function updateCurrentDate() {
    const dateElement = document.getElementById('current-date');
    if (dateElement) {
        const now = new Date();
        const options = { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        };
        dateElement.textContent = now.toLocaleDateString('pt-BR', options);
    }
}

// Configurar navegação
function setupNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            navigateToPage(page);
        });
    });
}

// Configurar efeitos nos botões
function setupButtonEffects() {
    // Adicionar efeito de ripple nos botões
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn')) {
            createRippleEffect(e.target, e);
        }
    });
}

// Criar efeito de ripple
function createRippleEffect(button, event) {
    const ripple = document.createElement('span');
    const rect = button.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;
    
    ripple.style.cssText = `
        position: absolute;
        width: ${size}px;
        height: ${size}px;
        left: ${x}px;
        top: ${y}px;
        background: rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        transform: scale(0);
        animation: ripple 0.6s linear;
        pointer-events: none;
    `;
    
    button.style.position = 'relative';
    button.style.overflow = 'hidden';
    button.appendChild(ripple);
    
    setTimeout(() => {
        ripple.remove();
    }, 600);
}

// Adicionar CSS para animação de ripple
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Navegar para página
function navigateToPage(page) {
    // Atualizar navegação ativa
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    document.querySelector(`[data-page="${page}"]`).classList.add('active');
    
    // Esconder todas as páginas
    document.querySelectorAll('.page').forEach(pageElement => {
        pageElement.classList.remove('active');
    });
    
    // Mostrar página selecionada
    document.getElementById(`${page}-page`).classList.add('active');
    
    // Atualizar título da página
    const pageTitle = document.querySelector('.page-title');
    const titles = {
        'clientes': 'Clientes'
    };
    pageTitle.textContent = titles[page] || 'Clientes';
    
    currentPage = page;
    
    // Carregar dados da página
    switch(page) {
        case 'clientes':
            loadClientes();
            break;
    }
}

// Funções de carregamento de dados
async function loadClientes() {
    console.log('Carregando lista de clientes...');
    showLoading();
    try {
        const response = await fetch(`${API_BASE_URL}/clientes`);
        if (!response.ok) throw new Error('Erro ao carregar clientes');
        
        const clientes = await response.json();
        console.log('Clientes carregados:', clientes);
        displayClientes(clientes);
    } catch (error) {
        console.error('Erro ao carregar clientes:', error);
        showError('Erro ao carregar clientes: ' + error.message);
    } finally {
        hideLoading();
    }
}

async function loadFaturasCliente(clienteId, clienteNome) {
    showLoading();
    try {
        const response = await fetch(`${API_BASE_URL}/faturas/${clienteId}`);
        if (!response.ok) throw new Error('Erro ao carregar faturas do cliente');
        
        const faturas = await response.json();
        displayFaturasCliente(faturas, clienteNome);
    } catch (error) {
        showError('Erro ao carregar faturas do cliente: ' + error.message);
    } finally {
        hideLoading();
    }
}

// Funções de exibição
function displayClientes(clientes) {
    console.log('Exibindo clientes:', clientes);
    const tbody = document.getElementById('clientes-tbody');
    const countElement = document.getElementById('clientes-count');
    
    countElement.textContent = clientes.length;
    
    if (clientes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 3rem;">
                    <div style="font-size: 4rem; margin-bottom: 1.5rem; opacity: 0.3;">📋</div>
                    <h3 style="color: var(--accent-graphite); margin-bottom: 0.75rem; font-size: 1.25rem;">Nenhum cliente encontrado</h3>
                    <p style="color: var(--secondary-gray); font-size: 1rem;">Comece cadastrando o primeiro cliente</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = '';
    clientes.forEach((cliente, index) => {
        console.log(`Cliente ${cliente.id}: status=${cliente.statusBloqueio}, limite=${cliente.limiteCredito}`);
        const row = document.createElement('tr');
        row.style.animation = `fadeInUp 0.3s ease ${index * 0.1}s both`;
        row.innerHTML = `
            <td style="font-weight: 600; color: var(--accent-graphite);">${cliente.nome}</td>
            <td class="cpf-cell">${formatCPF(cliente.cpf)}</td>
            <td style="font-weight: 500;">${cliente.idade} anos</td>
            <td>
                <span class="status-badge status-${getStatusClienteTexto(cliente.statusBloqueio).toLowerCase()}">
                    ${getStatusClienteTexto(cliente.statusBloqueio)}
                </span>
            </td>
            <td style="font-weight: 600; color: var(--primary-dark);">${formatCurrency(cliente.limiteDisponivel)}</td>
            <td>
                <button class="btn btn-primary" style="padding: 0.625rem 1.25rem; font-size: 0.8rem;" onclick="verFaturasCliente(${cliente.id}, '${cliente.nome}')">
                    Ver Faturas
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function displayFaturasCliente(faturas, clienteNome) {
    const tbody = document.getElementById('faturas-cliente-tbody');
    const countElement = document.getElementById('faturas-cliente-count');
    const clienteInfo = document.getElementById('cliente-info');
    
    countElement.textContent = faturas.length;
    clienteInfo.textContent = `Faturas de ${clienteNome}`;
    
    if (faturas.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; padding: 3rem;">
                    <div style="font-size: 4rem; margin-bottom: 1.5rem; opacity: 0.3;">📄</div>
                    <h3 style="color: var(--accent-graphite); margin-bottom: 0.75rem; font-size: 1.25rem;">Nenhuma fatura encontrada</h3>
                    <p style="color: var(--secondary-gray); font-size: 1rem;">Este cliente não possui faturas</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = '';
    faturas.forEach((fatura, index) => {
        const row = document.createElement('tr');
        row.style.animation = `fadeInUp 0.3s ease ${index * 0.1}s both`;
        row.innerHTML = `
            <td style="font-weight: 600; color: var(--primary-dark);">${formatCurrency(fatura.valor)}</td>
            <td style="font-weight: 500;">${formatDate(fatura.dataVencimento)}</td>
            <td>
                <span class="status-badge status-${getStatusFaturaTexto(fatura.status).toLowerCase()}">
                    ${getStatusFaturaTexto(fatura.status)}
                </span>
            </td>
            <td style="font-weight: 500;">${fatura.dataPagamento ? formatDate(fatura.dataPagamento) : '-'}</td>
            <td>
                <button class="btn btn-success" style="padding: 0.625rem 1.25rem; font-size: 0.8rem;" onclick="registrarPagamento(${fatura.id})" ${fatura.status === 'P' ? 'disabled' : ''}>
                    Registrar Pagamento
                </button>
                ${fatura.status === 'P' ? '<span style="margin-left: 0.75rem; color: #166534; font-size: 0.8rem; font-weight: 600;">Paga</span>' : ''}
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Funções de navegação
function verFaturasCliente(clienteId, clienteNome) {
    currentClienteId = clienteId;
    // Esconder página de clientes
    document.getElementById('clientes-page').classList.remove('active');
    document.getElementById('clientes-page').style.display = 'none';
    // Mostrar página de faturas do cliente
    document.getElementById('faturas-cliente-page').classList.add('active');
    document.getElementById('faturas-cliente-page').style.display = 'block';
    // Atualizar título
    document.querySelector('.page-title') && (document.querySelector('.page-title').textContent = 'Faturas do Cliente');
    // Carregar faturas do cliente
    loadFaturasCliente(clienteId, clienteNome);
}

function voltarParaClientes() {
    currentClienteId = null;
    // Esconder página de faturas do cliente
    document.getElementById('faturas-cliente-page').classList.remove('active');
    document.getElementById('faturas-cliente-page').style.display = 'none';
    // Mostrar página de clientes
    document.getElementById('clientes-page').classList.add('active');
    document.getElementById('clientes-page').style.display = 'block';
    // Recarregar lista de clientes para mostrar status atualizado
    loadClientes();
}

// Funções de ações
async function registrarPagamento(faturaId) {
    if (!confirm('Tem certeza que deseja registrar o pagamento desta fatura?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/faturas/${faturaId}/pagamento`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Erro ao registrar pagamento');
        
        // Recarregar a página atual
        if (currentClienteId) {
            loadFaturasCliente(currentClienteId, document.getElementById('cliente-info').textContent.replace('Faturas de ', ''));
        }
        
        // Recarregar também a lista de clientes para atualizar status
        loadClientes();
        
        showSuccessMessage('Pagamento registrado com sucesso!');
    } catch (error) {
        showErrorMessage('Erro ao registrar pagamento: ' + error.message);
    }
}

// Funções auxiliares
function getStatusClienteTexto(status) {
    switch (status) {
        case 'A': return 'ATIVO';
        case 'B': return 'BLOQUEADO';
        default: return status;
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

function formatCPF(cpf) {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('pt-BR');
}

function showLoading() {
    document.getElementById('loading').style.display = 'flex';
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
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
        color: #166534;
        padding: 1.25rem 1.75rem;
        border-radius: 1rem;
        box-shadow: var(--shadow-lg);
        z-index: 1001;
        max-width: 450px;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        border-left: 4px solid #16a34a;
        animation: slideIn 0.3s ease-out;
    `;
    notification.innerHTML = `
        <span style="font-size: 1.25rem;">✅</span>
        <span style="font-weight: 600;">${message}</span>
    `;
    document.body.appendChild(notification);
    
    // Remover automaticamente após 5 segundos
    setTimeout(() => {
        if (notification.parentNode) {
            notification.style.animation = 'slideOut 0.3s ease-in';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }
    }, 5000);
}

function showErrorMessage(message) {
    // Criar uma notificação de erro
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
        color: #991b1b;
        padding: 1.25rem 1.75rem;
        border-radius: 1rem;
        box-shadow: var(--shadow-lg);
        z-index: 1001;
        max-width: 450px;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        border-left: 4px solid #dc2626;
        animation: slideIn 0.3s ease-out;
    `;
    notification.innerHTML = `
        <span style="font-size: 1.25rem;">❌</span>
        <span style="font-weight: 600;">${message}</span>
    `;
    document.body.appendChild(notification);
    
    // Remover automaticamente após 8 segundos
    setTimeout(() => {
        if (notification.parentNode) {
            notification.style.animation = 'slideOut 0.3s ease-in';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }
    }, 8000);
}

// Adicionar CSS para animações
const animationStyle = document.createElement('style');
animationStyle.textContent = `
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(animationStyle); 