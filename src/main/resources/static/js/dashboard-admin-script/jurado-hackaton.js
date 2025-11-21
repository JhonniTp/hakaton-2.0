let asignacionesData = [];
let juradosDisponibles = [];
let hackatonesDisponibles = [];

async function cargarAsignaciones() {
    try {
        const response = await fetch('/api/admin/jurados-hackatones');
        if (!response.ok) throw new Error('Error al cargar asignaciones');
        
        asignacionesData = await response.json();
        renderizarTablaAsignaciones();
        await cargarJuradosDisponibles();
        await cargarHackatonesDisponibles();
        cargarFiltros();
    } catch (error) {
        console.error('Error:', error);
        mostrarMensajeError('No se pudieron cargar las asignaciones');
    }
}

async function cargarJuradosDisponibles() {
    try {
        const response = await fetch('/api/admin/usuarios');
        if (!response.ok) throw new Error('Error al cargar jurados');
        
        const usuarios = await response.json();
        juradosDisponibles = usuarios.filter(u => u.rol === 'JURADO');
        
        const selectJurado = document.getElementById('select-jurado');
        selectJurado.innerHTML = '<option value="">Seleccione un jurado...</option>';
        juradosDisponibles.forEach(jurado => {
            selectJurado.innerHTML += `
                <option value="${jurado.idUsuario}">
                    ${jurado.nombre} ${jurado.apellido} (${jurado.correo})
                </option>
            `;
        });
    } catch (error) {
        console.error('Error al cargar jurados:', error);
    }
}

async function cargarHackatonesDisponibles() {
    try {
        const response = await fetch('/api/admin/hackatones');
        if (!response.ok) throw new Error('Error al cargar hackatones');
        
        hackatonesDisponibles = await response.json();
        
        const selectHackaton = document.getElementById('select-hackaton-asignar');
        selectHackaton.innerHTML = '<option value="">Seleccione un hackatón...</option>';
        hackatonesDisponibles.forEach(hackaton => {
            selectHackaton.innerHTML += `
                <option value="${hackaton.idHackaton}">
                    ${hackaton.nombre} (${hackaton.fechaInicio} - ${hackaton.fechaFin})
                </option>
            `;
        });
    } catch (error) {
        console.error('Error al cargar hackatones:', error);
    }
}

function cargarFiltros() {
    const filtroHackaton = document.getElementById('filtro-hackaton');
    const hackatonesUnicos = [...new Set(asignacionesData.map(a => a.nombreHackaton))];
    
    filtroHackaton.innerHTML = '<option value="">Todos los hackatones</option>';
    hackatonesUnicos.forEach(nombre => {
        filtroHackaton.innerHTML += `<option value="${nombre}">${nombre}</option>`;
    });

    const filtroJurado = document.getElementById('filtro-jurado');
    const juradosUnicos = [...new Set(asignacionesData.map(a => a.nombreCompletoJurado))];
    
    filtroJurado.innerHTML = '<option value="">Todos los jurados</option>';
    juradosUnicos.forEach(nombre => {
        filtroJurado.innerHTML += `<option value="${nombre}">${nombre}</option>`;
    });

    filtroHackaton.addEventListener('change', aplicarFiltros);
    filtroJurado.addEventListener('change', aplicarFiltros);
}

