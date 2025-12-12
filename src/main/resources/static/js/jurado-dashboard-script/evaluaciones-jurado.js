// =============================================
// EVALUACIONES JURADO - Carga dinámica
// =============================================

document.addEventListener('DOMContentLoaded', function() {
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            const evaluationsSection = document.getElementById('evaluations-section');
            if (evaluationsSection && !evaluationsSection.classList.contains('hidden')) {
                cargarEvaluaciones();
            }
        });
    });
    
    const evaluationsSection = document.getElementById('evaluations-section');
    if (evaluationsSection) {
        observer.observe(evaluationsSection, { attributes: true, attributeFilter: ['class'] });
    }
});

async function cargarEvaluaciones() {
    try {
        const response = await fetch('/jurado/api/proyectos');
        const proyectos = await response.json();
        
        console.log('📋 Proyectos para evaluar:', proyectos);
        
        const container = document.getElementById('evaluaciones-container');
        if (!container) return;
        
        if (!proyectos || proyectos.length === 0) {
            container.innerHTML = `
                <div class="col-span-full text-center py-16">
                    <i class="fas fa-clipboard-list text-8xl text-gray-300 mb-4"></i>
                    <p class="text-gray-500 text-xl">No hay proyectos para evaluar</p>
                    <p class="text-gray-400 text-sm mt-2">No estás asignado a ninguna hackathon con proyectos activos</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = proyectos.map(proyecto => {
            const estado = calcularEstadoEvaluacion(proyecto);
            const badge = obtenerBadgeEstado(estado);
            const boton = obtenerBotonAccion(proyecto, estado);
            
            return `
                <div class="evaluation-card rounded-xl border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow">
                    <div class="p-6">
                        <div class="flex items-start justify-between mb-4">
                            <div class="flex-1">
                                <h3 class="font-semibold text-gray-dark mb-1">${proyecto.nombreProyecto || 'Sin nombre'}</h3>
                                <p class="text-sm text-gray-medium">${proyecto.nombreEquipo || 'Sin equipo'}</p>
                            </div>
                            ${badge}
                        </div>

                        <div class="space-y-3 mb-4">
                            <div class="flex items-center justify-between text-sm">
                                <span class="text-gray-medium">Hackaton:</span>
                                <span class="font-medium text-gray-dark">${proyecto.nombreHackathon || 'N/A'}</span>
                            </div>
                            ${proyecto.fechaEntrega ? `
                                <div class="flex items-center justify-between text-sm">
                                    <span class="text-gray-medium">Fecha límite:</span>
                                    <span class="font-medium ${esFechaProxima(proyecto.fechaEntrega) ? 'text-red-600' : 'text-gray-dark'}">${formatDate(proyecto.fechaEntrega)}</span>
                                </div>
                            ` : ''}
                            ${estado === 'EN_PROGRESO' ? `
                                <div class="flex items-center justify-between text-sm">
                                    <span class="text-gray-medium">Progreso:</span>
                                    <span class="font-medium text-secondary-cyan">${calcularProgreso(proyecto)}% completado</span>
                                </div>
                            ` : ''}
                            ${proyecto.puntuacionPromedio && estado === 'COMPLETADA' ? `
                                <div class="flex items-center justify-between text-sm">
                                    <span class="text-gray-medium">Puntuación:</span>
                                    <span class="font-bold text-judge-gold">${proyecto.puntuacionPromedio.toFixed(1)}/5.0</span>
                                </div>
                            ` : ''}
                        </div>

                        ${estado === 'EN_PROGRESO' ? `
                            <div class="mb-4">
                                <div class="w-full bg-gray-200 rounded-full h-2">
                                    <div class="evaluation-progress h-2 rounded-full" style="width: ${calcularProgreso(proyecto)}%"></div>
                                </div>
                            </div>
                        ` : ''}

                        ${proyecto.puntuacionPromedio && estado === 'COMPLETADA' ? `
                            <div class="mb-4">
                                <div class="flex justify-center">
                                    ${generarEstrellas(proyecto.puntuacionPromedio)}
                                </div>
                            </div>
                        ` : ''}

                        ${boton}
                    </div>
                </div>
            `;
        }).join('');
        
    } catch (error) {
        console.error('Error al cargar evaluaciones:', error);
        const container = document.getElementById('evaluaciones-container');
        if (container) {
            container.innerHTML = `
                <div class="col-span-full text-center py-16">
                    <i class="fas fa-exclamation-triangle text-8xl text-red-300 mb-4"></i>
                    <p class="text-red-500 text-xl">Error al cargar evaluaciones</p>
                    <p class="text-gray-400 text-sm mt-2">Por favor, intenta recargar la página</p>
                </div>
            `;
        }
    }
}

function calcularEstadoEvaluacion(proyecto) {
    if (!proyecto.evaluaciones_completadas && !proyecto.total_criterios) {
        return 'PENDIENTE';
    }
    
    if (proyecto.evaluaciones_completadas >= proyecto.total_criterios) {
        return 'COMPLETADA';
    }
    
    if (proyecto.evaluaciones_completadas > 0) {
        return 'EN_PROGRESO';
    }
    
    return 'PENDIENTE';
}

function obtenerBadgeEstado(estado) {
    switch(estado) {
        case 'PENDIENTE':
            return '<span class="status-badge status-pending">Pendiente</span>';
        case 'EN_PROGRESO':
            return '<span class="status-badge status-evaluating">En Progreso</span>';
        case 'COMPLETADA':
            return '<span class="status-badge status-completed">Completada</span>';
        default:
            return '<span class="status-badge status-pending">Pendiente</span>';
    }
}

function obtenerBotonAccion(proyecto, estado) {
    switch(estado) {
        case 'PENDIENTE':
            return `
                <button onclick="evaluarProyectoDesdeId(${proyecto.idProyecto})"
                    class="w-full bg-primary-blue hover:bg-blue-800 text-white py-3 px-4 rounded-lg font-medium transition-colors">
                    <i class="fas fa-clipboard-check mr-2"></i>Iniciar Evaluación
                </button>
            `;
        case 'EN_PROGRESO':
            return `
                <button onclick="evaluarProyectoDesdeId(${proyecto.idProyecto})"
                    class="w-full bg-secondary-cyan hover:bg-cyan-600 text-white py-3 px-4 rounded-lg font-medium transition-colors">
                    <i class="fas fa-edit mr-2"></i>Continuar Evaluación
                </button>
            `;
        case 'COMPLETADA':
            return `
                <button onclick="mostrarDetalleProyecto(${proyecto.idProyecto})"
                    class="w-full bg-gray-100 hover:bg-gray-200 text-gray-dark py-3 px-4 rounded-lg font-medium transition-colors">
                    <i class="fas fa-eye mr-2"></i>Ver Evaluación
                </button>
            `;
        default:
            return '';
    }
}

function calcularProgreso(proyecto) {
    if (!proyecto.total_criterios || proyecto.total_criterios === 0) return 0;
    return Math.round((proyecto.evaluaciones_completadas / proyecto.total_criterios) * 100);
}

function esFechaProxima(fechaStr) {
    const fecha = new Date(fechaStr);
    const ahora = new Date();
    const diferenciaDias = Math.ceil((fecha - ahora) / (1000 * 60 * 60 * 24));
    return diferenciaDias <= 3;
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
}

function generarEstrellas(puntuacion) {
    const estrellas = Math.floor(puntuacion);
    const mediaEstrella = puntuacion % 1 >= 0.5;
    
    let html = '<div class="flex text-yellow-400">';
    
    for (let i = 0; i < estrellas; i++) {
        html += '<i class="fas fa-star"></i>';
    }
    
    if (mediaEstrella) {
        html += '<i class="fas fa-star-half-alt"></i>';
    }
    
    const estrellasVacias = 5 - estrellas - (mediaEstrella ? 1 : 0);
    for (let i = 0; i < estrellasVacias; i++) {
        html += '<i class="far fa-star"></i>';
    }
    
    html += '</div>';
    return html;
}
