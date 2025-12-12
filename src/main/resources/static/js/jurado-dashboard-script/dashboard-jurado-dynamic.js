let estadisticas = {};
let proyectos = [];
let perfilUsuario = {};

async function cargarEstadisticas() {
    try {
        const response = await fetch('/jurado/api/estadisticas');
        if (!response.ok) {
            console.error('Error al cargar estadísticas');
            return;
        }

        estadisticas = await response.json();
        actualizarEstadisticas();

        await cargarPerfilBasico();
    } catch (error) {
        console.error('Error:', error);
    }
}

async function cargarPerfilBasico() {
    try {
        const response = await fetch('/jurado/api/perfil');
        if (!response.ok) {
            console.error('Error en la respuesta del perfil:', response.status);
            return;
        }

        perfilUsuario = await response.json();
        console.log('Perfil cargado:', perfilUsuario);
        actualizarNombresUsuario();
    } catch (error) {
        console.error('Error al cargar perfil:', error);
    }
}

function actualizarNombresUsuario() {
    console.log('actualizarNombresUsuario llamado con:', perfilUsuario);
    
    if (!perfilUsuario.nombreCompleto) {
        console.warn('No hay nombreCompleto en perfilUsuario');
        return;
    }

    const iniciales = perfilUsuario.iniciales || 'JU';
    const nombreCompleto = perfilUsuario.nombreCompleto;
    const primerNombre = nombreCompleto.split(' ')[0];

    console.log('Actualizando con:', { iniciales, nombreCompleto, primerNombre });

    // Actualizar sidebar
    const sidebarIniciales = document.getElementById('sidebar-iniciales');
    const sidebarNombre = document.getElementById('sidebar-nombre');
    const headerBienvenida = document.getElementById('header-bienvenida');
    
    if (sidebarIniciales) {
        sidebarIniciales.textContent = iniciales;
        console.log('Sidebar iniciales actualizado');
    }
    if (sidebarNombre) {
        sidebarNombre.textContent = nombreCompleto;
        console.log('Sidebar nombre actualizado a:', nombreCompleto);
    }
    if (headerBienvenida) {
        headerBienvenida.textContent = `Bienvenido, ${primerNombre}`;
        console.log('Header bienvenida actualizado');
    }
    
    // Actualizar menú de usuario
    const menuNombre = document.getElementById('menu-nombre-jurado');
    const menuEmail = document.getElementById('menu-email-jurado');
    
    if (menuNombre) menuNombre.textContent = nombreCompleto;
    if (menuEmail && perfilUsuario.correoElectronico) {
        menuEmail.textContent = perfilUsuario.correoElectronico;
    }
    
    // Actualizar perfil header
    const perfilIniciales = document.getElementById('perfil-header-iniciales');
    const perfilNombre = document.getElementById('perfil-header-nombre');
    
    if (perfilIniciales) perfilIniciales.textContent = iniciales;
    if (perfilNombre) perfilNombre.textContent = nombreCompleto;
}

function actualizarEstadisticas() {
    document.getElementById('stat-pendientes').textContent = estadisticas.proyectosPendientes || 0;
    document.getElementById('stat-completadas').textContent = estadisticas.proyectosEvaluados || 0;
    document.getElementById('stat-promedio').textContent = (estadisticas.promedioCalificacion || 0).toFixed(1);
    document.getElementById('stat-hackathons').textContent = estadisticas.hackatonesActivos || 0;

    document.getElementById('header-proyectos-pendientes').textContent = `${estadisticas.proyectosPendientes || 0} proyectos`;
}

async function cargarProyectos() {
    try {
        const response = await fetch('/jurado/api/proyectos');
        if (!response.ok) {
            console.error('Error al cargar proyectos');
            return;
        }

        proyectos = await response.json();
        renderizarProyectosPendientes();
        renderizarTodosProyectos();
        actualizarEstadisticasProgreso();
    } catch (error) {
        console.error('Error:', error);
    }
}

function actualizarEstadisticasProgreso() {
    if (proyectos.length === 0) return;
    
    const completadas = proyectos.filter(p => {
        const total = p.total_criterios || 0;
        const compl = p.evaluaciones_completadas || 0;
        return total > 0 && compl >= total;
    }).length;
    
    const enProgreso = proyectos.filter(p => {
        const total = p.total_criterios || 0;
        const compl = p.evaluaciones_completadas || 0;
        return compl > 0 && compl < total;
    }).length;
    
    const pendientes = proyectos.filter(p => {
        const compl = p.evaluaciones_completadas || 0;
        return compl === 0;
    }).length;
    
    const total = proyectos.length;
    const porcentaje = total > 0 ? Math.round((completadas / total) * 100) : 0;
    
    // Actualizar porcentaje circular
    const progressCircle = document.getElementById('progress-circle-fill');
    const progressText = document.getElementById('progress-percentage');
    if (progressCircle && progressText) {
        const circumference = 251.2;
        const offset = circumference - (porcentaje / 100) * circumference;
        progressCircle.style.strokeDashoffset = offset;
        progressText.textContent = `${porcentaje}%`;
    }
    
    // Actualizar estadísticas
    const statsCompletadas = document.getElementById('stats-completadas');
    const statsEnProgreso = document.getElementById('stats-en-progreso');
    const statsPendientes = document.getElementById('stats-pendientes-widget');
    
    if (statsCompletadas) statsCompletadas.textContent = `${completadas}/${total}`;
    if (statsEnProgreso) statsEnProgreso.textContent = enProgreso;
    if (statsPendientes) statsPendientes.textContent = pendientes;
}

