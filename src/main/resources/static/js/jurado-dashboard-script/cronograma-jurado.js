// ========================================
// CRONOGRAMA - JURADO DASHBOARD
// ========================================

let todosLosEventos = [];
let eventosFuturos = [];
let eventosPasados = [];

// Inicializar cronograma
async function inicializarCronograma() {
    await cargarCronograma();
}

// Cargar eventos del cronograma
async function cargarCronograma() {
    try {
        const response = await fetch('/jurado/api/cronograma');
        if (!response.ok) throw new Error('Error al cargar cronograma');

        todosLosEventos = await response.json();
        clasificarEventos();
        renderizarCronograma(todosLosEventos);

    } catch (error) {
        console.error('Error cargando cronograma:', error);
        mostrarMensajeErrorCronograma('No se pudo cargar el cronograma');
    }
}

// Clasificar eventos en futuros y pasados
function clasificarEventos() {
    const ahora = new Date();

    eventosFuturos = todosLosEventos
        .filter(e => new Date(e.fecha) > ahora)
        .sort((a, b) => new Date(a.fecha) - new Date(b.fecha));

    eventosPasados = todosLosEventos
        .filter(e => new Date(e.fecha) <= ahora)
        .sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
}

// Renderizar cronograma completo
function renderizarCronograma(eventos) {
    const container = document.getElementById('cronograma-container');

    if (!container) {
        console.error('Contenedor de cronograma no encontrado');
        return;
    }

    if (eventos.length === 0) {
        container.innerHTML = `
            <div class="judge-card rounded-2xl p-12 text-center">
                <i class="fas fa-calendar-alt text-6xl text-gray-300 mb-4"></i>
                <h3 class="text-xl font-semibold text-gray-dark mb-2">Sin eventos</h3>
                <p class="text-gray-medium">No hay eventos programados en tu cronograma</p>
            </div>
        `;
        return;
    }

    // Renderizar eventos próximos
    let html = '';

    if (eventosFuturos.length > 0) {
        html += `
            <div class="mb-8">
                <h3 class="text-xl font-bold text-gray-dark mb-4 flex items-center">
                    <i class="fas fa-clock text-primary-blue mr-2"></i>
                    Próximos Eventos (${eventosFuturos.length})
                </h3>
                <div class="space-y-4">
                    ${eventosFuturos.map(evento => renderizarEventoCard(evento, true)).join('')}
                </div>
            </div>
        `;
    }

    if (eventosPasados.length > 0) {
        html += `
            <div>
                <h3 class="text-xl font-bold text-gray-dark mb-4 flex items-center">
                    <i class="fas fa-history text-gray-medium mr-2"></i>
                    Eventos Pasados (${eventosPasados.length})
                </h3>
                <div class="space-y-4 opacity-60">
                    ${eventosPasados.map(evento => renderizarEventoCard(evento, false)).join('')}
                </div>
            </div>
        `;
    }

    container.innerHTML = html;

    // Actualizar próximo evento en header
    actualizarProximoEvento();
}

// Renderizar una tarjeta de evento
function renderizarEventoCard(evento, esFuturo) {
    const fecha = new Date(evento.fecha);
    const diasRestantes = esFuturo ? calcularDiasRestantes(evento.fecha) : null;
    const iconoTipo = obtenerIconoTipoEvento(evento.tipo);
    const colorPrioridad = obtenerColorPrioridad(evento.prioridad);
    const colorBorde = esFuturo ? colorPrioridad : 'border-gray-300';

    return `
        <div class="judge-card rounded-xl border-l-4 ${colorBorde} p-6 hover:shadow-md transition-shadow">
            <div class="flex items-start justify-between">
                <div class="flex-1">
                    <div class="flex items-center space-x-3 mb-2">
                        <div class="w-10 h-10 ${obtenerBgPrioridad(evento.prioridad)} rounded-lg flex items-center justify-center">
                            <i class="${iconoTipo} text-white"></i>
                        </div>
                        <div>
                            <h4 class="font-semibold text-gray-dark">${evento.hackaton}</h4>
                            <p class="text-sm text-gray-medium">${evento.descripcion}</p>
                        </div>
                    </div>
                    
                    <div class="mt-3 flex flex-wrap items-center gap-4 text-sm">
                        <div class="flex items-center text-gray-medium">
                            <i class="fas fa-calendar mr-2"></i>
                            <span>${formatearFecha(fecha)}</span>
                        </div>
                        <div class="flex items-center text-gray-medium">
                            <i class="fas fa-clock mr-2"></i>
                            <span>${formatearHora(fecha)}</span>
                        </div>
                        <div class="flex items-center">
                            <i class="fas fa-tag mr-2 ${obtenerColorTexto(evento.prioridad)}"></i>
                            <span class="font-medium ${obtenerColorTexto(evento.prioridad)}">${evento.tipo.replace(/_/g, ' ')}</span>
                        </div>
                    </div>
                </div>
                
                ${esFuturo && diasRestantes !== null ? `
                <div class="text-right ml-4">
                    <div class="text-2xl font-bold ${obtenerColorDias(diasRestantes.dias)}">${diasRestantes.dias}</div>
                    <div class="text-xs text-gray-medium uppercase">${diasRestantes.dias === 1 ? 'día' : 'días'}</div>
                    ${diasRestantes.urgente ? '<div class="text-xs text-red-600 font-semibold mt-1">URGENTE</div>' : ''}
                </div>
                ` : ''}
            </div>
        </div>
    `;
}

