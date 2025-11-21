let todosLosHackatones = [];
let hackatonesFiltrados = [];
let hackatonIdParaEliminar = null;
let hackatonNombreParaEliminar = '';
let hackatonActualDetalles = null;

const FILAS_POR_PAGINA_HACKATON = 10;
let paginaActualHackaton = 1;
let totalPaginasHackaton = 1;

async function cargarHackatones() {
    try {
        const response = await fetch('/api/admin/hackatones');
        if (!response.ok) {
            throw new Error('Error al cargar hackatones');
        }
        
        todosLosHackatones = await response.json();
        hackatonesFiltrados = [...todosLosHackatones];
        paginaActualHackaton = 1;
        renderizarTablaHackatones();
    } catch (error) {
        console.error('Error al cargar hackatones:', error);
        mostrarMensajeError('Error al cargar la lista de hackatones');
    }
}

function renderizarTablaHackatones() {
    const tbody = document.getElementById('hackaton-table-body');
    if (!tbody) return;
    
    if (hackatonesFiltrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-10 text-gray-500">
                    <i class="fas fa-trophy text-4xl mb-3 text-gray-300"></i>
                    <p>No se encontraron hackatones para mostrar.</p>
                </td>
            </tr>
        `;
        totalPaginasHackaton = 1;
        actualizarControlesPaginacionHackaton();
        return;
    }
    
    totalPaginasHackaton = Math.ceil(hackatonesFiltrados.length / FILAS_POR_PAGINA_HACKATON);
    const inicio = (paginaActualHackaton - 1) * FILAS_POR_PAGINA_HACKATON;
    const fin = inicio + FILAS_POR_PAGINA_HACKATON;
    const hackatonesPagina = hackatonesFiltrados.slice(inicio, fin);
    
    tbody.innerHTML = hackatonesPagina.map(hackaton => {
        const fechaInicio = new Date(hackaton.fechaInicio).toLocaleDateString('es-ES');
        const fechaFin = new Date(hackaton.fechaFin).toLocaleDateString('es-ES');
        
        return `
        <tr class="hover:bg-gray-50 transition-colors">
            <td class="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">${hackaton.nombre}</td>
            <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">${hackaton.nombreCategoria || 'N/A'}</td>
            <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">${fechaInicio} - ${fechaFin}</td>
            <td class="px-4 py-3 whitespace-nowrap">
                ${obtenerBadgeEstado(hackaton.estado)}
            </td>
            <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500 text-center">${hackaton.maximoParticipantes}</td>
            <td class="px-4 py-3 whitespace-nowrap text-center text-sm font-medium">
                <button onclick="mostrarDetallesHackaton(${hackaton.idHackaton})" 
                        class="text-blue-600 hover:text-blue-800 transition duration-150 ease-in-out" 
                        title="Ver Detalles">
                    <i class="fas fa-eye"></i>
                </button>
                <button onclick="editarHackaton(${hackaton.idHackaton})" 
                        class="text-indigo-600 hover:text-indigo-800 transition duration-150 ease-in-out ml-3" 
                        title="Editar">
                    <i class="fas fa-pencil-alt"></i>
                </button>
                <button onclick="abrirModalEliminarHackaton(${hackaton.idHackaton}, '${hackaton.nombre.replace(/'/g, "\\'")}')" 
                        class="text-red-600 hover:text-red-800 transition duration-150 ease-in-out ml-3" 
                        title="Eliminar">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        </tr>
    `}).join('');
    
    actualizarControlesPaginacionHackaton();
}

function obtenerBadgeEstado(estado) {
    const colores = {
        'PROXIMO': 'bg-yellow-100 text-yellow-800',
        'EN_CURSO': 'bg-green-100 text-green-800',
        'FINALIZADO': 'bg-gray-100 text-gray-800'
    };
    
    const textos = {
        'PROXIMO': 'Próximo',
        'EN_CURSO': 'En Curso',
        'FINALIZADO': 'Finalizado'
    };
    
    return `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${colores[estado] || 'bg-gray-100 text-gray-800'}">${textos[estado] || estado}</span>`;
}

