let todosLosUsuarios = [];
let usuariosFiltrados = [];
let usuarioIdParaEliminar = null;
let usuarioNombreParaEliminar = '';
let usuarioActualDetalles = null;

const FILAS_POR_PAGINA = 13;
let paginaActual = 1;
let totalPaginas = 1;


async function cargarUsuarios() {
    try {
        const response = await fetch('/api/admin/usuarios');
        if (!response.ok) {
            throw new Error('Error al cargar usuarios');
        }
        
        todosLosUsuarios = await response.json();
        usuariosFiltrados = [...todosLosUsuarios];
        paginaActual = 1;
        renderizarTablaUsuarios();
        actualizarEstadisticas();
    } catch (error) {
        console.error('Error al cargar usuarios:', error);
        mostrarMensajeError('Error al cargar la lista de usuarios');
    }
}


function renderizarTablaUsuarios() {
    const tbody = document.getElementById('user-table-body');
    if (!tbody) return;
    
    if (usuariosFiltrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center py-10 text-gray-500">
                    <i class="fas fa-users text-4xl mb-3 text-gray-300"></i>
                    <p>No se encontraron usuarios para mostrar.</p>
                </td>
            </tr>
        `;
        totalPaginas = 1;
        actualizarControlesPaginacion();
        return;
    }
    
    totalPaginas = Math.ceil(usuariosFiltrados.length / FILAS_POR_PAGINA);
    const inicio = (paginaActual - 1) * FILAS_POR_PAGINA;
    const fin = inicio + FILAS_POR_PAGINA;
    const usuariosPagina = usuariosFiltrados.slice(inicio, fin);
    
    const terminoBusqueda = aplicarResaltado();
    
    tbody.innerHTML = usuariosPagina.map((usuario, index) => {
        const numeroFila = inicio + index + 1;
        const isSelected = usuariosSeleccionados.has(usuario.idUsuario);
        const nombreCompleto = `${usuario.nombre} ${usuario.apellido}`;
        
        return `
        <tr class="hover:bg-gray-50 transition-colors ${isSelected ? 'bg-blue-50' : ''}">
            <td class="px-3 py-2 text-center">
                <input type="checkbox" class="user-checkbox w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500" 
                       data-user-id="${usuario.idUsuario}" 
                       onchange="toggleSeleccion(this, ${usuario.idUsuario})"
                       ${isSelected ? 'checked' : ''}>
            </td>
            <td class="px-3 py-2 whitespace-nowrap text-sm text-gray-500 font-medium">${numeroFila}</td>
            <td class="px-3 py-2 whitespace-nowrap text-sm font-medium text-gray-900">${resaltarTexto(nombreCompleto, terminoBusqueda)}</td>
            <td class="px-3 py-2 whitespace-nowrap text-sm text-gray-500">${resaltarTexto(usuario.correoElectronico, terminoBusqueda)}</td>
            <td class="px-3 py-2 whitespace-nowrap text-sm text-gray-500">${usuario.telefono || 'N/A'}</td>
            <td class="px-3 py-2 whitespace-nowrap text-sm text-gray-500">${usuario.documentoDni || 'N/A'}</td>
            <td class="px-3 py-2 whitespace-nowrap">
                ${obtenerBadgeRol(usuario.rol)}
            </td>
            <td class="px-3 py-2 whitespace-nowrap text-center text-sm font-medium">
                <button onclick="mostrarDetallesUsuario(${usuario.idUsuario})" 
                        class="text-blue-600 hover:text-blue-800 transition duration-150 ease-in-out" 
                        title="Ver Detalles">
                    <i class="fas fa-eye"></i>
                </button>
                <button onclick="editarUsuario(${usuario.idUsuario})" 
                        class="text-indigo-600 hover:text-indigo-800 transition duration-150 ease-in-out ml-3" 
                        title="Editar">
                    <i class="fas fa-pencil-alt"></i>
                </button>
                <button onclick="abrirModalEliminar(${usuario.idUsuario}, '${usuario.nombre} ${usuario.apellido}')" 
                        class="text-red-600 hover:text-red-800 transition duration-150 ease-in-out ml-3" 
                        title="Eliminar">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        </tr>
    `}).join('');
    
    actualizarControlesPaginacion();
}

function obtenerBadgeRol(rol) {
    const colores = {
        'ADMINISTRADOR': 'bg-red-100 text-red-800',
        'JURADO': 'bg-yellow-100 text-yellow-800',
        'PARTICIPANTE': 'bg-green-100 text-green-800'
    };
    
    return `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${colores[rol] || 'bg-gray-100 text-gray-800'}">${rol}</span>`;
}

function formatearFecha(fecha) {
    const dia = String(fecha.getDate()).padStart(2, '0');
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const anio = String(fecha.getFullYear()).slice(-2);
    const horas = String(fecha.getHours()).padStart(2, '0');
    const minutos = String(fecha.getMinutes()).padStart(2, '0');
    return `${dia}/${mes}/${anio} ${horas}:${minutos}`;
}

function actualizarControlesPaginacion() {
    const totalElement = document.getElementById('user-total');
    const startElement = document.getElementById('user-start');
    const endElement = document.getElementById('user-end');
    const currentPageElement = document.getElementById('current-page');
    const totalPagesElement = document.getElementById('total-pages');
    const prevButton = document.getElementById('user-prev');
    const nextButton = document.getElementById('user-next');
    
    const inicio = (paginaActual - 1) * FILAS_POR_PAGINA + 1;
    const fin = Math.min(paginaActual * FILAS_POR_PAGINA, usuariosFiltrados.length);
    
    if (totalElement) totalElement.textContent = usuariosFiltrados.length;
    if (startElement) startElement.textContent = usuariosFiltrados.length > 0 ? inicio : 0;
    if (endElement) endElement.textContent = fin;
    if (currentPageElement) currentPageElement.textContent = paginaActual;
    if (totalPagesElement) totalPagesElement.textContent = totalPaginas;
    
    if (prevButton) {
        prevButton.disabled = paginaActual === 1;
    }
    if (nextButton) {
        nextButton.disabled = paginaActual >= totalPaginas;
    }
}

function cambiarPagina(direccion) {
    const nuevaPagina = paginaActual + direccion;
    if (nuevaPagina >= 1 && nuevaPagina <= totalPaginas) {
        paginaActual = nuevaPagina;
        renderizarTablaUsuarios();
    }
}

function showUserForm() {
    const formView = document.getElementById('user-form-view');
    const listView = document.getElementById('user-list-view');
    
    if (formView && listView) {
        listView.classList.add('hidden');
        formView.classList.remove('hidden');
        
        if (typeof prepararFormularioNuevo === 'function') {
            prepararFormularioNuevo();
        }
    }
}


function editarUsuario(id) {
    const formView = document.getElementById('user-form-view');
    const listView = document.getElementById('user-list-view');
    
    if (formView && listView) {
        listView.classList.add('hidden');
        formView.classList.remove('hidden');
        if (typeof cargarUsuarioParaEditar === 'function') {
            cargarUsuarioParaEditar(id);
        }
    }
}

function abrirModalEliminar(id, nombreCompleto) {
    usuarioIdParaEliminar = id;
    usuarioNombreParaEliminar = nombreCompleto;
    
    const modal = document.getElementById('delete-user-modal');
    const nombreElement = document.getElementById('delete-user-name');
    
    if (nombreElement) {
        nombreElement.textContent = nombreCompleto;
    }
    
    if (modal) {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }
}


function cerrarModalEliminar() {
    const modal = document.getElementById('delete-user-modal');
    if (modal) {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
    usuarioIdParaEliminar = null;
    usuarioNombreParaEliminar = '';
}

function confirmarEliminacion() {
    if (usuarioIdParaEliminar) {
        eliminarUsuario(usuarioIdParaEliminar);
        cerrarModalEliminar();
    }
}


async function eliminarUsuario(id) {
    try {
        const paginaAnterior = paginaActual;
        
        const response = await fetch(`/api/admin/usuarios/${id}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            mostrarMensajeExito(data.mensaje || 'Usuario eliminado exitosamente');
            await cargarUsuarios();

            const nuevasTotalPaginas = Math.ceil(usuariosFiltrados.length / FILAS_POR_PAGINA);
            if (paginaAnterior > nuevasTotalPaginas && nuevasTotalPaginas > 0) {
                paginaActual = nuevasTotalPaginas;
            } else {
                paginaActual = paginaAnterior;
            }
            
            renderizarTablaUsuarios();
        } else {
            mostrarMensajeError(data.mensaje || 'Error al eliminar el usuario');
        }
    } catch (error) {
        console.error('Error al eliminar usuario:', error);
        mostrarMensajeError('Error de conexión al eliminar el usuario');
    }
}


function filtrarUsuarios() {
    const searchInput = document.getElementById('user-search');
    const filterSelect = document.getElementById('user-filter');
    
    if (!searchInput || !filterSelect) return;
    
    const searchTerm = searchInput.value.toLowerCase().trim();
    const filterValue = filterSelect.value;
    
    usuariosFiltrados = todosLosUsuarios.filter(usuario => {
        const coincideBusqueda = searchTerm === '' || 
            usuario.nombre.toLowerCase().includes(searchTerm) ||
            usuario.apellido.toLowerCase().includes(searchTerm) ||
            usuario.correoElectronico.toLowerCase().includes(searchTerm) ||
            (usuario.documentoDni && usuario.documentoDni.toLowerCase().includes(searchTerm)) ||
            (usuario.telefono && usuario.telefono.toLowerCase().includes(searchTerm));
        const coincideRol = filterValue === 'all' || usuario.rol === filterValue;
        
        return coincideBusqueda && coincideRol;
    });
    
    paginaActual = 1;
    renderizarTablaUsuarios();
}


function mostrarDetallesUsuario(id) {
    const usuario = todosLosUsuarios.find(u => u.idUsuario === id);
    if (!usuario) return;
    
    usuarioActualDetalles = usuario;
    
    const modal = document.getElementById('user-details-modal');
    const content = document.getElementById('user-details-content');
    
    if (content) {
        content.innerHTML = `
            <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
                <!-- Información Personal -->
                <div class="col-span-2 md:col-span-3">
                    <h4 class="text-base font-semibold text-gray-900 mb-3 pb-2 border-b border-gray-200 flex items-center">
                        <i class="fas fa-user text-blue-600 mr-2 text-sm"></i>
                        Información Personal
                    </h4>
                </div>
                
                <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1">Nombre Completo</label>
                    <p class="text-sm text-gray-900 font-medium">${usuario.nombre} ${usuario.apellido}</p>
                </div>
                
                <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1">DNI</label>
                    <p class="text-sm text-gray-900">${usuario.documentoDni || '<span class="text-gray-400">No registrado</span>'}</p>
                </div>
                
                <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1">Teléfono</label>
                    <p class="text-sm text-gray-900">${usuario.telefono || '<span class="text-gray-400">No registrado</span>'}</p>
                </div>
                
                <!-- Información de Cuenta -->
                <div class="col-span-2 md:col-span-3 mt-2">
                    <h4 class="text-base font-semibold text-gray-900 mb-3 pb-2 border-b border-gray-200 flex items-center">
                        <i class="fas fa-envelope text-blue-600 mr-2 text-sm"></i>
                        Información de Cuenta
                    </h4>
                </div>
                
                <div class="col-span-2">
                    <label class="block text-xs font-medium text-gray-500 mb-1">Correo Electrónico</label>
                    <p class="text-sm text-gray-900">${usuario.correoElectronico}</p>
                </div>
                
                <div>
                    <label class="block text-xs font-medium text-gray-500 mb-1">Rol</label>
                    <p>${obtenerBadgeRol(usuario.rol)}</p>
                </div>
                
                <!-- Perfil y Experiencia -->
                <div class="col-span-2 md:col-span-3 mt-2">
                    <h4 class="text-base font-semibold text-gray-900 mb-3 pb-2 border-b border-gray-200 flex items-center">
                        <i class="fas fa-briefcase text-blue-600 mr-2 text-sm"></i>
                        Perfil y Experiencia
                    </h4>
                </div>
                
                <div class="col-span-2 md:col-span-3">
                    <label class="block text-xs font-medium text-gray-500 mb-1">Descripción del Perfil</label>
                    <p class="text-sm text-gray-900 whitespace-pre-wrap max-h-24 overflow-y-auto">${usuario.perfilExperiencia || '<span class="text-gray-400">No hay descripción de perfil</span>'}</p>
                </div>
            </div>
        `;
    }
    
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        document.body.style.overflow = 'hidden';
    }
}


function cerrarModalDetalles() {
    const modal = document.getElementById('user-details-modal');
    if (modal) {
        modal.classList.remove('flex');
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
    usuarioActualDetalles = null;
}


function editarDesdeDetalles() {
    if (usuarioActualDetalles) {
        cerrarModalDetalles();
        editarUsuario(usuarioActualDetalles.idUsuario);
    }
}


function mostrarMensajeExito(mensaje) {
    mostrarToast(mensaje, 'success');
}


function mostrarMensajeError(mensaje) {
    mostrarToast(mensaje, 'error');
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

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('user-search');
    const filterSelect = document.getElementById('user-filter');
    
    if (searchInput) {
        searchInput.addEventListener('input', filtrarUsuarios);
    }
    
    if (filterSelect) {
        filterSelect.addEventListener('change', filtrarUsuarios);
    }

    const listView = document.getElementById('user-list-view');
    if (listView && !listView.classList.contains('hidden')) {
        cargarUsuarios();
    }
});

const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .animate-slide-in {
        animation: slideInRight 0.3s ease-out;
    }
`;
document.head.appendChild(style);