function renderizarProyectosPendientes() {
    const container = document.getElementById('evaluaciones-pendientes-dashboard');
    if (!container) return;

    // Obtener proyectos pendientes y en progreso (primeros 3)
    const pendientes = proyectos.filter(p => {
        const totalCriterios = p.total_criterios || 0;
        const completadas = p.evaluaciones_completadas || 0;
        return completadas < totalCriterios;
    }).slice(0, 3);

    if (pendientes.length === 0) {
        container.innerHTML = `
            <div class="text-center py-8 text-gray-500">
                <i class="fas fa-check-circle text-4xl mb-2"></i>
                <p>No tienes proyectos pendientes</p>
            </div>
        `;
        return;
    }

    container.innerHTML = pendientes.map(proyecto => {
        const totalCriterios = proyecto.total_criterios || 0;
        const completadas = proyecto.evaluaciones_completadas || 0;
        const enProgreso = completadas > 0 && completadas < totalCriterios;
        const progreso = totalCriterios > 0 ? Math.round((completadas / totalCriterios) * 100) : 0;
        
        return `
            <div class="evaluation-card rounded-xl p-4 border border-gray-200">
                <div class="flex items-start justify-between mb-3">
                    <div class="flex-1">
                        <h4 class="font-semibold text-gray-dark mb-1">${proyecto.nombreProyecto || 'Proyecto sin nombre'}</h4>
                        <p class="text-sm text-gray-medium mb-2">Equipo: ${proyecto.nombreEquipo || 'Sin equipo'}</p>
                        <div class="flex items-center space-x-4 text-xs text-gray-medium">
                            <span><i class="fas fa-calendar mr-1"></i>${proyecto.nombreHackathon || proyecto.nombreHackaton || 'Sin hackathon'}</span>
                        </div>
                    </div>
                    <div class="flex flex-col items-end space-y-2">
                        <span class="status-badge ${enProgreso ? 'status-evaluating' : 'status-pending'}">
                            ${enProgreso ? 'En Evaluación' : 'Pendiente'}
                        </span>
                        <button onclick="evaluarProyectoDesdeId(${proyecto.idProyecto})" 
                                class="text-${enProgreso ? 'secondary-cyan hover:text-cyan-600' : 'primary-blue hover:text-blue-800'} font-medium text-sm">
                            ${enProgreso ? 'Continuar' : 'Evaluar'} <i class="fas fa-arrow-right ml-1"></i>
                        </button>
                    </div>
                </div>
                ${enProgreso ? `
                    <div class="flex items-center justify-between">
                        <div class="flex-1">
                            <p class="text-xs text-gray-medium mb-1">Progreso</p>
                            <div class="w-full bg-gray-200 rounded-full h-2">
                                <div class="evaluation-progress h-2 rounded-full" style="width: ${progreso}%"></div>
                            </div>
                        </div>
                        <span class="ml-3 text-xs font-medium text-secondary-cyan">${progreso}%</span>
                    </div>
                ` : ''}
            </div>
        `;
    }).join('');
}

