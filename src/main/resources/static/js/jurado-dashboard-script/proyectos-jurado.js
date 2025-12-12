// ========================================
// PROYECTOS - JURADO DASHBOARD
// ========================================

let todosLosProyectos = [];
let proyectosFiltrados = [];

// Cargar proyectos al iniciar la sección
async function inicializarProyectos() {
    await cargarProyectosDetalle();
}

// Cargar todos los proyectos con detalles
async function cargarProyectosDetalle() {
    try {
        const response = await fetch('/jurado/api/proyectos');
        if (!response.ok) throw new Error('Error al cargar proyectos');

        todosLosProyectos = await response.json();
        proyectosFiltrados = [...todosLosProyectos];

        await cargarHackathonsParaFiltro();
        renderizarProyectos();
        actualizarContadores();
    } catch (error) {
        console.error('Error cargando proyectos:', error);
        mostrarMensajeError('No se pudieron cargar los proyectos');
    }
}

// Cargar hackathons para el selector de filtro
async function cargarHackathonsParaFiltro() {
    try {
        const response = await fetch('/jurado/api/hackathons');
        if (!response.ok) throw new Error('Error al cargar hackathons');

        const hackathons = await response.json();
        const selector = document.getElementById('filtro-hackathon-proyectos');

        if (selector) {
            selector.innerHTML = '<option value="">Todos los hackatones</option>';
            hackathons.forEach(h => {
                selector.innerHTML += `<option value="${h.idHackaton}">${h.nombre}</option>`;
            });
        }
    } catch (error) {
        console.error('Error cargando hackathons:', error);
    }
}

// Renderizar tarjetas de proyectos
function renderizarProyectos() {
    const container = document.getElementById('proyectos-grid');

    if (!container) {
        console.error('Contenedor de proyectos no encontrado');
        return;
    }

    if (proyectosFiltrados.length === 0) {
        container.innerHTML = `
            <div class="col-span-full">
                <div class="judge-card rounded-2xl p-12 text-center">
                    <i class="fas fa-folder-open text-6xl text-gray-300 mb-4"></i>
                    <h3 class="text-xl font-semibold text-gray-dark mb-2">No hay proyectos</h3>
                    <p class="text-gray-medium">No se encontraron proyectos con los filtros aplicados</p>
                </div>
            </div>
        `;
        return;
    }

    container.innerHTML = proyectosFiltrados.map(proyecto => `
        <div class="evaluation-card rounded-xl border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow">
            <div class="p-6">
                <div class="flex items-start justify-between mb-4">
                    <div class="flex-1">
                        <h3 class="font-semibold text-gray-dark mb-1">${proyecto.nombreProyecto || 'Sin nombre'}</h3>
                        <p class="text-sm text-gray-medium">${proyecto.nombreEquipo || 'Sin equipo'}</p>
                    </div>
                    <span class="status-badge ${obtenerClaseEstado(proyecto.estado)}">${obtenerTextoEstado(proyecto.estado)}</span>
                </div>
                
                <div class="space-y-2 mb-4">
                    <div class="flex items-center justify-between text-sm">
                        <span class="text-gray-medium">Hackaton:</span>
                        <span class="font-medium text-gray-dark">${proyecto.nombreHackaton || 'N/A'}</span>
                    </div>
                    <div class="flex items-center justify-between text-sm">
                        <span class="text-gray-medium">Evaluaciones:</span>
                        <span class="font-medium text-gray-dark">${proyecto.evaluaciones_completadas || 0}/${proyecto.total_criterios || 0}</span>
                    </div>
                    ${proyecto.descripcion ? `
                    <div class="mt-3">
                        <p class="text-xs text-gray-medium line-clamp-2">${proyecto.descripcion}</p>
                    </div>
                    ` : ''}
                </div>
                
                <div class="flex gap-2">
                    <button onclick="mostrarDetalleProyecto(${proyecto.idProyecto})" 
                        class="flex-1 bg-primary-blue hover:bg-blue-800 text-white py-2 px-4 rounded-lg font-medium transition-colors text-sm">
                        <i class="fas fa-eye mr-2"></i>Ver Detalles
                    </button>
                    ${proyecto.urlEntregable ? `
                    <a href="${proyecto.urlEntregable}" target="_blank" 
                        class="bg-gray-100 hover:bg-gray-200 text-gray-dark py-2 px-4 rounded-lg font-medium transition-colors text-sm">
                        <i class="fas fa-external-link-alt"></i>
                    </a>
                    ` : ''}
                </div>
            </div>
        </div>
    `).join('');
}

