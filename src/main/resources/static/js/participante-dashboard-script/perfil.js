// =============================================
// MI PERFIL - Carga dinámica
// =============================================

document.addEventListener('DOMContentLoaded', function() {
    const navProfile = document.getElementById('nav-profile');
    if (navProfile) {
        navProfile.addEventListener('click', function() {
            setTimeout(() => cargarMiPerfil(), 100);
        });
    }
});

async function cargarMiPerfil() {
    try {
        const response = await fetch('/participante/api/mi-perfil');
        const perfil = await response.json();
        
        console.log('👤 Mi perfil:', perfil);
        
        // Guardar perfil actual para edición
        perfilActual = perfil;
        
        // Actualizar header del perfil
        const headerIniciales = document.getElementById('perfil-header-iniciales');
        const headerNombre = document.getElementById('perfil-header-nombre');
        const headerRol = document.getElementById('perfil-header-rol');
        
        if (headerIniciales) headerIniciales.textContent = perfil.iniciales || perfil.nombre.substring(0, 2).toUpperCase();
        if (headerNombre) headerNombre.textContent = `${perfil.nombre} ${perfil.apellido}`.toUpperCase();
        if (headerRol) headerRol.textContent = perfil.rol || 'participante';
        
        // Actualizar información personal (usando .value para inputs)
        const nombreInput = document.getElementById('perfil-nombre');
        const correoInput = document.getElementById('perfil-correo');
        const telefonoInput = document.getElementById('perfil-telefono');
        const carreraInput = document.getElementById('perfil-carrera');
        const cicloInput = document.getElementById('perfil-ciclo');
        const biografiaElement = document.getElementById('perfil-biografia');
        
        if (nombreInput) nombreInput.value = `${perfil.nombre} ${perfil.apellido}`;
        if (correoInput) correoInput.value = perfil.correoElectronico;
        if (telefonoInput) telefonoInput.value = perfil.telefono || '';
        if (carreraInput) carreraInput.value = perfil.carrera || '';
        if (cicloInput) cicloInput.value = perfil.ciclo || '';
        if (biografiaElement) biografiaElement.textContent = perfil.biografia || 'Sin biografía';
        
        // Actualizar estadísticas
        document.getElementById('perfil-hackathons').textContent = perfil.totalHackathons || 0;
        document.getElementById('perfil-proyectos').textContent = perfil.proyectosCompletados || 0;
        document.getElementById('perfil-puntuacion').textContent = perfil.puntuacionPromedio ? perfil.puntuacionPromedio.toFixed(1) : '0.0';
        document.getElementById('perfil-logros').textContent = perfil.logrosObtenidos || 0;
        document.getElementById('perfil-ranking').textContent = perfil.posicionRanking ? `#${perfil.posicionRanking}` : 'N/A';
        
        // Actualizar habilidades
        const habilidadesContainer = document.getElementById('perfil-habilidades');
        if (habilidadesContainer) {
            if (perfil.habilidades && perfil.habilidades.length > 0) {
                habilidadesContainer.innerHTML = perfil.habilidades.map(h => 
                    `<span class="inline-block bg-blue-100 text-blue-800 text-xs px-3 py-1 rounded-full">${h}</span>`
                ).join('');
            } else {
                habilidadesContainer.innerHTML = '<span class="text-gray-400 text-sm">No hay habilidades registradas</span>';
            }
        }
        
        // Actualizar enlaces sociales
        const githubLink = document.getElementById('perfil-github');
        const linkedinLink = document.getElementById('perfil-linkedin');
        
        if (githubLink) {
            if (perfil.githubUrl) {
                githubLink.href = perfil.githubUrl;
                githubLink.classList.remove('hidden');
            } else {
                githubLink.classList.add('hidden');
            }
        }
        
        if (linkedinLink) {
            if (perfil.linkedinUrl) {
                linkedinLink.href = perfil.linkedinUrl;
                linkedinLink.classList.remove('hidden');
            } else {
                linkedinLink.classList.add('hidden');
            }
        }
        
        // Cargar logros dinámicamente
        cargarLogros(perfil);
        
        // Generar QR del perfil
        generarQRPerfil(perfil);
        
    } catch (error) {
        console.error('Error al cargar perfil:', error);
    }
}

