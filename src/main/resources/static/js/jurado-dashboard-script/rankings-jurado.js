// ========================================
// RANKINGS - JURADO DASHBOARD
// ========================================

let hackathonsDisponibles = [];
let rankingActual = [];

// Inicializar sección de rankings
async function inicializarRankings() {
    await cargarHackathonsParaRanking();
}

// Cargar hackathons para el selector
async function cargarHackathonsParaRanking() {
    try {
        const response = await fetch('/jurado/api/hackathons');
        if (!response.ok) throw new Error('Error al cargar hackathons');

        hackathonsDisponibles = await response.json();

        const selector = document.getElementById('selector-hackathon-ranking');
        if (!selector) {
            console.error('Selector de hackathon no encontrado');
            return;
        }

        if (hackathonsDisponibles.length === 0) {
            selector.innerHTML = '<option value="">No hay hackathons disponibles</option>';
            mostrarMensajeVacio('No tienes hackathons asignados');
            return;
        }

        selector.innerHTML = '<option value="">Selecciona un hackathon...</option>';
        hackathonsDisponibles.forEach(h => {
            selector.innerHTML += `<option value="${h.idHackaton}">${h.nombre}</option>`;
        });

        // Cargar el primer hackathon automáticamente
        if (hackathonsDisponibles.length > 0) {
            selector.value = hackathonsDisponibles[0].idHackaton;
            await cargarRanking(hackathonsDisponibles[0].idHackaton);
        }

    } catch (error) {
        console.error('Error cargando hackathons:', error);
        mostrarMensajeError('No se pudieron cargar los hackathons');
    }
}

// Cargar ranking de un hackathon específico
async function cargarRanking(idHackaton) {
    if (!idHackaton) {
        mostrarMensajeVacio('Selecciona un hackathon para ver el ranking');
        return;
    }

    try {
        mostrarCargando();

        const response = await fetch(`/jurado/api/rankings/hackathon/${idHackaton}`);
        if (!response.ok) throw new Error('Error al cargar ranking');

        rankingActual = await response.json();
        renderizarTablaRanking(rankingActual);

    } catch (error) {
        console.error('Error cargando ranking:', error);
        mostrarMensajeError('No se pudo cargar el ranking de este hackathon');
    }
}

// Renderizar tabla de ranking
function renderizarTablaRanking(datos) {
    const tbody = document.getElementById('ranking-tbody');

    if (!tbody) {
        console.error('Tabla de ranking no encontrada');
        return;
    }

    if (datos.length === 0) {
        mostrarMensajeVacio('No hay proyectos para mostrar en el ranking');
        return;
    }

    tbody.innerHTML = datos.map((proyecto, index) => {
        const medalla = obtenerMedalla(proyecto.posicion);
        const puntaje = proyecto.puntajeFinal || 0;
        const progreso = proyecto.totalEvaluaciones > 0
            ? Math.round((proyecto.evaluacionesCompletadas / proyecto.totalEvaluaciones) * 100)
            : 0;

        return `
            <tr class="border-b border-gray-200 hover:bg-gray-50 transition-colors">
                <td class="px-6 py-4">
                    <div class="flex items-center space-x-3">
                        <span class="text-2xl">${medalla}</span>
                        <span class="text-lg font-bold ${proyecto.posicion <= 3 ? 'text-judge-gold' : 'text-gray-dark'}">${proyecto.posicion}</span>
                    </div>
                </td>
                <td class="px-6 py-4">
                    <div>
                        <h4 class="font-semibold text-gray-dark">${proyecto.nombreProyecto}</h4>
                        <p class="text-sm text-gray-medium">${proyecto.nombreEquipo}</p>
                    </div>
                </td>
                <td class="px-6 py-4 text-center">
                    <div class="flex items-center justify-center">
                        <div class="text-center">
                            <div class="text-2xl font-bold ${obtenerColorPuntaje(puntaje)}">${puntaje.toFixed(1)}</div>
                            <div class="flex justify-center mt-1">
                                ${generarEstrellas(puntaje)}
                            </div>
                        </div>
                    </div>
                </td>
                <td class="px-6 py-4">
                    <div class="flex items-center space-x-3">
                        <div class="flex-1">
                            <div class="w-full bg-gray-200 rounded-full h-2">
                                <div class="evaluation-progress h-2 rounded-full" style="width: ${progreso}%"></div>
                            </div>
                        </div>
                        <span class="text-sm font-medium text-gray-dark whitespace-nowrap">${proyecto.evaluacionesCompletadas}/${proyecto.totalEvaluaciones}</span>
                    </div>
                </td>
                <td class="px-6 py-4 text-center">
                    <span class="status-badge ${obtenerClaseEstadoRanking(proyecto.estado)}">${obtenerTextoEstadoRanking(proyecto.estado)}</span>
                </td>
            </tr>
        `;
    }).join('');

    // Actualizar estadísticas
    actualizarEstadisticasRanking(datos);
}