// Filtrar por hackathon
function filtrarPorHackathon(idHackaton) {
    const estadoSeleccionado = document.getElementById('filtro-estado-proyectos')?.value || '';

    proyectosFiltrados = todosLosProyectos.filter(proyecto => {
        const cumpleHackathon = !idHackaton || proyecto.nombreHackaton === obtenerNombreHackathon(idHackaton);
        const cumpleEstado = !estadoSeleccionado || proyecto.estado === estadoSeleccionado;
        return cumpleHackathon && cumpleEstado;
    });

    renderizarProyectos();
    actualizarContadores();
}

// Filtrar por estado
function filtrarPorEstado(estado) {
    const idHackaton = document.getElementById('filtro-hackathon-proyectos')?.value || '';

    proyectosFiltrados = todosLosProyectos.filter(proyecto => {
        const cumpleHackathon = !idHackaton || proyecto.nombreHackaton === obtenerNombreHackathon(idHackaton);
        const cumpleEstado = !estado || proyecto.estado === estado;
        return cumpleHackathon && cumpleEstado;
    });

    renderizarProyectos();
    actualizarContadores();
}

// Mostrar modal con detalle completo del proyecto
async function mostrarDetalleProyecto(idProyecto) {
    try {
        const response = await fetch(`/jurado/api/proyecto/${idProyecto}/detalle`);
        if (!response.ok) throw new Error('Error al cargar detalles');

        const proyecto = await response.json();

        const modal = document.getElementById('modal-detalle-proyecto');
        if (!modal) {
            console.error('Modal no encontrado');
            return;
        }

        // Llenar información del modal
        document.getElementById('modal-proyecto-nombre').textContent = proyecto.nombreProyecto;
        document.getElementById('modal-proyecto-equipo').textContent = proyecto.nombreEquipo;
        document.getElementById('modal-proyecto-hackathon').textContent = proyecto.nombreHackaton;
        document.getElementById('modal-proyecto-descripcion').textContent = proyecto.descripcion || 'Sin descripción';
        document.getElementById('modal-proyecto-estado').textContent = obtenerTextoEstado(proyecto.estado);
        document.getElementById('modal-proyecto-estado').className = `status-badge ${obtenerClaseEstado(proyecto.estado)}`;

        // Evaluaciones
        document.getElementById('modal-proyecto-evaluaciones').textContent =
            `${proyecto.evaluacionesCompletadas}/${proyecto.evaluacionesTotales}`;

        // Puntaje promedio
        const puntaje = proyecto.puntajePromedio || 0;
        document.getElementById('modal-proyecto-puntaje').textContent = puntaje.toFixed(1);

        // Enlaces
        const enlacesContainer = document.getElementById('modal-proyecto-enlaces');
        enlacesContainer.innerHTML = '';

        if (proyecto.urlEntregable) {
            enlacesContainer.innerHTML += `
                <a href="${proyecto.urlEntregable}" target="_blank" 
                    class="bg-primary-blue hover:bg-blue-800 text-white py-2 px-4 rounded-lg font-medium transition-colors text-sm">
                    <i class="fas fa-link mr-2"></i>Ver Entregable
                </a>
            `;
        }

        if (proyecto.urlRepositorio) {
            enlacesContainer.innerHTML += `
                <a href="${proyecto.urlRepositorio}" target="_blank" 
                    class="bg-gray-700 hover:bg-gray-800 text-white py-2 px-4 rounded-lg font-medium transition-colors text-sm">
                    <i class="fab fa-github mr-2"></i>Repositorio
                </a>
            `;
        }

        // Miembros del equipo
        const miembrosContainer = document.getElementById('modal-proyecto-miembros');
        if (proyecto.miembros && proyecto.miembros.length > 0) {
            miembrosContainer.innerHTML = proyecto.miembros.map(miembro => `
                <div class="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
                    <div class="w-10 h-10 professional-gradient rounded-lg flex items-center justify-center text-white font-semibold">
                        ${obtenerIniciales(miembro)}
                    </div>
                    <span class="text-sm font-medium text-gray-dark">${miembro}</span>
                </div>
            `).join('');
        } else {
            miembrosContainer.innerHTML = '<p class="text-sm text-gray-medium">No hay miembros registrados</p>';
        }

        // Mostrar modal
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';

    } catch (error) {
        console.error('Error mostrando detalle:', error);
        alert('No se pudo cargar el detalle del proyecto');
    }
}