// Calcular días restantes hasta un evento
function calcularDiasRestantes(fechaEvento) {
    const ahora = new Date();
    const fecha = new Date(fechaEvento);
    const diferencia = fecha - ahora;
    const dias = Math.ceil(diferencia / (1000 * 60 * 60 * 24));

    return {
        dias: Math.max(0, dias),
        urgente: dias <= 2 && dias >= 0
    };
}

// Obtener eventos próximos (siguientes 7 días)
function obtenerEventosProximos() {
    const ahora = new Date();
    const proximos7Dias = new Date(ahora.getTime() + (7 * 24 * 60 * 60 * 1000));

    return todosLosEventos.filter(evento => {
        const fechaEvento = new Date(evento.fecha);
        return fechaEvento > ahora && fechaEvento <= proximos7Dias;
    }).sort((a, b) => new Date(a.fecha) - new Date(b.fecha));
}

// Actualizar próximo evento en header o widget
function actualizarProximoEvento() {
    const proximoEventoElement = document.getElementById('proximo-evento-info');
    if (!proximoEventoElement) return;

    if (eventosFuturos.length > 0) {
        const proximo = eventosFuturos[0];
        const diasRestantes = calcularDiasRestantes(proximo.fecha);

        proximoEventoElement.innerHTML = `
            <div class="flex items-center space-x-3">
                <i class="fas fa-bell text-accent-pink"></i>
                <div>
                    <p class="text-sm font-medium text-gray-dark">${proximo.hackaton}</p>
                    <p class="text-xs text-gray-medium">En ${diasRestantes.dias} ${diasRestantes.dias === 1 ? 'día' : 'días'}</p>
                </div>
            </div>
        `;
    } else {
        proximoEventoElement.innerHTML = `
            <p class="text-sm text-gray-medium">No hay eventos próximos</p>
        `;
    }
}

// Formatear fecha legible
function formatearFecha(fecha) {
    const opciones = {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    };
    return fecha.toLocaleDateString('es-ES', opciones);
}

// Formatear hora legible
function formatearHora(fecha) {
    return fecha.toLocaleTimeString('es-ES', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Obtener icono según tipo de evento
function obtenerIconoTipoEvento(tipo) {
    const iconos = {
        'INICIO_HACKATHON': 'fas fa-rocket',
        'FIN_HACKATHON': 'fas fa-flag-checkered',
        'FIN_EVALUACIONES': 'fas fa-clipboard-check',
        'PRESENTACION': 'fas fa-presentation'
    };
    return iconos[tipo] || 'fas fa-calendar-day';
}

// Obtener color según prioridad
function obtenerColorPrioridad(prioridad) {
    const colores = {
        'ALTA': 'border-red-500',
        'MEDIA': 'border-yellow-500',
        'BAJA': 'border-blue-500'
    };
    return colores[prioridad] || 'border-gray-300';
}

// Obtener background según prioridad
function obtenerBgPrioridad(prioridad) {
    const colores = {
        'ALTA': 'bg-red-500',
        'MEDIA': 'bg-yellow-500',
        'BAJA': 'bg-blue-500'
    };
    return colores[prioridad] || 'bg-gray-500';
}

// Obtener color de texto según prioridad
function obtenerColorTexto(prioridad) {
    const colores = {
        'ALTA': 'text-red-600',
        'MEDIA': 'text-yellow-600',
        'BAJA': 'text-blue-600'
    };
    return colores[prioridad] || 'text-gray-600';
}

// Obtener color según días restantes
function obtenerColorDias(dias) {
    if (dias <= 1) return 'text-red-600';
    if (dias <= 3) return 'text-yellow-600';
    if (dias <= 7) return 'text-blue-600';
    return 'text-gray-600';
}

// Mostrar mensaje de error
function mostrarMensajeErrorCronograma(mensaje) {
    const container = document.getElementById('cronograma-container');
    if (container) {
        container.innerHTML = `
            <div class="judge-card rounded-2xl p-12 text-center">
                <i class="fas fa-exclamation-triangle text-6xl text-red-300 mb-4"></i>
                <h3 class="text-xl font-semibold text-gray-dark mb-2">Error</h3>
                <p class="text-gray-medium">${mensaje}</p>
            </div>
        `;
    }
}

// Filtrar eventos por tipo
function filtrarPorTipoEvento(tipo) {
    const eventosFiltrados = tipo ? todosLosEventos.filter(e => e.tipo === tipo) : todosLosEventos;
    renderizarCronograma(eventosFiltrados);
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function () {
    // Configurar filtro de tipo de evento si existe
    const filtroTipo = document.getElementById('filtro-tipo-evento');
    if (filtroTipo) {
        filtroTipo.addEventListener('change', (e) => filtrarPorTipoEvento(e.target.value));
    }
});