function actualizarControlesPaginacionHackaton() {
    const totalElement = document.getElementById('hackaton-total');
    const startElement = document.getElementById('hackaton-start');
    const endElement = document.getElementById('hackaton-end');
    const prevButton = document.getElementById('hackaton-prev');
    const nextButton = document.getElementById('hackaton-next');
    
    const inicio = (paginaActualHackaton - 1) * FILAS_POR_PAGINA_HACKATON + 1;
    const fin = Math.min(paginaActualHackaton * FILAS_POR_PAGINA_HACKATON, hackatonesFiltrados.length);
    
    if (totalElement) totalElement.textContent = hackatonesFiltrados.length;
    if (startElement) startElement.textContent = hackatonesFiltrados.length > 0 ? inicio : 0;
    if (endElement) endElement.textContent = fin;
    
    if (prevButton) {
        prevButton.disabled = paginaActualHackaton === 1;
        prevButton.classList.toggle('opacity-50', paginaActualHackaton === 1);
        prevButton.classList.toggle('cursor-not-allowed', paginaActualHackaton === 1);
    }
    if (nextButton) {
        nextButton.disabled = paginaActualHackaton >= totalPaginasHackaton;
        nextButton.classList.toggle('opacity-50', paginaActualHackaton >= totalPaginasHackaton);
        nextButton.classList.toggle('cursor-not-allowed', paginaActualHackaton >= totalPaginasHackaton);
    }
}

function cambiarPaginaHackaton(direccion) {
    const nuevaPagina = paginaActualHackaton + direccion;
    if (nuevaPagina >= 1 && nuevaPagina <= totalPaginasHackaton) {
        paginaActualHackaton = nuevaPagina;
        renderizarTablaHackatones();
    }
}

function filtrarHackatones() {
    const searchInput = document.getElementById('hackaton-search');
    const filterSelect = document.getElementById('hackaton-filter');
    
    if (!searchInput || !filterSelect) return;
    
    const searchTerm = searchInput.value.toLowerCase().trim();
    const filterValue = filterSelect.value;
    
    hackatonesFiltrados = todosLosHackatones.filter(hackaton => {
        const coincideBusqueda = searchTerm === '' || 
            hackaton.nombre.toLowerCase().includes(searchTerm) ||
            (hackaton.descripcion && hackaton.descripcion.toLowerCase().includes(searchTerm)) ||
            (hackaton.nombreCategoria && hackaton.nombreCategoria.toLowerCase().includes(searchTerm));
        
        const coincideEstado = filterValue === 'all' || hackaton.estado === filterValue.toUpperCase();
        
        return coincideBusqueda && coincideEstado;
    });
    
    paginaActualHackaton = 1;
    renderizarTablaHackatones();
}

function showHackatonForm() {
    const formView = document.getElementById('hackaton-form-view');
    const listView = document.getElementById('hackaton-list-view');
    
    if (formView && listView) {
        listView.classList.add('hidden');
        formView.classList.remove('hidden');
        
        if (typeof prepararFormularioNuevoHackaton === 'function') {
            prepararFormularioNuevoHackaton();
        }
    }
}

function editarHackaton(id) {
    const formView = document.getElementById('hackaton-form-view');
    const listView = document.getElementById('hackaton-list-view');
    
    if (formView && listView) {
        listView.classList.add('hidden');
        formView.classList.remove('hidden');
        if (typeof cargarHackatonParaEditar === 'function') {
            cargarHackatonParaEditar(id);
        }
    }
}

function abrirModalEliminarHackaton(id, nombre) {
    hackatonIdParaEliminar = id;
    hackatonNombreParaEliminar = nombre;
    
    const modal = document.getElementById('delete-hackaton-modal');
    const nombreElement = document.getElementById('delete-hackaton-nombre');
    
    if (nombreElement) {
        nombreElement.textContent = nombre;
    }
    
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        document.body.style.overflow = 'hidden';
    }
}