// Cerrar modal de detalle
function cerrarDetalleProyecto() {
    const modal = document.getElementById('modal-detalle-proyecto');
    if (modal) {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
}

// Utilidades
function obtenerClaseEstado(estado) {
    const estados = {
        'PENDIENTE': 'status-pending',
        'EN_PROGRESO': 'status-evaluating',
        'COMPLETADO': 'status-completed'
    };
    return estados[estado] || 'status-pending';
}

function obtenerTextoEstado(estado) {
    const estados = {
        'PENDIENTE': 'Pendiente',
        'EN_PROGRESO': 'En Progreso',
        'COMPLETADO': 'Completado'
    };
    return estados[estado] || estado;
}

function obtenerNombreHackathon(idHackaton) {
    const selector = document.getElementById('filtro-hackathon-proyectos');
    if (!selector) return '';
    const option = selector.querySelector(`option[value="${idHackaton}"]`);
    return option ? option.textContent : '';
}

function obtenerIniciales(nombreCompleto) {
    if (!nombreCompleto) return '??';
    const partes = nombreCompleto.trim().split(' ');
    if (partes.length >= 2) {
        return (partes[0][0] + partes[1][0]).toUpperCase();
    }
    return partes[0].substring(0, 2).toUpperCase();
}

function actualizarContadores() {
    const total = proyectosFiltrados.length;
    const completados = proyectosFiltrados.filter(p => p.estado === 'COMPLETADO').length;
    const pendientes = proyectosFiltrados.filter(p => p.estado === 'PENDIENTE').length;

    const contadorElement = document.getElementById('contador-proyectos');
    if (contadorElement) {
        contadorElement.textContent = `Mostrando ${total} proyecto${total !== 1 ? 's' : ''} (${completados} evaluados, ${pendientes} pendientes)`;
    }
}

function mostrarMensajeError(mensaje) {
    const container = document.getElementById('proyectos-grid');
    if (container) {
        container.innerHTML = `
            <div class="col-span-full">
                <div class="judge-card rounded-2xl p-12 text-center">
                    <i class="fas fa-exclamation-triangle text-6xl text-red-300 mb-4"></i>
                    <h3 class="text-xl font-semibold text-gray-dark mb-2">Error</h3>
                    <p class="text-gray-medium">${mensaje}</p>
                </div>
            </div>
        `;
    }
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function () {
    // Configurar filtros si existen
    const filtroHackathon = document.getElementById('filtro-hackathon-proyectos');
    if (filtroHackathon) {
        filtroHackathon.addEventListener('change', (e) => filtrarPorHackathon(e.target.value));
    }

    const filtroEstado = document.getElementById('filtro-estado-proyectos');
    if (filtroEstado) {
        filtroEstado.addEventListener('change', (e) => filtrarPorEstado(e.target.value));
    }
});
