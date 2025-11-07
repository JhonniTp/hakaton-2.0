
// FUNCIONALIDADES AVANZADAS PARA TABLA DE USUARIOS

let usuariosSeleccionados = new Set();

let ordenActual = { campo: null, direccion: 'asc' };

function ordenarPor(campo) {
    if (ordenActual.campo === campo) {
        ordenActual.direccion = ordenActual.direccion === 'asc' ? 'desc' : 'asc';
    } else {
        ordenActual.campo = campo;
        ordenActual.direccion = 'asc';
    }
    usuariosFiltrados.sort((a, b) => {
        let valorA = a[campo];
        let valorB = b[campo];
        if (!valorA) return 1;
        if (!valorB) return -1;
        if (typeof valorA === 'string') valorA = valorA.toLowerCase();
        if (typeof valorB === 'string') valorB = valorB.toLowerCase();
        if (valorA < valorB) return ordenActual.direccion === 'asc' ? -1 : 1;
        if (valorA > valorB) return ordenActual.direccion === 'asc' ? 1 : -1;
        return 0;
    });
    
    actualizarIconosOrdenamiento(campo);

    paginaActual = 1;
    renderizarTablaUsuarios();
}


function actualizarIconosOrdenamiento(campoActivo) {
    const campos = ['nombre', 'correoElectronico', 'rol'];
    
    campos.forEach(campo => {
        const icon = document.getElementById(`sort-icon-${campo}`);
        if (icon) {
            if (campo === campoActivo) {
                icon.className = ordenActual.direccion === 'asc' 
                    ? 'fas fa-sort-up text-blue-600' 
                    : 'fas fa-sort-down text-blue-600';
            } else {
                icon.className = 'fas fa-sort text-gray-400';
            }
        }
    });
}


// SELECCIÓN MÚLTIPLE
function toggleSeleccionarTodos(checkbox) {
    const checkboxes = document.querySelectorAll('.user-checkbox');
    checkboxes.forEach(cb => {
        cb.checked = checkbox.checked;
        const userId = parseInt(cb.dataset.userId);
        if (checkbox.checked) {
            usuariosSeleccionados.add(userId);
        } else {
            usuariosSeleccionados.delete(userId);
        }
    });
    actualizarUISeleccion();
}

function toggleSeleccion(checkbox, userId) {
    if (checkbox.checked) {
        usuariosSeleccionados.add(userId);
    } else {
        usuariosSeleccionados.delete(userId);
    }

    const selectAllCheckbox = document.getElementById('select-all');
    const checkboxes = document.querySelectorAll('.user-checkbox');
    const todosSeleccionados = Array.from(checkboxes).every(cb => cb.checked);
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = todosSeleccionados;
    }
    
    actualizarUISeleccion();
}


function actualizarUISeleccion() {
    const selectedCount = document.getElementById('selected-count');
    const btnEliminar = document.getElementById('btn-eliminar-lote');
    const btnCambiarRol = document.getElementById('btn-cambiar-rol-lote');
    const btnCancelar = document.getElementById('btn-cancelar-lote');
    
    const haySeleccion = usuariosSeleccionados.size > 0;
    
    if (selectedCount) {
        selectedCount.textContent = usuariosSeleccionados.size;
    }
    
    if (haySeleccion) {
        if (btnEliminar) {
            btnEliminar.disabled = false;
            btnEliminar.className = 'px-3 py-1.5 text-sm bg-red-600 text-white rounded-lg hover:bg-red-700 transition cursor-pointer';
        }
        if (btnCambiarRol) {
            btnCambiarRol.disabled = false;
            btnCambiarRol.className = 'px-3 py-1.5 text-sm bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition cursor-pointer';
        }
        if (btnCancelar) {
            btnCancelar.disabled = false;
            btnCancelar.className = 'px-3 py-1.5 text-sm border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition cursor-pointer';
        }
    } else {
        if (btnEliminar) {
            btnEliminar.disabled = true;
            btnEliminar.className = 'px-3 py-1.5 text-sm bg-gray-400 text-white rounded-lg cursor-not-allowed transition disabled:opacity-50';
        }
        if (btnCambiarRol) {
            btnCambiarRol.disabled = true;
            btnCambiarRol.className = 'px-3 py-1.5 text-sm bg-gray-400 text-white rounded-lg cursor-not-allowed transition disabled:opacity-50';
        }
        if (btnCancelar) {
            btnCancelar.disabled = true;
            btnCancelar.className = 'px-3 py-1.5 text-sm border border-gray-300 text-gray-400 rounded-lg cursor-not-allowed transition disabled:opacity-50';
        }
    }
}

function deseleccionarTodos() {
    usuariosSeleccionados.clear();
    const checkboxes = document.querySelectorAll('.user-checkbox, #select-all');
    checkboxes.forEach(cb => cb.checked = false);
    actualizarUISeleccion();
}

function eliminarSeleccionados() {
    if (usuariosSeleccionados.size === 0) return;
    
    const modal = document.getElementById('delete-bulk-modal');
    const countElement = document.getElementById('delete-bulk-count');
    
    if (countElement) {
        countElement.textContent = usuariosSeleccionados.size;
    }
    
    if (modal) {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }
}