function renderizarTodosProyectos() {
    const container = document.getElementById('todos-proyectos-container');
    if (!container) return;

    if (proyectos.length === 0) {
        container.innerHTML = `
            <div class="text-center py-12 text-gray-500">
                <i class="fas fa-inbox text-6xl mb-4"></i>
                <p>No tienes proyectos asignados</p>
            </div>
        `;
        return;
    }

    container.innerHTML = proyectos.map(proyecto => {
        const estadoBadge = proyecto.estado === 'COMPLETADO' ? 'status-completed' :
            proyecto.estado === 'EN_PROGRESO' ? 'status-evaluating' : 'status-pending';
        const estadoTexto = proyecto.estado === 'COMPLETADO' ? 'Completada' :
            proyecto.estado === 'EN_PROGRESO' ? 'En Progreso' : 'Pendiente';
        const botonTexto = proyecto.estado === 'COMPLETADO' ? 'Ver Evaluación' :
            proyecto.estado === 'EN_PROGRESO' ? 'Continuar' : 'Iniciar Evaluación';

        return `
            <div class="evaluation-card rounded-xl border border-gray-200 overflow-hidden">
                <div class="p-6">
                    <div class="flex items-start justify-between mb-4">
                        <div class="flex-1">
                            <h3 class="font-semibold text-gray-dark mb-1">${proyecto.nombreProyecto}</h3>
                            <p class="text-sm text-gray-medium">${proyecto.nombreEquipo}</p>
                        </div>
                        <span class="status-badge ${estadoBadge}">${estadoTexto}</span>
                    </div>
                    
                    <div class="space-y-3 mb-4">
                        <div class="flex items-center justify-between text-sm">
                            <span class="text-gray-medium">Hackaton:</span>
                            <span class="font-medium text-gray-dark">${proyecto.nombreHackaton}</span>
                        </div>
                        <div class="flex items-center justify-between text-sm">
                            <span class="text-gray-medium">Progreso:</span>
                            <span class="font-medium text-secondary-cyan">${proyecto.evaluaciones_completadas}/${proyecto.total_criterios}</span>
                        </div>
                    </div>

                    <button onclick="evaluarProyectoDesdeId(${proyecto.idProyecto})" 
                            class="w-full bg-primary-blue hover:bg-blue-800 text-white py-3 px-4 rounded-lg font-medium transition-colors">
                        <i class="fas fa-clipboard-check mr-2"></i>${botonTexto}
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

async function evaluarProyectoDesdeId(idProyecto) {
    const proyecto = proyectos.find(p => p.idProyecto === idProyecto);
    if (!proyecto) return;

    try {
        const [criteriosResp, evaluacionesResp] = await Promise.all([
            fetch('/jurado/api/criterios'),
            fetch(`/jurado/api/evaluaciones/proyecto/${idProyecto}`)
        ]);

        const criterios = await criteriosResp.json();
        const evaluacionesExistentes = await evaluacionesResp.json();

        mostrarModalEvaluacion(proyecto, criterios, evaluacionesExistentes);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar datos para evaluación');
    }
}

function mostrarModalEvaluacion(proyecto, criterios, evaluacionesExistentes) {
    document.getElementById('eval-proyecto-nombre').textContent = proyecto.nombreProyecto;
    document.getElementById('eval-equipo-nombre').textContent = proyecto.nombreEquipo;
    document.getElementById('eval-proyecto-id').value = proyecto.idProyecto;

    const contenedor = document.getElementById('criterios-evaluacion-container');
    contenedor.innerHTML = criterios.map(criterio => {
        const evaluacionExistente = evaluacionesExistentes.find(e => e.idCriterio === criterio.idCriterio);
        const puntuacion = evaluacionExistente ? evaluacionExistente.puntuacion : '';

        return `
            <div class="border border-gray-200 rounded-lg p-4">
                <div class="flex justify-between items-start mb-2">
                    <div>
                        <h4 class="font-medium text-gray-dark">${criterio.nombreCriterio}</h4>
                        <p class="text-sm text-gray-500">${criterio.descripcion || ''}</p>
                    </div>
                    <span class="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded">${criterio.pesoFormatted}</span>
                </div>
                <div class="flex items-center space-x-2">
                    <input type="number" 
                           min="0" 
                           max="5" 
                           step="0.1" 
                           value="${puntuacion}"
                           placeholder="0.0"
                           data-criterio-id="${criterio.idCriterio}"
                           class="puntuacion-input w-24 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-blue">
                    <span class="text-gray-500">/5.0</span>
                    ${evaluacionExistente ? `<span class="text-green-600 text-sm"><i class="fas fa-check-circle"></i> Evaluado</span>` : ''}
                </div>
            </div>
        `;
    }).join('');

    // Mostrar el modal con el ID correcto
    const modal = document.getElementById('modal-evaluacion');
    if (modal) {
        modal.classList.remove('hidden');
    }
    
    // Event listener para el formulario
    const formEvaluacion = document.getElementById('form-evaluacion');
    if (formEvaluacion) {
        formEvaluacion.onsubmit = async (e) => {
            e.preventDefault();
            await guardarEvaluacionJurado();
        };
    }
}

async function guardarEvaluacionJurado() {
    const idProyecto = document.getElementById('eval-proyecto-id').value;
    const inputs = document.querySelectorAll('.puntuacion-input');

    let guardadas = 0;
    for (let input of inputs) {
        const puntuacion = parseFloat(input.value);
        if (!puntuacion || puntuacion < 0 || puntuacion > 5) continue;

        const datos = {
            idProyecto: parseInt(idProyecto),
            idCriterio: parseInt(input.dataset.criterioId),
            puntuacion: puntuacion
        };

        try {
            const response = await fetch('/jurado/api/evaluaciones', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datos)
            });

            const data = await response.json();
            if (data.success) guardadas++;
        } catch (error) {
            console.error('Error:', error);
        }
    }

    cerrarModalEvaluacion();
    await cargarEstadisticas();
    await cargarProyectos();
    alert(`${guardadas} evaluaciones guardadas exitosamente`);
}

function cerrarModalEvaluacion() {
    const modal = document.getElementById('modal-evaluacion');
    if (modal) {
        modal.classList.add('hidden');
    }
}

window.openEvaluationModal = function (proyectoNombre) {
    const proyecto = proyectos.find(p => p.nombreProyecto.includes(proyectoNombre));
    if (proyecto) {
        evaluarProyectoDesdeId(proyecto.idProyecto);
    }
};

document.addEventListener('DOMContentLoaded', () => {
    cargarEstadisticas();
    cargarProyectos();
    setInterval(() => {
        cargarEstadisticas();
    }, 30000);
});