function renderizarTablaAsignaciones() {
    const tbody = document.getElementById('asignaciones-table-body');
    const noDataMessage = document.getElementById('no-asignaciones-message');
    
    if (asignacionesData.length === 0) {
        tbody.innerHTML = '';
        noDataMessage.classList.remove('hidden');
        return;
    }

    noDataMessage.classList.add('hidden');
    tbody.innerHTML = '';

    asignacionesData.forEach(asignacion => {
        const row = document.createElement('tr');
        row.className = 'hover:bg-gray-50 transition-colors';
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10">
                        <div class="h-10 w-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-semibold">
                            ${obtenerIniciales(asignacion.nombreJurado, asignacion.apellidoJurado)}
                        </div>
                    </div>
                    <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">
                            ${asignacion.nombreJurado} ${asignacion.apellidoJurado}
                        </div>
                        <div class="text-sm text-gray-500">${asignacion.correoJurado}</div>
                    </div>
                </div>
            </td>
            <td class="px-6 py-4">
                <div class="text-sm text-gray-900 font-medium">${asignacion.nombreHackaton}</div>
                <div class="text-sm text-gray-500">ID: ${asignacion.idHackaton}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">
                    <i class="fas fa-calendar-alt text-gray-400 mr-2"></i>
                    ${formatearFecha(asignacion.fechaAsignacion)}
                </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button onclick="confirmarRemoverAsignacion(${asignacion.idJuradoHackaton})" 
                        class="text-red-600 hover:text-red-900 transition-colors">
                    <i class="fas fa-trash-alt mr-1"></i>Remover
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function obtenerIniciales(nombre, apellido) {
    return (nombre.charAt(0) + apellido.charAt(0)).toUpperCase();
}


function formatearFecha(fechaStr) {
    const fecha = new Date(fechaStr);
    const opciones = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return fecha.toLocaleDateString('es-ES', opciones);
}

function aplicarFiltros() {
    const filtroHackaton = document.getElementById('filtro-hackaton').value;
    const filtroJurado = document.getElementById('filtro-jurado').value;

    let datosFiltrados = [...asignacionesData];

    if (filtroHackaton) {
        datosFiltrados = datosFiltrados.filter(a => a.nombreHackaton === filtroHackaton);
    }

    if (filtroJurado) {
        datosFiltrados = datosFiltrados.filter(a => a.nombreCompletoJurado === filtroJurado);
    }

    const datosOriginales = asignacionesData;
    asignacionesData = datosFiltrados;
    renderizarTablaAsignaciones();
    asignacionesData = datosOriginales;
}

function limpiarFiltrosAsignaciones() {
    document.getElementById('filtro-hackaton').value = '';
    document.getElementById('filtro-jurado').value = '';
    renderizarTablaAsignaciones();
}

function abrirModalAsignarJurado() {
    document.getElementById('modal-asignar-jurado').classList.remove('hidden');
    document.getElementById('modal-asignar-jurado').classList.add('flex');
    document.getElementById('form-asignar-jurado').reset();
    ocultarMensajeValidacion();
}

function cerrarModalAsignarJurado() {
    document.getElementById('modal-asignar-jurado').classList.add('hidden');
    document.getElementById('modal-asignar-jurado').classList.remove('flex');
    document.getElementById('form-asignar-jurado').reset();
    ocultarMensajeValidacion();
}

async function guardarAsignacion(event) {
    event.preventDefault();

    const idJurado = document.getElementById('select-jurado').value;
    const idHackaton = document.getElementById('select-hackaton-asignar').value;

    if (!idJurado || !idHackaton) {
        mostrarMensajeValidacion('Por favor complete todos los campos', 'error');
        return;
    }

    try {
        const response = await fetch('/api/admin/jurados-hackatones', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                idJurado: parseInt(idJurado),
                idHackaton: parseInt(idHackaton)
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al asignar jurado');
        }

        const resultado = await response.json();
        
        const selectJurado = document.getElementById('select-jurado');
        const selectHackaton = document.getElementById('select-hackaton-asignar');
        const nombreJurado = selectJurado.options[selectJurado.selectedIndex].text;
        const nombreHackaton = selectHackaton.options[selectHackaton.selectedIndex].text.split(' (')[0];
        
        mostrarMensajeExito(`✓ ${nombreJurado} asignado correctamente a ${nombreHackaton}`);
        cerrarModalAsignarJurado();
        await cargarAsignaciones();

    } catch (error) {
        console.error('Error:', error);
        mostrarMensajeValidacion(error.message, 'error');
    }
}

let asignacionAEliminar = null;

function confirmarRemoverAsignacion(idAsignacion) {
    const asignacion = asignacionesData.find(a => a.idJuradoHackaton === idAsignacion);
    
    if (!asignacion) {
        mostrarMensajeError('No se encontró la asignación');
        return;
    }
    
    asignacionAEliminar = idAsignacion;
    
    document.getElementById('eliminar-jurado-nombre').textContent = asignacion.nombreCompletoJurado;
    document.getElementById('eliminar-hackaton-nombre').textContent = asignacion.nombreHackaton;
    abrirModalConfirmarEliminar();
}

function abrirModalConfirmarEliminar() {
    document.getElementById('modal-confirmar-eliminar-asignacion').classList.remove('hidden');
    document.getElementById('modal-confirmar-eliminar-asignacion').classList.add('flex');
}

function cerrarModalConfirmarEliminar() {
    document.getElementById('modal-confirmar-eliminar-asignacion').classList.add('hidden');
    document.getElementById('modal-confirmar-eliminar-asignacion').classList.remove('flex');
    asignacionAEliminar = null;
}

async function confirmarEliminarAsignacion() {
    if (!asignacionAEliminar) {
        mostrarMensajeError('No hay asignación seleccionada');
        return;
    }
    
    await removerAsignacion(asignacionAEliminar);
    cerrarModalConfirmarEliminar();
}

async function removerAsignacion(idAsignacion) {
    try {
        const asignacion = asignacionesData.find(a => a.idJuradoHackaton === idAsignacion);
        
        const response = await fetch(`/api/admin/jurados-hackatones/${idAsignacion}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Error al remover asignación');
        }

        mostrarMensajeExito(`✓ Asignación removida: ${asignacion.nombreCompletoJurado} - ${asignacion.nombreHackaton}`);
        await cargarAsignaciones();

    } catch (error) {
        console.error('Error:', error);
        mostrarMensajeError('✗ No se pudo remover la asignación');
    }
}


function mostrarMensajeValidacion(mensaje, tipo) {
    const contenedor = document.getElementById('mensaje-validacion-asignar');
    const parrafo = contenedor.querySelector('p');
    
    contenedor.classList.remove('hidden', 'bg-red-100', 'bg-green-100', 'text-red-700', 'text-green-700');
    
    if (tipo === 'error') {
        contenedor.classList.add('bg-red-100', 'text-red-700');
    } else {
        contenedor.classList.add('bg-green-100', 'text-green-700');
    }
    
    parrafo.textContent = mensaje;
}

function ocultarMensajeValidacion() {
    const contenedor = document.getElementById('mensaje-validacion-asignar');
    contenedor.classList.add('hidden');
}

function mostrarToast(mensaje, tipo = 'info') {
    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'fixed top-4 right-4 z-50 space-y-2';
        document.body.appendChild(toastContainer);
    }
    
    const colores = {
        success: 'bg-green-500',
        error: 'bg-red-500',
        warning: 'bg-yellow-500',
        info: 'bg-blue-500'
    };
    
    const iconos = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };
    
    const toast = document.createElement('div');
    toast.className = `${colores[tipo]} text-white px-6 py-4 rounded-lg shadow-lg flex items-center space-x-3 animate-slide-in max-w-md`;
    toast.innerHTML = `
        <i class="fas ${iconos[tipo]} text-xl"></i>
        <span class="flex-1">${mensaje}</span>
        <button onclick="this.parentElement.remove()" class="text-white hover:text-gray-200">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    toastContainer.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease-in';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}

function mostrarMensajeExito(mensaje) {
    mostrarToast(mensaje, 'success');
}

function mostrarMensajeError(mensaje) {
    mostrarToast(mensaje, 'error');
}

function mostrarMensajeAdvertencia(mensaje) {
    mostrarToast(mensaje, 'warning');
}

document.addEventListener('DOMContentLoaded', function() {
    const originalShowSection = window.showSection;
    if (originalShowSection) {
        window.showSection = function(sectionId) {
            originalShowSection(sectionId);
            if (sectionId === 'judges') {
                cargarAsignaciones();
            }
        };
    }
});
