let proyectos = [];
let evaluaciones = [];

async function cargarProyectos() {
    try {
        const response = await fetch('/api/admin/proyectos');
        if (!response.ok) {
            console.error('Error al cargar proyectos');
            return;
        }

        proyectos = await response.json();
        renderizarProyectos();
    } catch (error) {
        console.error('Error:', error);
    }
}

async function cargarEvaluaciones() {
    try {
        const response = await fetch('/api/admin/evaluaciones');
        if (!response.ok) {
            console.error('Error al cargar evaluaciones');
            return;
        }

        evaluaciones = await response.json();
        renderizarEvaluaciones();
    } catch (error) {
        console.error('Error:', error);
    }
}

function renderizarProyectos() {
    const tbody = document.getElementById('tabla-proyectos-body');
    if (!tbody) return;

    if (proyectos.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                    <i class="fas fa-inbox text-4xl mb-2 block"></i>
                    <p>No hay proyectos disponibles</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = proyectos.map(proyecto => {
        const progreso = `${proyecto.evaluaciones_completadas || 0}/${proyecto.total_criterios || 0}`;
        const porcentaje = proyecto.total_criterios > 0
            ? (proyecto.evaluaciones_completadas / proyecto.total_criterios) * 100
            : 0;

        return `
            <tr class="hover:bg-gray-50">
                <td class="px-6 py-4 text-sm font-medium text-gray-900">${proyecto.nombreProyecto}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${proyecto.nombreEquipo}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${proyecto.nombreHackaton}</td>
                <td class="px-6 py-4 text-sm">
                    <div class="flex items-center">
                        <div class="w-full bg-gray-200 rounded-full h-2 mr-2">
                            <div class="bg-blue-600 h-2 rounded-full" style="width: ${porcentaje}%"></div>
                        </div>
                        <span class="text-xs text-gray-600">${progreso}</span>
                    </div>
                </td>
                <td class="px-6 py-4 text-sm text-gray-900">${proyecto.puntajeFinal || '-'}</td>
                <td class="px-6 py-4 text-right text-sm">
                    <button onclick="evaluarProyecto(${proyecto.idProyecto})" 
                            class="text-blue-600 hover:text-blue-800 font-medium">
                        <i class="fas fa-star mr-1"></i>Evaluar
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

function renderizarEvaluaciones() {
    const tbody = document.getElementById('tabla-evaluaciones-body');
    if (!tbody) return;

    if (evaluaciones.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                    <i class="fas fa-inbox text-4xl mb-2 block"></i>
                    <p>No hay evaluaciones registradas</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = evaluaciones.map(ev => `
        <tr class="hover:bg-gray-50">
            <td class="px-6 py-4 text-sm text-gray-500">
                ${new Date(ev.fechaEvaluacion).toLocaleDateString()}
            </td>
            <td class="px-6 py-4 text-sm text-gray-900">${ev.nombreProyecto}</td>
            <td class="px-6 py-4 text-sm text-gray-500">${ev.nombreJurado}</td>
            <td class="px-6 py-4 text-sm text-gray-500">${ev.nombreCriterio}</td>
            <td class="px-6 py-4 text-sm">
                <span class="px-2 py-1 rounded-full bg-green-100 text-green-800 font-medium">
                    ${ev.puntuacion}/5
                </span>
            </td>
            <td class="px-6 py-4 text-right text-sm">
                <button onclick="eliminarEvaluacion(${ev.idEvaluacion})" class="text-red-600 hover:text-red-800">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function evaluarProyecto(idProyecto) {
    const proyecto = proyectos.find(p => p.idProyecto === idProyecto);
    if (!proyecto) return;

    try {
        const criteriosResp = await fetch('/api/admin/criterios');
        const criterios = await criteriosResp.json();

        const evaluacionesResp = await fetch(`/api/admin/evaluaciones/proyecto/${idProyecto}`);
        const evaluacionesExistentes = await evaluacionesResp.json();

        mostrarFormularioEvaluacion(proyecto, criterios, evaluacionesExistentes);
    } catch (error) {
        console.error('Error:', error);
        mostrarMensajeError('Error al cargar datos para evaluación');
    }
}

function mostrarFormularioEvaluacion(proyecto, criterios, evaluacionesExistentes) {
    const modal = document.getElementById('modal-evaluacion');
    const contenedor = document.getElementById('contenedor-evaluacion');

    document.getElementById('evaluacion-proyecto-nombre').textContent = proyecto.nombreProyecto;
    document.getElementById('evaluacion-equipo-nombre').textContent = proyecto.nombreEquipo;
    document.getElementById('evaluacion-proyecto-id').value = proyecto.idProyecto;

    contenedor.innerHTML = criterios.map(criterio => {
        const evaluacionExistente = evaluacionesExistentes.find(e => e.idCriterio === criterio.idCriterio);
        const puntuacion = evaluacionExistente ? evaluacionExistente.puntuacion : '';

        return `
            <div class="border border-gray-200 rounded-lg p-4">
                <div class="flex justify-between items-start mb-2">
                    <div>
                        <h4 class="font-medium text-gray-900">${criterio.nombreCriterio}</h4>
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
                           class="puntuacion-input w-24 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500" 
                           required>
                    <span class="text-gray-500">/5.0</span>
                    ${evaluacionExistente ? `<span class="text-green-600 text-sm"><i class="fas fa-check-circle"></i> Evaluado</span>` : ''}
                </div>
            </div>
        `;
    }).join('');

    modal.classList.remove('hidden');
}

async function guardarEvaluacion() {
    const idProyecto = document.getElementById('evaluacion-proyecto-id').value;
    const inputs = document.querySelectorAll('.puntuacion-input');

    for (let input of inputs) {
        const puntuacion = parseFloat(input.value);
        if (!puntuacion || puntuacion < 0 || puntuacion > 5) continue;

        const datos = {
            idProyecto: parseInt(idProyecto),
            idCriterio: parseInt(input.dataset.criterioId),
            idJurado: 1,
            puntuacion: puntuacion
        };

        try {
            const response = await fetch('/api/admin/evaluaciones', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datos)
            });

            const data = await response.json();
            if (!data.success) {
                console.error(data.mensaje);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    cerrarModalEvaluacion();
    await cargarProyectos();
    await cargarEvaluaciones();
    mostrarMensajeExito('Evaluaciones registradas exitosamente');
}

async function eliminarEvaluacion(id) {
    if (!confirm('¿Estás seguro de eliminar esta evaluación?')) return;

    try {
        const response = await fetch(`/api/admin/evaluaciones/${id}`, { method: 'DELETE' });
        const data = await response.json();

        if (data.success) {
            await cargarEvaluaciones();
            mostrarMensajeExito(data.mensaje);
        } else {
            mostrarMensajeError(data.mensaje);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function cerrarModalEvaluacion() {
    document.getElementById('modal-evaluacion').classList.add('hidden');
}

document.addEventListener('DOMContentLoaded', () => {
    const seccionEvaluaciones = document.getElementById('evaluations-section');
    if (seccionEvaluaciones && !seccionEvaluaciones.classList.contains('hidden')) {
        cargarProyectos();
        cargarEvaluaciones();
    }
});
