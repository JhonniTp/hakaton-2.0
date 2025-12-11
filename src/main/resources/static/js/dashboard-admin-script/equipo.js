
let equipos = [];
let hackatones = [];
let participantesDisponibles = [];
let todosLosParticipantes = [];
let equipoActual = null;

function cargarEquipos() {
    fetch('/api/admin/equipos')
        .then(response => response.json())
        .then(data => {
            equipos = data;
            cargarFiltrosHackaton();
            renderizarTablaEquipos(equipos);
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarNotificacion('Error al cargar equipos', 'error');
        });
}
function cargarFiltrosHackaton() {
    fetch('/api/admin/hackatones')
        .then(response => response.json())
        .then(data => {
            hackatones = data;

            const filtroSelect = document.getElementById('filtro-hackaton-equipo');
            filtroSelect.innerHTML = '<option value="">Todos los hackatones</option>';
            hackatones.forEach(hackaton => {
                filtroSelect.innerHTML += `<option value="${hackaton.idHackaton}">${hackaton.nombre}</option>`;
            });
            const equipoHackatonSelect = document.getElementById('equipo-hackaton');
            equipoHackatonSelect.innerHTML = '<option value="">Seleccione un hackatón...</option>';
            hackatones.forEach(hackaton => {
                equipoHackatonSelect.innerHTML += `<option value="${hackaton.idHackaton}">${hackaton.nombre}</option>`;
            });
        })
        .catch(error => {
            console.error('Error al cargar hackatones:', error);
            mostrarNotificacion('Error al cargar hackatones', 'error');
        });
    document.getElementById('filtro-hackaton-equipo').addEventListener('change', function() {
        const idHackaton = this.value;
        if (idHackaton) {
            cargarEquiposPorHackaton(idHackaton);
        } else {
            renderizarTablaEquipos(equipos);
        }
    });
}

function cargarEquiposPorHackaton(idHackaton) {
    fetch(`/api/admin/equipos/hackaton/${idHackaton}`)
        .then(response => response.json())
        .then(data => {
            renderizarTablaEquipos(data);
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarNotificacion('Error al cargar equipos del hackatón', 'error');
        });
}

function limpiarFiltrosEquipos() {
    document.getElementById('filtro-hackaton-equipo').value = '';
    renderizarTablaEquipos(equipos);
}