function cerrarModalEliminarHackaton() {
    const modal = document.getElementById('delete-hackaton-modal');
    if (modal) {
        modal.classList.remove('flex');
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
    hackatonIdParaEliminar = null;
    hackatonNombreParaEliminar = '';
}

function confirmarEliminacionHackaton() {
    if (hackatonIdParaEliminar) {
        eliminarHackaton(hackatonIdParaEliminar);
        cerrarModalEliminarHackaton();
    }
}

async function eliminarHackaton(id) {
    try {
        const response = await fetch(`/api/admin/hackatones/${id}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            mostrarMensajeExito(data.mensaje || 'Hackatón eliminado exitosamente');
            await cargarHackatones();
        } else {
            mostrarMensajeError(data.mensaje || 'Error al eliminar el hackatón');
        }
    } catch (error) {
        console.error('Error al eliminar hackatón:', error);
        mostrarMensajeError('Error de conexión al eliminar el hackatón');
    }
}

function mostrarDetallesHackaton(id) {
    const hackaton = todosLosHackatones.find(h => h.idHackaton === id);
    if (!hackaton) {
        console.error('No se encontró el hackatón con id:', id);
        return;
    }
    
    hackatonActualDetalles = hackaton;
    
    const imagenContainer = document.getElementById('detail-imagen-container');
    const imagenElement = document.getElementById('detail-imagen');
    if (hackaton.urlImg && hackaton.urlImg.trim() !== '') {
        imagenElement.src = hackaton.urlImg;
        imagenElement.alt = hackaton.nombre;
        imagenContainer.classList.remove('hidden');
    } else {
        imagenContainer.classList.add('hidden');
    }
    
    document.getElementById('detail-nombre').textContent = hackaton.nombre;
    document.getElementById('detail-categoria').textContent = hackaton.nombreCategoria || 'N/A';
    document.getElementById('detail-descripcion').textContent = hackaton.descripcion || 'Sin descripción';
    
    const fechaInicio = new Date(hackaton.fechaInicio).toLocaleDateString('es-ES', {
        year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
    const fechaFin = new Date(hackaton.fechaFin).toLocaleDateString('es-ES', {
        year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
    
    document.getElementById('detail-fecha-inicio').textContent = fechaInicio;
    document.getElementById('detail-fecha-fin').textContent = fechaFin;
    document.getElementById('detail-ubicacion').textContent = hackaton.ubicacion || 'No especificada';
    document.getElementById('detail-premios').textContent = hackaton.premios || 'No especificados';
    document.getElementById('detail-requisitos').textContent = hackaton.requisitos || 'Sin requisitos especificados';
    
    const modalidadElement = document.getElementById('detail-modalidad');
    modalidadElement.textContent = hackaton.modalidad || 'Presencial';
    modalidadElement.className = 'inline-flex items-center px-2.5 py-1 rounded-full text-sm font-medium ';
    modalidadElement.className += hackaton.modalidad === 'Virtual' 
        ? 'bg-blue-100 text-blue-800' 
        : 'bg-green-100 text-green-800';
    
    const estadoElement = document.getElementById('detail-estado');
    estadoElement.textContent = hackaton.estado;
    estadoElement.className = 'inline-flex items-center px-2.5 py-1 rounded-full text-sm font-medium ';
    
    switch(hackaton.estado) {
        case 'ACTIVO':
        case 'ACTIVA':
            estadoElement.className += 'bg-green-100 text-green-800';
            break;
        case 'INACTIVO':
        case 'INACTIVA':
            estadoElement.className += 'bg-gray-100 text-gray-800';
            break;
        case 'FINALIZADO':
        case 'FINALIZADA':
            estadoElement.className += 'bg-blue-100 text-blue-800';
            break;
        case 'CANCELADO':
        case 'CANCELADA':
            estadoElement.className += 'bg-red-100 text-red-800';
            break;
        default:
            estadoElement.className += 'bg-gray-100 text-gray-800';
    }
    
    const modal = document.getElementById('hackaton-details-modal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        document.body.style.overflow = 'hidden';
    }
}

function cerrarModalDetallesHackaton() {
    const modal = document.getElementById('hackaton-details-modal');
    if (modal) {
        modal.classList.remove('flex');
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
    hackatonActualDetalles = null;
}

function editarDesdeDetallesHackaton() {
    if (hackatonActualDetalles) {
        cerrarModalDetallesHackaton();
        editarHackaton(hackatonActualDetalles.idHackaton);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('hackaton-search');
    const filterSelect = document.getElementById('hackaton-filter');
    const prevButton = document.getElementById('hackaton-prev');
    const nextButton = document.getElementById('hackaton-next');
    const confirmDeleteBtn = document.getElementById('confirm-delete-hackaton-btn');
    
    if (searchInput) {
        searchInput.addEventListener('input', filtrarHackatones);
    }
    
    if (filterSelect) {
        filterSelect.addEventListener('change', filtrarHackatones);
    }
    
    if (prevButton) {
        prevButton.addEventListener('click', () => cambiarPaginaHackaton(-1));
    }
    
    if (nextButton) {
        nextButton.addEventListener('click', () => cambiarPaginaHackaton(1));
    }
    
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', confirmarEliminacionHackaton);
    }
});

function mostrarMensajeExito(mensaje) {
    mostrarAlerta(mensaje, 'success');
}

function mostrarMensajeError(mensaje) {
    mostrarAlerta(mensaje, 'error');
}

function mostrarAlerta(mensaje, tipo) {
    const alertContainer = document.createElement('div');
    alertContainer.className = `fixed top-4 right-4 z-50 max-w-md animate-slide-in`;
    
    const bgColor = tipo === 'success' ? 'bg-green-500' : 'bg-red-500';
    const icon = tipo === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
    
    alertContainer.innerHTML = `
        <div class="${bgColor} text-white px-6 py-4 rounded-lg shadow-lg flex items-center space-x-3">
            <i class="fas ${icon} text-xl"></i>
            <span class="flex-1">${mensaje}</span>
            <button onclick="this.parentElement.parentElement.remove()" class="text-white hover:text-gray-200">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;
    
    document.body.appendChild(alertContainer);
    
    setTimeout(() => {
        alertContainer.remove();
    }, 5000);
}