async function cargarLogros(perfil) {
    const logrosContainer = document.getElementById('perfil-logros-lista');
    if (!logrosContainer) return;
    
    try {
        const response = await fetch('/participante/api/logros');
        const logros = await response.json();
        
        if (!logros || logros.length === 0) {
            logrosContainer.innerHTML = `
                <div class="text-center py-4">
                    <i class="fas fa-medal text-gray-300 text-3xl mb-2"></i>
                    <p class="text-sm text-gray-400">Aún no tienes logros</p>
                    <p class="text-xs text-gray-400">Participa en hackatones para obtener logros</p>
                </div>
            `;
            return;
        }
        
        logrosContainer.innerHTML = logros.map(logro => {
            const iconos = {
                'TROFEO': 'fa-trophy',
                'ESTRELLA': 'fa-star',
                'EQUIPO': 'fa-users',
                'MEDALLA': 'fa-medal'
            };
            
            const colores = {
                'TROFEO': 'bg-yellow-500',
                'ESTRELLA': 'bg-blue-500',
                'EQUIPO': 'bg-green-500',
                'MEDALLA': 'bg-purple-500'
            };
            
            return `
                <div class="flex items-center space-x-3">
                    <div class="w-8 h-8 ${colores[logro.tipo] || 'bg-gray-500'} rounded-full flex items-center justify-center">
                        <i class="fas ${iconos[logro.tipo] || 'fa-award'} text-white text-xs"></i>
                    </div>
                    <div>
                        <p class="text-sm font-medium text-gray-dark">${logro.titulo}</p>
                        <p class="text-xs text-gray-medium">${logro.descripcion}</p>
                    </div>
                </div>
            `;
        }).join('');
        
    } catch (error) {
        console.error('Error al cargar logros:', error);
        logrosContainer.innerHTML = `
            <div class="text-center py-4">
                <p class="text-sm text-gray-400">No se pudieron cargar los logros</p>
            </div>
        `;
    }
}

function generarQRPerfil(perfil) {
    const qrContainer = document.getElementById('qr-perfil');
    if (!qrContainer) return;
    
    // Crear canvas para QR code
    qrContainer.innerHTML = `
        <div class="bg-white p-4 rounded-lg shadow-sm">
            <div class="w-32 h-32 bg-gray-100 rounded-lg flex items-center justify-center">
                <i class="fas fa-qrcode text-6xl text-gray-300"></i>
            </div>
        </div>
    `;
}

let perfilActual = {};

function editarPerfil() {
    const modal = document.getElementById('modal-editar-perfil');
    if (!modal) return;
    
    // Llenar el formulario con los datos actuales
    document.getElementById('edit-nombre').value = perfilActual.nombre || '';
    document.getElementById('edit-apellido').value = perfilActual.apellido || '';
    document.getElementById('edit-telefono').value = perfilActual.telefono || '';
    document.getElementById('edit-carrera').value = perfilActual.carrera || '';
    document.getElementById('edit-ciclo').value = perfilActual.ciclo || '';
    document.getElementById('edit-biografia').value = perfilActual.biografia || '';
    
    // Mostrar modal con animación
    modal.classList.remove('hidden');
    setTimeout(() => {
        modal.querySelector('.modal-content').classList.remove('scale-95');
        modal.querySelector('.modal-content').classList.add('scale-100');
    }, 10);
}

function cerrarModalEditarPerfil() {
    const modal = document.getElementById('modal-editar-perfil');
    const content = modal.querySelector('.modal-content');
    
    content.classList.remove('scale-100');
    content.classList.add('scale-95');
    
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 200);
}

async function guardarPerfilEditado(event) {
    event.preventDefault();
    
    const formData = {
        nombre: document.getElementById('edit-nombre').value.trim(),
        apellido: document.getElementById('edit-apellido').value.trim(),
        telefono: document.getElementById('edit-telefono').value.trim(),
        carrera: document.getElementById('edit-carrera').value.trim(),
        ciclo: document.getElementById('edit-ciclo').value.trim(),
        biografia: document.getElementById('edit-biografia').value.trim()
    };
    
    try {
        const response = await fetch('/participante/api/mi-perfil', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            mostrarModal('success', '¡Éxito!', 'Perfil actualizado correctamente');
            cerrarModalEditarPerfil();
            
            // Recargar el perfil
            setTimeout(() => cargarMiPerfil(), 500);
        } else {
            mostrarModal('error', 'Error', result.mensaje || 'No se pudo actualizar el perfil');
        }
    } catch (error) {
        console.error('Error al guardar perfil:', error);
        mostrarModal('error', 'Error', 'Ocurrió un error al actualizar el perfil');
    }
}

// Inicializar formulario
document.addEventListener('DOMContentLoaded', function() {
    const formEditar = document.getElementById('form-editar-perfil');
    if (formEditar) {
        formEditar.addEventListener('submit', guardarPerfilEditado);
    }
});

function subirFoto() {
    alert('Funcionalidad de subir foto en desarrollo');
}
