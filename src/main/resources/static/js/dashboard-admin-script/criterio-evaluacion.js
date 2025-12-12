let criterios = [];

async function cargarCriterios() {
    try {
        const response = await fetch('/api/admin/criterios');
        if (!response.ok) {
            console.error('Error al cargar criterios');
            return;
        }

        criterios = await response.json();
        renderizarCriterios();
        actualizarValidacionPesos();
    } catch (error) {
        console.error('Error:', error);
    }
}

function renderizarCriterios() {
    const tbody = document.getElementById('tabla-criterios-body');
    if (!tbody) return;

    if (criterios.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="px-6 py-8 text-center text-gray-500">
                    <i class="fas fa-inbox text-4xl mb-2 block"></i>
                    <p>No hay criterios de evaluación registrados</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = criterios.map(criterio => `
        <tr class="hover:bg-gray-50">
            <td class="px-6 py-4 text-sm text-gray-900">${criterio.nombreCriterio}</td>
            <td class="px-6 py-4 text-sm text-gray-500">${criterio.descripcion || '-'}</td>
            <td class="px-6 py-4 text-sm">
                <span class="px-2 py-1 rounded-full bg-blue-100 text-blue-800 font-medium">
                    ${criterio.pesoFormatted}
                </span>
            </td>
            <td class="px-6 py-4 text-sm text-gray-900">${(criterio.peso * 5).toFixed(2)} pts</td>
            <td class="px-6 py-4 text-right text-sm">
                <button onclick="editarCriterio(${criterio.idCriterio})" class="text-blue-600 hover:text-blue-800 mr-3">
                    <i class="fas fa-edit"></i>
                </button>
                <button onclick="confirmarEliminarCriterio(${criterio.idCriterio})" class="text-red-600 hover:text-red-800">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function actualizarValidacionPesos() {
    try {
        const response = await fetch('/api/admin/criterios/validar-pesos');
        if (!response.ok) return;

        const data = await response.json();
        const indicador = document.getElementById('indicador-pesos');
        if (!indicador) return;

        const porcentaje = data.suma * 100;
        const clase = data.valido ? 'text-green-600' : 'text-red-600';
        const icono = data.valido ? 'fa-check-circle' : 'fa-exclamation-circle';

        indicador.innerHTML = `
            <i class="fas ${icono} ${clase}"></i>
            <span class="${clase} font-medium">${porcentaje.toFixed(0)}%</span>
            <span class="text-gray-500 text-sm ml-2">${data.mensaje}</span>
        `;
    } catch (error) {
        console.error('Error:', error);
    }
}

function mostrarFormularioCriterio(editar = false) {
    const modal = document.getElementById('modal-criterio');
    const titulo = document.getElementById('titulo-modal-criterio');

    if (!editar) {
        document.getElementById('form-criterio').reset();
        document.getElementById('criterio-id').value = '';
        titulo.textContent = 'Nuevo Criterio';
    } else {
        titulo.textContent = 'Editar Criterio';
    }

    modal.classList.remove('hidden');
}

async function editarCriterio(id) {
    const criterio = criterios.find(c => c.idCriterio === id);
    if (!criterio) return;

    document.getElementById('criterio-id').value = criterio.idCriterio;
    document.getElementById('criterio-nombre').value = criterio.nombreCriterio;
    document.getElementById('criterio-descripcion').value = criterio.descripcion || '';
    document.getElementById('criterio-peso').value = criterio.peso;

    mostrarFormularioCriterio(true);
}

async function guardarCriterio() {
    const form = document.getElementById('form-criterio');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const idCriterio = document.getElementById('criterio-id').value;
    const esEdicion = idCriterio !== '';

    const datos = {
        nombreCriterio: document.getElementById('criterio-nombre').value.trim(),
        descripcion: document.getElementById('criterio-descripcion').value.trim(),
        peso: parseFloat(document.getElementById('criterio-peso').value)
    };

    const url = esEdicion ? `/api/admin/criterios/${idCriterio}` : '/api/admin/criterios';
    const method = esEdicion ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        const data = await response.json();

        if (data.success) {
            cerrarModalCriterio();
            await cargarCriterios();
            mostrarMensajeExito(data.mensaje);
        } else {
            mostrarMensajeError(data.mensaje);
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarMensajeError('Error al guardar el criterio');
    }
}

async function confirmarEliminarCriterio(id) {
    if (!confirm('¿Estás seguro de eliminar este criterio?')) return;

    try {
        const response = await fetch(`/api/admin/criterios/${id}`, { method: 'DELETE' });
        const data = await response.json();

        if (data.success) {
            await cargarCriterios();
            mostrarMensajeExito(data.mensaje);
        } else {
            mostrarMensajeError(data.mensaje);
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarMensajeError('Error al eliminar el criterio');
    }
}

function cerrarModalCriterio() {
    document.getElementById('modal-criterio').classList.add('hidden');
}

document.addEventListener('DOMContentLoaded', () => {
    const seccionEvaluaciones = document.getElementById('evaluations-section');
    if (seccionEvaluaciones && !seccionEvaluaciones.classList.contains('hidden')) {
        cargarCriterios();
    }
});
