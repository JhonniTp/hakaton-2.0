// =============================================
// MIS PROYECTOS - Carga dinámica
// =============================================

document.addEventListener('DOMContentLoaded', function() {
    // Cargar cuando cambie a la sección (la navegación la maneja dashboard-participante.js)
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            const projectsSection = document.getElementById('projects-section');
            if (projectsSection && !projectsSection.classList.contains('hidden')) {
                cargarMisProyectos();
            }
        });
    });
    
    const projectsSection = document.getElementById('projects-section');
    if (projectsSection) {
        observer.observe(projectsSection, { attributes: true, attributeFilter: ['class'] });
    }
});

let proyectosData = [];

async function cargarMisProyectos() {
    try {
        const response = await fetch('/participante/api/mis-proyectos');
        const proyectos = await response.json();
        
        console.log('📁 Mis proyectos:', proyectos);
        proyectosData = proyectos;
        
        const container = document.getElementById('mis-proyectos-container');
        if (!container) return;
        
        // Actualizar estadísticas
        const totalProyectos = proyectos.length;
        const enProgreso = proyectos.filter(p => p.estado === 'EN_PROGRESO').length;
        const evaluados = proyectos.filter(p => p.estado === 'EVALUADO').length;
        
        const statTotal = document.getElementById('stat-total-proyectos');
        const statProgreso = document.getElementById('stat-proyectos-progreso');
        const statEvaluados = document.getElementById('stat-proyectos-evaluados');
        
        if (statTotal) statTotal.textContent = totalProyectos;
        if (statProgreso) statProgreso.textContent = enProgreso;
        if (statEvaluados) statEvaluados.textContent = evaluados;
        
        // Configurar filtros
        configurarFiltrosProyectos();
        
        if (!proyectos || proyectos.length === 0) {
            container.innerHTML = `
                <div class="col-span-full text-center py-16">
                    <div class="mb-6">
                        <i class="fas fa-folder-open text-8xl text-gray-300 mb-4"></i>
                    </div>
                    <h3 class="text-2xl font-bold text-gray-600 mb-2">No tienes proyectos</h3>
                    <p class="text-gray-400 text-sm mt-2 mb-8">Los proyectos se crean automáticamente al formar un equipo en un hackathon</p>
                    <button onclick="showSection('hackathons')" class="bg-gradient-to-r from-blue-600 to-cyan-600 text-white px-8 py-4 rounded-xl font-semibold hover:shadow-lg transform hover:scale-105 transition-all">
                        <i class="fas fa-search mr-2"></i>Explorar Hackathons
                    </button>
                </div>
            `;
            return;
        }
        
        renderizarProyectos(proyectos);
        
    } catch (error) {
        console.error('Error al cargar proyectos:', error);
    }
}

