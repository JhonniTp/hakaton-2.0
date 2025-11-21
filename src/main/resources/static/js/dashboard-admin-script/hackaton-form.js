let hackatonEnEdicion = null;
let categorias = [];

async function prepararFormularioNuevoHackaton() {
    hackatonEnEdicion = null;
    await cargarCategorias();
    limpiarFormularioHackaton();
    document.getElementById('hackaton-form-title').textContent = 'Nuevo Hackatón';
    document.getElementById('hackaton-id').value = '';
}

async function cargarHackatonParaEditar(id) {
    try {
        const response = await fetch(`/api/admin/hackatones/${id}`);
        if (!response.ok) {
            throw new Error('Error al cargar el hackatón');
        }
        
        hackatonEnEdicion = await response.json();
        await cargarCategorias();
        llenarFormularioHackaton(hackatonEnEdicion);
        document.getElementById('hackaton-form-title').textContent = 'Editar Hackatón';
    } catch (error) {
        console.error('Error al cargar hackatón:', error);
        mostrarMensajeError('Error al cargar los datos del hackatón');
        cancelarFormularioHackaton();
    }
}

async function cargarCategorias() {
    try {
        const response = await fetch('/api/admin/categorias');
        if (!response.ok) {
            throw new Error('Error al cargar categorías');
        }
        
        categorias = await response.json();
        const selectCategoria = document.getElementById('hackaton-category');
        
        if (selectCategoria) {
            selectCategoria.innerHTML = '<option value="">Seleccione una categoría</option>' +
                categorias.map(cat => 
                    `<option value="${cat.idCategoria}">${cat.nombreCategoria}</option>`
                ).join('');
        }
    } catch (error) {
        console.error('Error al cargar categorías:', error);
        mostrarMensajeError('Error al cargar las categorías');
    }
}

function llenarFormularioHackaton(hackaton) {
    document.getElementById('hackaton-id').value = hackaton.idHackaton || '';
    document.getElementById('hackaton-name').value = hackaton.nombre || '';
    document.getElementById('hackaton-description').value = hackaton.descripcion || '';
    document.getElementById('hackaton-image').value = hackaton.urlImg || '';
    document.getElementById('hackaton-category').value = hackaton.idCategoria || '';
    
    if (hackaton.fechaInicio) {
        const fechaInicio = new Date(hackaton.fechaInicio);
        document.getElementById('hackaton-start-date').value = formatearFechaParaInput(fechaInicio);
    }
    
    if (hackaton.fechaFin) {
        const fechaFin = new Date(hackaton.fechaFin);
        document.getElementById('hackaton-end-date').value = formatearFechaParaInput(fechaFin);
    }
    
    document.getElementById('hackaton-max-participants').value = hackaton.maximoParticipantes || '';
    document.getElementById('hackaton-team-size').value = hackaton.grupoCantidadParticipantes || '';
    document.getElementById('hackaton-status').value = hackaton.estado || 'PROXIMO';
}

function formatearFechaParaInput(fecha) {
    const year = fecha.getFullYear();
    const month = String(fecha.getMonth() + 1).padStart(2, '0');
    const day = String(fecha.getDate()).padStart(2, '0');
    const hours = String(fecha.getHours()).padStart(2, '0');
    const minutes = String(fecha.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function limpiarFormularioHackaton() {
    const form = document.getElementById('hackaton-form');
    if (form) {
        form.reset();
    }
    hackatonEnEdicion = null;
}

async function guardarHackaton() {
    const formulario = document.getElementById('hackaton-form');
    
    if (!formulario.checkValidity()) {
        formulario.reportValidity();
        return;
    }
    
    const hackatonData = {
        nombre: document.getElementById('hackaton-name').value.trim(),
        descripcion: document.getElementById('hackaton-description').value.trim(),
        urlImg: document.getElementById('hackaton-image').value.trim(),
        idCategoria: parseInt(document.getElementById('hackaton-category').value),
        fechaInicio: document.getElementById('hackaton-start-date').value,
        fechaFin: document.getElementById('hackaton-end-date').value,
        maximoParticipantes: parseInt(document.getElementById('hackaton-max-participants').value),
        grupoCantidadParticipantes: parseInt(document.getElementById('hackaton-team-size').value),
        estado: document.getElementById('hackaton-status').value
    };
    
    if (new Date(hackatonData.fechaFin) <= new Date(hackatonData.fechaInicio)) {
        mostrarMensajeError('La fecha de fin debe ser posterior a la fecha de inicio');
        return;
    }
    
    try {
        const hackatonId = document.getElementById('hackaton-id').value;
        const esEdicion = hackatonId !== '';
        
        const url = esEdicion 
            ? `/api/admin/hackatones/${hackatonId}`
            : '/api/admin/hackatones';
        
        const method = esEdicion ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(hackatonData)
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            mostrarMensajeExito(data.mensaje || 'Hackatón guardado exitosamente');
            cancelarFormularioHackaton();
            await cargarHackatones();
        } else {
            mostrarMensajeError(data.mensaje || 'Error al guardar el hackatón');
        }
    } catch (error) {
        console.error('Error al guardar hackatón:', error);
        mostrarMensajeError('Error de conexión al guardar el hackatón');
    }
}

function cancelarFormularioHackaton() {
    const formView = document.getElementById('hackaton-form-view');
    const listView = document.getElementById('hackaton-list-view');
    
    if (formView && listView) {
        formView.classList.add('hidden');
        listView.classList.remove('hidden');
        limpiarFormularioHackaton();
    }
}
