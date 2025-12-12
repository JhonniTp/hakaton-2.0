// ========================================
// DASHBOARD PARTICIPANTE - DYNAMIC DATA
// ========================================

document.addEventListener('DOMContentLoaded', function () {
    cargarDatosDashboard();
});

// Cargar todos los datos del dashboard
async function cargarDatosDashboard() {
    await Promise.all([
        cargarEstadisticas(),
        cargarHackatonesActivos(),
        cargarTareasPendientes(),
        cargarLogros()
    ]);
}

// Cargar estadísticas del participante
async function cargarEstadisticas() {
    try {
        const response = await fetch('/participante/api/estadisticas');
        if (!response.ok) throw new Error('Error al cargar estadísticas');

        const stats = await response.json();

        // Actualizar cards de estadísticas
        actualizarCardEstadisticas(stats);

    } catch (error) {
        console.error('Error:', error);
    }
}

// Actualizar cards de estadísticas en el dashboard
function actualizarCardEstadisticas(stats) {
    // Hack athons activos
    const hackatonesActivosElement = document.querySelector('.stats-hackathons-activos');
    if (hackatonesActivosElement) {
        hackatonesActivosElement.textContent = stats.hackatonesActivos || 0;
    }

    // Total proyectos
    const proyectosTotalElement = document.querySelector('.stats-proyectos-total');
    if (proyectosTotalElement) {
        proyectosTotalElement.textContent = (stats.proyectosActivos + (stats.proyectosCompletados || 0)) || 0;
    }

    // Puntuación promedio
    const puntuacionElement = document.querySelector('.stats-puntuacion');
    if (puntuacionElement) {
        puntuacionElement.textContent = stats.puntuacionPromedio ? stats.puntuacionPromedio.toFixed(1) : '0.0';
    }

    // Ranking posición
    const rankingElement = document.querySelector('.stats-ranking');
    if (rankingElement) {
        rankingElement.textContent = stats.posicionRanking > 0 ? `#${stats.posicionRanking}` : '-';
    }

    // Actualizar mensaje de bienvenida
    const mensajeBienvenida = document.querySelector('.mensaje-bienvenida');
    if (mensajeBienvenida) {
        mensajeBienvenida.innerHTML = `
            Tienes <span class="font-bold">${stats.hackatonesActivos} ${stats.hackatonesActivos === 1 ? 'hackaton activo' : 'hackatones activos'}</span> y
            <span class="font-bold">${stats.proyectosActivos} ${stats.proyectosActivos === 1 ? 'proyecto' : 'proyectos'}</span> en desarrollo
        `;
    }
}

// Cargar hackathons activos
async function cargarHackatonesActivos() {
    try {
        const response = await fetch('/participante/api/hackathons-activos');
        if (!response.ok) throw new Error('Error al cargar hackathons');

        const hackathons = await response.json();

        renderizarHackatonesActivos(hackathons);

    } catch (error) {
        console.error('Error:', error);
        mostrarMensajeNoHackathons();
    }
}