function renderizarProyectos(proyectos) {
    const container = document.getElementById('mis-proyectos-container');
    if (!container) return;
    
    container.innerHTML = proyectos.map((proyecto, index) => `
        <div class="bg-white rounded-xl shadow-md overflow-hidden hover-lift card-animated" 
             data-proyecto-id="${proyecto.idProyecto}"
             data-estado="${proyecto.estado}"
             data-url-entregable="${proyecto.urlEntregable || ''}"
             data-url-presentacion="${proyecto.urlPresentacion || ''}"
             style="animation-delay: ${index * 0.1}s">
            <!-- Header mejorado -->
            <div class="bg-gradient-to-br from-blue-600 via-cyan-600 to-teal-500 p-6 text-white relative overflow-hidden">
                <div class="absolute inset-0 bg-black/10"></div>
                <div class="relative z-10">
                    <div class="flex items-start justify-between mb-3">
                        <div class="flex-1">
                            <h3 class="text-2xl font-bold mb-2 drop-shadow-lg">${proyecto.nombreProyecto}</h3>
                            <p class="text-white/90 text-sm mb-1 flex items-center">
                                <i class="fas fa-trophy mr-2"></i>${proyecto.nombreHackathon}
                            </p>
                            <p class="text-white/80 text-xs flex items-center">
                                <i class="fas fa-users mr-2"></i>Equipo: ${proyecto.nombreEquipo}
                            </p>
                        </div>
                        <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${getEstadoBadge(proyecto.estado)} border border-white/30">
                            ${getEstadoTexto(proyecto.estado)}
                        </span>
                    </div>
                </div>
            </div>
                
                <!-- Body -->
                <div class="p-6">
                    ${proyecto.descripcion ? 
                        `<p class="text-gray-600 text-sm mb-4 line-clamp-2">${proyecto.descripcion}</p>` : 
                        '<p class="text-gray-400 text-sm mb-4 italic">Sin descripción</p>'
                    }
                    
                    <div class="space-y-2 mb-4">
                        ${proyecto.urlEntregable ? 
                            `<div class="flex items-center text-sm text-gray-600">
                                <i class="fas fa-link mr-2 text-blue-600"></i>
                                <a href="${proyecto.urlEntregable}" target="_blank" class="text-blue-600 hover:underline truncate">
                                    Entregable
                                </a>
                            </div>` : ''
                        }
                        ${proyecto.urlPresentacion ? 
                            `<div class="flex items-center text-sm text-gray-600">
                                <i class="fas fa-presentation mr-2 text-purple-600"></i>
                                <a href="${proyecto.urlPresentacion}" target="_blank" class="text-blue-600 hover:underline truncate">
                                    Presentación
                                </a>
                            </div>` : ''
                        }
                        ${proyecto.puntuacionPromedio ? 
                            `<div class="flex items-center text-sm text-gray-600">
                                <i class="fas fa-star mr-2 text-yellow-500"></i>
                                <span class="font-semibold">${proyecto.puntuacionPromedio.toFixed(1)} / 5.0</span>
                            </div>` : ''
                        }
                        ${proyecto.fechaEntrega ? 
                            `<div class="flex items-center text-sm text-gray-600">
                                <i class="far fa-calendar mr-2 text-green-600"></i>
                                <span>Entregado el ${formatDate(proyecto.fechaEntrega)}</span>
                            </div>` : ''
                        }
                    </div>
                    
                    <div class="flex gap-2">
                        <button onclick="editarProyecto(${proyecto.idProyecto})" 
                                class="flex-1 bg-gradient-to-r from-blue-600 to-cyan-600 text-white py-2 rounded-lg font-semibold hover:shadow-lg transition-all">
                            <i class="fas fa-edit mr-2"></i>Editar
                        </button>
                        <button onclick="verProyectoDetalle(${proyecto.idProyecto})" 
                                class="flex-1 bg-gray-100 text-gray-700 py-2 rounded-lg font-semibold hover:bg-gray-200 transition-all">
                            <i class="fas fa-eye mr-2"></i>Ver más
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
}

function configurarFiltrosProyectos() {
    const filterButtons = document.querySelectorAll('.project-filter-btn');
    filterButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            filterButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            
            const estado = btn.dataset.estado;
            filtrarProyectos(estado);
        });
    });
}

function filtrarProyectos(estado) {
    if (estado === 'todos') {
        renderizarProyectos(proyectosData);
    } else {
        const filtrados = proyectosData.filter(p => p.estado === estado);
        renderizarProyectos(filtrados);
    }
}

function getEstadoBadge(estado) {
    switch(estado) {
        case 'EN_PROGRESO': return 'bg-yellow-100 text-yellow-800';
        case 'ENTREGADO': return 'bg-blue-100 text-blue-800';
        case 'EVALUADO': return 'bg-green-100 text-green-800';
        default: return 'bg-gray-100 text-gray-800';
    }
}

function getEstadoTexto(estado) {
    switch(estado) {
        case 'EN_PROGRESO': return '⏳ En progreso';
        case 'ENTREGADO': return '📤 Entregado';
        case 'EVALUADO': return '✅ Evaluado';
        default: return estado;
    }
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
}

function editarProyecto(idProyecto) {
    // Obtener datos actuales del proyecto
    const proyectoCard = document.querySelector(`[data-proyecto-id="${idProyecto}"]`);
    const urlEntregableActual = proyectoCard?.dataset?.urlEntregable || '';
    const urlPresentacionActual = proyectoCard?.dataset?.urlPresentacion || '';
    
    // Abrir modal con datos actuales
    document.getElementById('proyecto-id-editar').value = idProyecto;
    document.getElementById('url-entregable').value = urlEntregableActual;
    document.getElementById('url-presentacion').value = urlPresentacionActual;
    
    // Mostrar modal
    document.getElementById('modal-editar-proyecto').classList.remove('hidden');
}

function cerrarModalEditarProyecto() {
    const modal = document.getElementById('modal-editar-proyecto');
    modal.classList.add('opacity-0');
    
    setTimeout(() => {
        modal.classList.add('hidden');
        modal.classList.remove('opacity-0');
        document.getElementById('form-editar-proyecto').reset();
    }, 300);
}

// Event listener para el formulario de edición
document.getElementById('form-editar-proyecto')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const idProyecto = document.getElementById('proyecto-id-editar').value;
    const urlEntregable = document.getElementById('url-entregable').value;
    const urlPresentacion = document.getElementById('url-presentacion').value;
    
    await actualizarProyecto(idProyecto, {
        urlEntregable: urlEntregable,
        urlPresentacion: urlPresentacion || null
    });
});

async function actualizarProyecto(idProyecto, datos) {
    try {
        const response = await fetch(`/participante/api/proyectos/${idProyecto}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });
        
        const result = await response.json();
        
        if (result.success) {
            mostrarModal('success', 'Enlaces Actualizados', result.mensaje || 'Los enlaces del proyecto se han actualizado correctamente');
            cerrarModalEditarProyecto();
            cargarMisProyectos();
        } else {
            mostrarModal('error', 'Error', result.mensaje || 'No se pudieron actualizar los enlaces');
        }
    } catch (error) {
        console.error('Error al actualizar proyecto:', error);
        mostrarModal('error', 'Error de Conexión', 'No se pudo conectar con el servidor');
    }
}

function verProyectoDetalle(idProyecto) {
    alert(`Ver detalle proyecto ID: ${idProyecto}`);
}