function cerrarModalEliminarLote() {
    const modal = document.getElementById('delete-bulk-modal');
    if (modal) {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
}

async function confirmarEliminacionLote() {
    if (usuariosSeleccionados.size === 0) return;

    cerrarModalEliminarLote();
    
    try {
        let eliminados = 0;
        let errores = 0;
        
        for (const userId of usuariosSeleccionados) {
            try {
                const response = await fetch(`/api/admin/usuarios/${userId}`, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
                    eliminados++;
                } else {
                    errores++;
                }
            } catch (error) {
                errores++;
            }
        }

        if (eliminados > 0) {
            mostrarMensajeExito(`${eliminados} usuario(s) eliminado(s) exitosamente`);
        }
        if (errores > 0) {
            mostrarMensajeError(`Error al eliminar ${errores} usuario(s)`);
        }

        usuariosSeleccionados.clear();
        await cargarUsuarios();
        renderizarTablaUsuarios();
        actualizarUISeleccion();
        
    } catch (error) {
        console.error('Error al eliminar usuarios:', error);
        mostrarMensajeError('Error al eliminar los usuarios seleccionados');
    }
}


function cambiarRolSeleccionados() {
    if (usuariosSeleccionados.size === 0) return;
    
    const modal = document.getElementById('change-role-modal');
    const countElement = document.getElementById('change-role-count');
    
    if (countElement) {
        countElement.textContent = usuariosSeleccionados.size;
    }
    
    if (modal) {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    }
}

function cerrarModalCambiarRol() {
    const modal = document.getElementById('change-role-modal');
    if (modal) {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
    document.getElementById('new-role-select').value = '';
}

async function confirmarCambioRol() {
    const nuevoRol = document.getElementById('new-role-select').value;
    
    if (!nuevoRol) {
        mostrarMensajeError('Por favor seleccione un rol');
        return;
    }
    
    try {
        let actualizados = 0;
        let errores = 0;
        
        for (const userId of usuariosSeleccionados) {
            try {
                const usuario = todosLosUsuarios.find(u => u.idUsuario === userId);
                if (!usuario) continue;
                
                const usuarioActualizado = {
                    ...usuario,
                    rol: nuevoRol
                };
                
                const response = await fetch(`/api/admin/usuarios/${userId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(usuarioActualizado)
                });
                
                if (response.ok) {
                    actualizados++;
                } else {
                    errores++;
                }
            } catch (error) {
                errores++;
            }
        }

        if (actualizados > 0) {
            mostrarMensajeExito(`Rol actualizado para ${actualizados} usuario(s)`);
        }
        if (errores > 0) {
            mostrarMensajeError(`Error al actualizar ${errores} usuario(s)`);
        }

        usuariosSeleccionados.clear();
        await cargarUsuarios();
        renderizarTablaUsuarios();
        actualizarUISeleccion();
        cerrarModalCambiarRol();
        
    } catch (error) {
        console.error('Error al cambiar rol:', error);
        mostrarMensajeError('Error al cambiar el rol de los usuarios');
    }
}

// EXPORTAR A EXCEL

function exportarAExcel() {
    const datosExportar = usuariosFiltrados.map((usuario, index) => ({
        'N°': index + 1,
        'Nombre': usuario.nombre || '',
        'Apellido': usuario.apellido || '',
        'Email': usuario.correoElectronico || '',
        'Teléfono': usuario.telefono || '',
        'DNI': usuario.documentoDni || '',
        'Rol': usuario.rol || '',
        'Perfil': (usuario.perfilExperiencia || '').replace(/\n/g, ' ').substring(0, 200)
    }));
    
    const csv = convertirACSV(datosExportar);
    
    descargarCSV(csv, 'usuarios_export.csv');
    
    mostrarMensajeExito(`${datosExportar.length} usuario(s) exportado(s) exitosamente`);
}

function convertirACSV(datos) {
    if (datos.length === 0) return '';

    const encabezados = Object.keys(datos[0]);
    
    const filas = datos.map(obj => {
        return encabezados.map(campo => {
            let valor = obj[campo];

            if (valor === null || valor === undefined) {
                valor = '';
            }
            
            valor = String(valor);
            
            valor = valor.replace(/[\r\n]+/g, ' ').trim();
            
            valor = valor.replace(/"/g, '""');
            
            return `"${valor}"`;
        }).join(',');
    });
    
    const encabezadosCSV = encabezados.map(h => `"${h}"`).join(',');
    return [encabezadosCSV, ...filas].join('\r\n');
}


function descargarCSV(contenido, nombreArchivo) {
    const BOM = '\uFEFF';
    const blob = new Blob([BOM + contenido], { 
        type: 'text/csv;charset=utf-8;' 
    });
    
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', nombreArchivo);
    link.style.visibility = 'hidden';
    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    setTimeout(() => URL.revokeObjectURL(url), 100);
}


// PANEL DE ESTADÍSTICAS

function actualizarEstadisticas() {
    const total = todosLosUsuarios.length;
    const admins = todosLosUsuarios.filter(u => u.rol === 'ADMINISTRADOR').length;
    const jurados = todosLosUsuarios.filter(u => u.rol === 'JURADO').length;
    const participantes = todosLosUsuarios.filter(u => u.rol === 'PARTICIPANTE').length;
    
    document.getElementById('stat-total').textContent = total;
    document.getElementById('stat-admin').textContent = admins;
    document.getElementById('stat-jurado').textContent = jurados;
    document.getElementById('stat-participante').textContent = participantes;
}

// BÚSQUEDA CON RESALTADO


function resaltarTexto(texto, busqueda) {
    if (!busqueda || busqueda.trim() === '') return texto;
    
    const regex = new RegExp(`(${busqueda})`, 'gi');
    return texto.replace(regex, '<mark class="bg-yellow-200 px-1 rounded">$1</mark>');
}

function aplicarResaltado() {
    const searchInput = document.getElementById('user-search');
    if (!searchInput) return '';
    
    return searchInput.value.trim();
}

console.log('Funcionalidades avanzadas cargadas');