// Renderizar tarjetas de hackathons activos  
function renderizarHackatonesActivos(hackathons) {
    const container = document.getElementById('hackathons-activos-container');
    if (!container) return;

    if (hackathons.length === 0) {
        container.innerHTML = `
            <div class="glass-card rounded-2xl p-12 text-center col-span-full">
                <i class="fas fa-trophy text-6xl text-gray-300 mb-4"></i>
                <h3 class="text-xl font-semibold text-gray-dark mb-2">No tienes hackathons activos</h3>
                <p class="text-gray-medium">Explora hackathons disponibles para inscribirte</p>
                <button onclick="showSection('hackathons')" class="mt-4 bg-secondary-cyan text-white px-6 py-3 rounded-lg">
                    Explorar Hackathons
                </button>
            </div>
        `;
        return;
    }

    container.innerHTML = hackathons.map(h => {
        const progreso = h.progresoProyecto || 0;
        const diasRestantes = h.diasRestantes || 0;
        const horasRestantes = h.horasRestantes || 0;

        return `
            <div class="glass-card rounded-2xl overflow-hidden card-hover">
                <div class="relative h-40 gradient-bg">
                    <div class="absolute inset-0 flex items-center justify-center">
                        <div class="w-20 h-20 bg-white bg-opacity-20 rounded-full flex items-center justify-center floating-element">
                            <i class="fas fa-code text-white text-3xl"></i>
                        </div>
                    </div>
                    <div class="absolute bottom-6 left-6 text-white">
                        <h4 class="text-2xl font-bold mb-1">${h.nombre}</h4>
                        <p class="text-sm opacity-90 flex items-center">
                            <i class="fas fa-clock mr-2"></i>Termina en ${diasRestantes} ${diasRestantes === 1 ? 'día' : 'días'}
                        </p>
                    </div>
                    <div class="absolute top-6 right-6">
                        <span class="glass-card bg-green-500 text-white px-4 py-2 rounded-full text-sm font-medium pulse-glow">
                            🔥 Activo
                        </span>
                    </div>
                </div>
                <div class="p-6">
                    <div class="mb-6">
                        <div class="flex justify-between text-sm mb-3">
                            <span class="text-gray-medium font-medium">Progreso del proyecto</span>
                            <span class="font-bold text-gradient">${progreso}%</span>
                        </div>
                        <div class="w-full bg-gray-200 rounded-full h-3">
                            <div class="gradient-bg h-3 rounded-full transition-all duration-1000" style="width: ${progreso}%"></div>
                        </div>
                    </div>
                    <div class="flex items-center justify-between">
                        <div class="flex items-center space-x-6 text-sm text-gray-medium">
                            <span class="flex items-center">
                                <i class="fas fa-users mr-2 text-secondary-cyan"></i>
                                <strong>${h.nombreEquipo || 'Sin equipo'}</strong>
                            </span>
                            <span class="flex items-center">
                                <i class="fas fa-fire mr-2 text-accent-pink"></i>
                                ${horasRestantes}h restantes
                            </span>
                        </div>
                        <button onclick="verDetalleHackathon(${h.idHackathon})" 
                            class="gradient-bg text-white px-6 py-3 rounded-xl font-medium transition-all hover:scale-105 neon-glow">
                            <i class="fas fa-rocket mr-2"></i>Ver Proyecto
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// Cargar tareas pendientes
async function cargarTareasPendientes() {
    try {
        const response = await fetch('/participante/api/tareas-pendientes');
        if (!response.ok) throw new Error('Error al cargar tareas');

        const tareas = await response.json();

        renderizarTareasPendientes(tareas);

    } catch (error) {
        console.error('Error:', error);
    }
}

// Renderizar tareas pendientes
function renderizarTareasPendientes(tareas) {
    const container = document.getElementById('tareas-pendientes-container');
    if (!container) return;

    if (tareas.length === 0) {
        container.innerHTML = `
            <div class="flex items-center justify-center py-8">
                <div class="text-center">
                    <i class="fas fa-check-circle text-4xl text-green-500 mb-2"></i>
                    <p class="text-gray-medium">¡Todo al día!</p>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = tareas.map(tarea => {
        const colorPrioridad = {
            'ALTA': 'bg-red-500',
            'MEDIA': 'bg-yellow-500',
            'BAJA': 'bg-blue-500'
        }[tarea.prioridad] || 'bg-gray-500';

        return `
            <div class="flex items-start space-x-3">
                <div class="w-2 h-2 ${colorPrioridad} rounded-full mt-2 flex-shrink-0"></div>
                <div class="flex-1">
                    <p class="text-sm font-medium text-gray-dark">${tarea.titulo}</p>
                    <p class="text-xs text-gray-medium">${tarea.hackathon} - Vence en ${tarea.diasRestantes} ${tarea.diasRestantes === 1 ? 'día' : 'días'}</p>
                </div>
            </div>
        `;
    }).join('');
}

// Cargar logros recientes
async function cargarLogros() {
    try {
        const response = await fetch('/participante/api/logros');
        if (!response.ok) throw new Error('Error al cargar logros');

        const logros = await response.json();

        renderizarLogros(logros);

    } catch (error) {
        console.error('Error:', error);
    }
}

// Renderizar logros
function renderizarLogros(logros) {
    const container = document.getElementById('logros-container');
    if (!container) return;

    if (logros.length === 0) {
        container.innerHTML = `
            <div class="flex items-center justify-center py-8">
                <div class="text-center">
                    <i class="fas fa-medal text-4xl text-gray-300 mb-2"></i>
                    <p class="text-gray-medium text-sm">Completa proyectos para obtener logros</p>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = logros.map(logro => {
        const colorBadge = {
            'yellow': 'bg-yellow-100 text-yellow-600',
            'blue': 'bg-blue-100 text-blue-600',
            'green': 'bg-green-100 text-green-600',
            'red': 'bg-red-100 text-red-600'
        }[logro.color] || 'bg-gray-100 text-gray-600';

        return `
            <div class="flex items-center space-x-3">
                <div class="w-10 h-10 ${colorBadge} rounded-full flex items-center justify-center">
                    <i class="${logro.icono}"></i>
                </div>
                <div>
                    <p class="text-sm font-medium text-gray-dark">${logro.titulo}</p>
                    <p class="text-xs text-gray-medium">${logro.hackathon || 'General'}</p>
                </div>
            </div>
        `;
    }).join('');
}

// Funciones auxiliares
function verDetalleHackathon(idHackathon) {
    // TODO: Implementar modal o redirección a detalle
    console.log('Ver hackathon:', idHackathon);
    showSection('hackathons');
}

function mostrarMensajeNoHackathons() {
    const container = document.getElementById('hackathons-activos-container');
    if (container) {
        container.innerHTML = `
            <div class="glass-card rounded-2xl p-12 text-center">
                <i class="fas fa-info-circle text-6xl text-blue-300 mb-4"></i>
                <h3 class="text-xl font-semibold text-gray-dark mb-2">Sin hackathons activos</h3>
                <p class="text-gray-medium">Inscríbete a un hackathon para comenzar</p>
            </div>
        `;
    }
}