// Obtener medalla según posición
function obtenerMedalla(posicion) {
    switch (posicion) {
        case 1: return '🥇';
        case 2: return '🥈';
        case 3: return '🥉';
        default: return '📊';
    }
}

// Generar estrellas visuales según puntaje
function generarEstrellas(puntaje) {
    const estrellas = Math.round(puntaje / 2); // Convertir de 10 a 5 estrellas
    let html = '';

    for (let i = 1; i <= 5; i++) {
        if (i <= estrellas) {
            html += '<i class="fas fa-star text-yellow-400 text-xs"></i>';
        } else if (i === estrellas + 1 && puntaje % 2 >= 1) {
            html += '<i class="fas fa-star-half-alt text-yellow-400 text-xs"></i>';
        } else {
            html += '<i class="far fa-star text-gray-300 text-xs"></i>';
        }
    }

    return html;
}

// Obtener color según puntaje
function obtenerColorPuntaje(puntaje) {
    if (puntaje >= 8) return 'text-green-600';
    if (puntaje >= 6) return 'text-blue-600';
    if (puntaje >= 4) return 'text-yellow-600';
    return 'text-red-600';
}

// Obtener clase de estado para ranking
function obtenerClaseEstadoRanking(estado) {
    return estado === 'COMPLETADO' ? 'status-completed' : 'status-pending';
}

// Obtener texto de estado para ranking
function obtenerTextoEstadoRanking(estado) {
    return estado === 'COMPLETADO' ? 'Completo' : 'Pendiente';
}

// Actualizar estadísticas del ranking
function actualizarEstadisticasRanking(datos) {
    if (datos.length === 0) return;

    const puntajes = datos.map(p => p.puntajeFinal || 0);
    const promedio = puntajes.reduce((a, b) => a + b, 0) / puntajes.length;
    const max = Math.max(...puntajes);
    const min = Math.min(...puntajes);

    // Actualizar cards de estadísticas
    const promedioElement = document.getElementById('ranking-promedio');
    const maxElement = document.getElementById('ranking-max');
    const minElement = document.getElementById('ranking-min');
    const totalElement = document.getElementById('ranking-total');

    if (promedioElement) promedioElement.textContent = promedio.toFixed(1);
    if (maxElement) maxElement.textContent = max.toFixed(1);
    if (minElement) minElement.textContent = min.toFixed(1);
    if (totalElement) totalElement.textContent = datos.length;
}

// Mostrar mensaje de carga
function mostrarCargando() {
    const tbody = document.getElementById('ranking-tbody');
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-12 text-center">
                    <div class="flex flex-col items-center">
                        <i class="fas fa-spinner fa-spin text-4xl text-primary-blue mb-4"></i>
                        <p class="text-gray-medium">Cargando ranking...</p>
                    </div>
                </td>
            </tr>
        `;
    }
}

// Mostrar mensaje vacío
function mostrarMensajeVacio(mensaje) {
    const tbody = document.getElementById('ranking-tbody');
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-12 text-center">
                    <div class="flex flex-col items-center">
                        <i class="fas fa-trophy text-6xl text-gray-300 mb-4"></i>
                        <h3 class="text-xl font-semibold text-gray-dark mb-2">Sin datos</h3>
                        <p class="text-gray-medium">${mensaje}</p>
                    </div>
                </td>
            </tr>
        `;
    }
}

// Mostrar mensaje de error
function mostrarMensajeError(mensaje) {
    const tbody = document.getElementById('ranking-tbody');
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-12 text-center">
                    <div class="flex flex-col items-center">
                        <i class="fas fa-exclamation-triangle text-6xl text-red-300 mb-4"></i>
                        <h3 class="text-xl font-semibold text-gray-dark mb-2">Error</h3>
                        <p class="text-gray-medium">${mensaje}</p>
                    </div>
                </td>
            </tr>
        `;
    }
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function () {
    const selector = document.getElementById('selector-hackathon-ranking');
    if (selector) {
        selector.addEventListener('change', (e) => cargarRanking(e.target.value));
    }
});