function renderizarTablaEquipos(equiposData) {
    const tbody = document.getElementById('equipos-table-body');
    const noEquiposMsg = document.getElementById('no-equipos-message');

    if (!equiposData || equiposData.length === 0) {
        tbody.innerHTML = '';
        noEquiposMsg.classList.remove('hidden');
        return;
    }

    noEquiposMsg.classList.add('hidden');
    tbody.innerHTML = '';

    equiposData.forEach(equipo => {
        const tr = document.createElement('tr');
        tr.className = 'hover:bg-gray-50 transition-colors';
        const fecha = equipo.fechaCreacion ? new Date(equipo.fechaCreacion).toLocaleDateString('es-ES') : 'N/A';
        
        tr.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                    ${equipo.imgUrl ? 
                        `<img src="${equipo.imgUrl}" alt="${equipo.nombreEquipo}" class="w-10 h-10 rounded-full mr-3 object-cover">` :
                        `<div class="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 flex items-center justify-center mr-3">
                            <i class="fas fa-users text-white"></i>
                        </div>`
                    }
                    <div>
                        <div class="text-sm font-medium text-gray-900">${equipo.nombreEquipo}</div>
                    </div>
                </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800">
                    ${equipo.nombreHackaton || 'Sin hackatón'}
                </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">${equipo.nombreLider || 'Sin líder'}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                    ${equipo.cantidadMiembros} ${equipo.cantidadMiembros === 1 ? 'miembro' : 'miembros'}
                </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                ${fecha}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                    <button onclick="abrirModalMiembros(${equipo.idEquipo})" 
                            class="text-purple-600 hover:text-purple-900 transition" title="Gestionar Miembros">
                        <i class="fas fa-users-cog"></i>
                    </button>
                    <button onclick="editarEquipo(${equipo.idEquipo})" 
                            class="text-blue-600 hover:text-blue-900 transition" title="Editar">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="abrirModalEliminarEquipo(${equipo.idEquipo})" 
                            class="text-red-600 hover:text-red-900 transition" title="Eliminar">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        `;

        tbody.appendChild(tr);
    });
}

function abrirModalCrearEquipo() {
    document.getElementById('modal-equipo-title').innerHTML = '<i class="fas fa-users mr-3"></i>Nuevo Equipo';
    document.getElementById('form-equipo').reset();
    document.getElementById('equipo-id').value = '';
    document.getElementById('modal-equipo').classList.remove('hidden');
}

function editarEquipo(idEquipo) {
    fetch(`/api/admin/equipos/${idEquipo}`)
        .then(response => response.json())
        .then(equipo => {
            document.getElementById('modal-equipo-title').innerHTML = '<i class="fas fa-users mr-3"></i>Editar Equipo';
            document.getElementById('equipo-id').value = equipo.idEquipo;
            document.getElementById('equipo-nombre').value = equipo.nombreEquipo;
            document.getElementById('equipo-hackaton').value = equipo.idHackaton;
            document.getElementById('equipo-img').value = equipo.imgUrl || '';
            document.getElementById('modal-equipo').classList.remove('hidden');
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarNotificacion('Error al cargar datos del equipo', 'error');
        });
}

function cerrarModalEquipo() {
    document.getElementById('modal-equipo').classList.add('hidden');
    document.getElementById('form-equipo').reset();
}

document.getElementById('form-equipo').addEventListener('submit', function(e) {
    e.preventDefault();

    const idEquipo = document.getElementById('equipo-id').value;
    const equipoData = {
        nombreEquipo: document.getElementById('equipo-nombre').value.trim(),
        idHackaton: parseInt(document.getElementById('equipo-hackaton').value),
        imgUrl: document.getElementById('equipo-img').value.trim() || null
    };

    const url = idEquipo ? `/api/admin/equipos/${idEquipo}` : '/api/admin/equipos';
    const method = idEquipo ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(equipoData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarNotificacion(data.mensaje, 'success');
            cerrarModalEquipo();
            cargarEquipos();
        } else {
            mostrarNotificacion(data.mensaje || 'Error al guardar equipo', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarNotificacion('Error al guardar equipo', 'error');
    });
});

function abrirModalEliminarEquipo(idEquipo) {
    document.getElementById('eliminar-equipo-id').value = idEquipo;
    document.getElementById('modal-eliminar-equipo').classList.remove('hidden');
}

function cerrarModalEliminarEquipo() {
    document.getElementById('modal-eliminar-equipo').classList.add('hidden');
}

function confirmarEliminarEquipo() {
    const idEquipo = document.getElementById('eliminar-equipo-id').value;

    fetch(`/api/admin/equipos/${idEquipo}`, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarNotificacion(data.mensaje, 'success');
            cerrarModalEliminarEquipo();
            cargarEquipos();
        } else {
            mostrarNotificacion(data.mensaje || 'Error al eliminar equipo', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarNotificacion('Error al eliminar equipo', 'error');
    });
}


function abrirModalMiembros(idEquipo) {
    equipoActual = equipos.find(e => e.idEquipo === idEquipo);
    
    if (!equipoActual) {
        mostrarNotificacion('Equipo no encontrado', 'error');
        return;
    }

    document.getElementById('miembros-equipo-id').value = idEquipo;
    document.getElementById('modal-miembros-equipo-nombre').textContent = `Equipo: ${equipoActual.nombreEquipo}`;
    
    cargarMiembrosEquipo(idEquipo);
    cargarParticipantesDisponibles(equipoActual.idHackaton);
    
    document.getElementById('modal-miembros').classList.remove('hidden');
}

function cerrarModalMiembros() {
    document.getElementById('modal-miembros').classList.add('hidden');
    equipoActual = null;
}


function cargarMiembrosEquipo(idEquipo) {
    fetch(`/api/admin/equipos/${idEquipo}/participantes`)
        .then(response => response.json())
        .then(data => {
            renderizarListaMiembros(data);
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarNotificacion('Error al cargar miembros', 'error');
        });
}

function renderizarListaMiembros(miembros) {
    const lista = document.getElementById('lista-miembros-equipo');
    const noMiembrosMsg = document.getElementById('no-miembros-message');

    if (!miembros || miembros.length === 0) {
        lista.innerHTML = '';
        noMiembrosMsg.classList.remove('hidden');
        return;
    }

    noMiembrosMsg.classList.add('hidden');
    lista.innerHTML = '';

    miembros.forEach(miembro => {
        const div = document.createElement('div');
        div.className = 'flex items-center justify-between p-3 bg-white border border-gray-200 rounded-lg hover:shadow-sm transition-shadow';
        
        div.innerHTML = `
            <div class="flex items-center flex-1">
                <div class="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 flex items-center justify-center mr-3">
                    <i class="fas fa-user text-white"></i>
                </div>
                <div>
                    <div class="text-sm font-medium text-gray-900">${miembro.nombreUsuario} ${miembro.apellidoUsuario}</div>
                    <div class="text-xs text-gray-500">${miembro.correoUsuario}</div>
                </div>
                ${miembro.esLider ? 
                    '<span class="ml-3 px-2 py-1 text-xs font-semibold bg-yellow-100 text-yellow-800 rounded-full">Líder</span>' : 
                    ''
                }
            </div>
            <div class="flex space-x-2">
                ${!miembro.esLider ? 
                    `<button onclick="asignarLider(${miembro.idUsuario})" 
                            class="px-3 py-1 text-xs bg-yellow-500 hover:bg-yellow-600 text-white rounded-lg transition" title="Hacer Líder">
                        <i class="fas fa-crown"></i>
                    </button>` : 
                    ''
                }
                <button onclick="removerMiembro(${miembro.idParticipanteEquipo})" 
                        class="px-3 py-1 text-xs bg-red-500 hover:bg-red-600 text-white rounded-lg transition" title="Remover">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `;

        lista.appendChild(div);
    });
}

function cargarParticipantesDisponibles(idHackaton) {
    fetch('/api/admin/usuarios')
        .then(response => response.json())
        .then(data => {
            todosLosParticipantes = data.filter(u => u.rol === 'PARTICIPANTE');
            participantesDisponibles = [...todosLosParticipantes];
            
            renderizarSelectParticipantes(participantesDisponibles);
        })
        .catch(error => {
            console.error('Error al cargar participantes:', error);
        });
}

function renderizarSelectParticipantes(participantes) {
    const select = document.getElementById('select-participante');
    select.innerHTML = '<option value="">Seleccione un participante...</option>';
    
    participantes.forEach(participante => {
        select.innerHTML += `<option value="${participante.idUsuario}" data-nombre="${participante.nombre}" data-apellido="${participante.apellido}" data-correo="${participante.correoElectronico}">${participante.nombre} ${participante.apellido} (${participante.correoElectronico})</option>`;
    });
}

function filtrarParticipantes() {
    const busqueda = document.getElementById('buscar-participante').value.toLowerCase().trim();
    
    if (!busqueda) {
        renderizarSelectParticipantes(todosLosParticipantes);
        return;
    }
    
    const participantesFiltrados = todosLosParticipantes.filter(participante => {
        const nombreCompleto = `${participante.nombre} ${participante.apellido}`.toLowerCase();
        const correo = participante.correoElectronico.toLowerCase();
        
        return nombreCompleto.includes(busqueda) || correo.includes(busqueda);
    });
    
    renderizarSelectParticipantes(participantesFiltrados);
}

function agregarMiembro() {
    const idEquipo = document.getElementById('miembros-equipo-id').value;
    const idUsuario = document.getElementById('select-participante').value;

    if (!idUsuario) {
        mostrarNotificacion('Debe seleccionar un participante', 'error');
        return;
    }

    const data = {
        idUsuario: parseInt(idUsuario),
        idEquipo: parseInt(idEquipo)
    };

    fetch(`/api/admin/equipos/${idEquipo}/participantes`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarNotificacion(data.mensaje, 'success');
            cargarMiembrosEquipo(idEquipo);
            document.getElementById('select-participante').value = '';
            document.getElementById('buscar-participante').value = '';
            cargarEquipos();
        } else {
            mostrarNotificacion(data.mensaje || 'Error al agregar miembro', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarNotificacion('Error al agregar miembro', 'error');
    });
}

function removerMiembro(idParticipanteEquipo) {
    if (!confirm('¿Está seguro de remover este miembro del equipo?')) {
        return;
    }

    const idEquipo = document.getElementById('miembros-equipo-id').value;

    fetch(`/api/admin/equipos/participantes/${idParticipanteEquipo}`, {
        method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarNotificacion(data.mensaje, 'success');
            cargarMiembrosEquipo(idEquipo);
            cargarEquipos();
        } else {
            mostrarNotificacion(data.mensaje || 'Error al remover miembro', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarNotificacion('Error al remover miembro', 'error');
    });
}

function asignarLider(idUsuario) {
    if (!confirm('¿Está seguro de asignar este participante como líder del equipo?')) {
        return;
    }

    const idEquipo = document.getElementById('miembros-equipo-id').value;

    fetch(`/api/admin/equipos/${idEquipo}/lider`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ idUsuario: parseInt(idUsuario) })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarNotificacion(data.mensaje, 'success');
            cargarMiembrosEquipo(idEquipo);
            cargarEquipos();
        } else {
            mostrarNotificacion(data.mensaje || 'Error al asignar líder', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarNotificacion('Error al asignar líder', 'error');
    });
}


function mostrarNotificacion(mensaje, tipo = 'success') {
    const existingToast = document.querySelector('.toast-notification');
    if (existingToast) {
        existingToast.remove();
    }

    const toast = document.createElement('div');
    toast.className = 'toast-notification fixed top-4 right-4 px-6 py-4 rounded-lg shadow-lg z-50 flex items-center space-x-3 transform transition-all duration-300';
    
    if (tipo === 'success') {
        toast.classList.add('bg-green-500', 'text-white');
        toast.innerHTML = `
            <i class="fas fa-check-circle text-xl"></i>
            <span class="font-medium">${mensaje}</span>
        `;
    } else {
        toast.classList.add('bg-red-500', 'text-white');
        toast.innerHTML = `
            <i class="fas fa-exclamation-circle text-xl"></i>
            <span class="font-medium">${mensaje}</span>
        `;
    }

    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('translate-x-0'), 10);

    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-x-full');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
