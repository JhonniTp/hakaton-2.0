// =============================================
// EXPLORAR HACKATHONS - Carga dinámica
// =============================================

document.addEventListener('DOMContentLoaded', function() {
    // Cargar cuando se muestra la sección
    const navHackathons = document.getElementById('nav-hackathons');
    if (navHackathons) {
        navHackathons.addEventListener('click', function() {
            setTimeout(() => cargarHackathonsDisponibles(), 100);
        });
    }
});

async function cargarHackathonsDisponibles() {
    try {
        const response = await fetch('/participante/api/hackathons-disponibles');
        const hackathons = await response.json();
        
        console.log('🔍 Hackathons disponibles:', hackathons);
        
        const container = document.getElementById('hackathons-disponibles-container');
        if (!container) return;
        
        if (!hackathons || hackathons.length === 0) {
            container.innerHTML = `
                <div class="col-span-full text-center py-16">
                    <i class="fas fa-search text-8xl text-gray-300 mb-4"></i>
                    <p class="text-gray-500 text-xl">No hay hackathons disponibles</p>
                    <p class="text-gray-400 text-sm mt-2">Vuelve pronto para nuevos desafíos</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = hackathons.map(h => `
            <div class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-2xl transition-all duration-300">
                <!-- Header con imagen -->
                <div class="h-48 relative overflow-hidden">
                    ${h.urlImg ? 
                        `<img src="${h.urlImg}" alt="${h.nombre}" class="w-full h-full object-cover">` :
                        `<div class="w-full h-full bg-gradient-to-r from-${getColorByState(h.estado)}-500 to-${getColorByState(h.estado)}-700"></div>`
                    }
                    <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-black/30 to-transparent"></div>
                    <div class="absolute bottom-0 left-0 p-6 text-white z-10">
                        <h3 class="text-2xl font-bold mb-2 drop-shadow-lg">${h.nombre}</h3>
                        <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${getBadgeClass(h.estado)}">
                            ${getEstadoTexto(h.estado)}
                        </span>
                    </div>
                </div>
                
                <!-- Body -->
                <div class="p-6">
                    <p class="text-gray-600 mb-4 line-clamp-3">${h.descripcion || 'Sin descripción'}</p>
                    
                    <!-- Info del hackathon -->
                    <div class="space-y-2 mb-4">
                        <div class="flex items-center text-sm text-gray-600">
                            <i class="far fa-calendar mr-2 text-blue-600"></i>
                            <span>${formatDate(h.fechaInicio)} - ${formatDate(h.fechaFin)}</span>
                        </div>
                        <div class="flex items-center text-sm text-gray-600">
                            <i class="fas fa-users mr-2 text-green-600"></i>
                            <span>${h.participantesActuales} / ${h.maximoParticipantes} participantes</span>
                        </div>
                        <div class="flex items-center text-sm text-gray-600">
                            <i class="fas fa-user-friends mr-2 text-purple-600"></i>
                            <span>Equipos de ${h.grupoCantidadParticipantes} personas</span>
                        </div>
                    </div>
                    
                    <!-- Barra de progreso -->
                    <div class="mb-4">
                        <div class="flex justify-between items-center mb-1">
                            <span class="text-xs text-gray-600">Cupos disponibles</span>
                            <span class="text-xs font-semibold text-blue-600">${h.lugaresDisponibles} restantes</span>
                        </div>
                        <div class="w-full bg-gray-200 rounded-full h-2">
                            <div class="bg-gradient-to-r from-blue-600 to-purple-600 h-2 rounded-full" 
                                 style="width: ${(h.participantesActuales / h.maximoParticipantes * 100)}%"></div>
                        </div>
                    </div>
                    
                    <!-- Botón de acción -->
                    ${h.estaInscrito ? 
                        '<button class="w-full bg-green-100 text-green-800 py-3 rounded-lg font-semibold" disabled>' +
                        '<i class="fas fa-check mr-2"></i>Ya estás inscrito</button>' :
                        h.lugaresDisponibles > 0 ?
                        `<button onclick="inscribirseHackathon(${h.idHackaton})" class="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-3 rounded-lg font-semibold hover:shadow-lg transform hover:scale-105 transition-all">
                            <i class="fas fa-rocket mr-2"></i>Inscribirse Ahora
                        </button>` :
                        '<button class="w-full bg-gray-300 text-gray-600 py-3 rounded-lg font-semibold" disabled>' +
                        '<i class="fas fa-lock mr-2"></i>Cupos Agotados</button>'
                    }
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        console.error('Error al cargar hackathons disponibles:', error);
    }
}

function getColorByState(estado) {
    switch(estado) {
        case 'EN_CURSO': return 'green';
        case 'PROXIMO': return 'blue';
        case 'FINALIZADO': return 'gray';
        default: return 'blue';
    }
}

function getBadgeClass(estado) {
    switch(estado) {
        case 'EN_CURSO': return 'bg-green-100 text-green-800';
        case 'PROXIMO': return 'bg-blue-100 text-blue-800';
        case 'FINALIZADO': return 'bg-gray-100 text-gray-800';
        default: return 'bg-blue-100 text-blue-800';
    }
}

function getEstadoTexto(estado) {
    switch(estado) {
        case 'EN_CURSO': return '🔥 En curso';
        case 'PROXIMO': return '📅 Próximo';
        case 'FINALIZADO': return '✓ Finalizado';
        default: return estado;
    }
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
}

async function inscribirseHackathon(idHackaton) {
    if (!confirm('¿Estás seguro de que deseas inscribirte en este hackathon?')) {
        return;
    }
    
    try {
        const response = await fetch(`/participante/api/inscribirse/${idHackaton}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            mostrarModal('success', '¡Inscripción Exitosa!', result.mensaje || 'Te has inscrito exitosamente al hackathon');
            // Recargar la lista de hackathons
            setTimeout(() => cargarHackathonsDisponibles(), 1000);
        } else {
            mostrarModal('error', 'Error de Inscripción', result.mensaje || result.error || 'No se pudo completar la inscripción');
        }
    } catch (error) {
        console.error('Error al inscribirse:', error);
        mostrarModal('error', 'Error de Conexión', 'No se pudo conectar con el servidor. Intenta nuevamente.');
    }
}

// Función para mostrar modal
function mostrarModal(tipo, titulo, mensaje) {
    const modal = document.getElementById('notification-modal');
    const iconContainer = document.getElementById('modal-icon-container');
    const icon = document.getElementById('modal-icon');
    const title = document.getElementById('modal-title');
    const messageEl = document.getElementById('modal-message');
    
    // Configurar estilos según el tipo
    if (tipo === 'success') {
        iconContainer.className = 'w-16 h-16 rounded-full flex items-center justify-center bg-green-100';
        icon.className = 'fas fa-check-circle text-3xl text-green-600';
    } else if (tipo === 'error') {
        iconContainer.className = 'w-16 h-16 rounded-full flex items-center justify-center bg-red-100';
        icon.className = 'fas fa-times-circle text-3xl text-red-600';
    } else if (tipo === 'warning') {
        iconContainer.className = 'w-16 h-16 rounded-full flex items-center justify-center bg-yellow-100';
        icon.className = 'fas fa-exclamation-circle text-3xl text-yellow-600';
    }
    
    title.textContent = titulo;
    messageEl.textContent = mensaje;
    
    // Mostrar modal con animación
    modal.classList.remove('hidden');
    setTimeout(() => {
        modal.querySelector('.modal-content').classList.remove('scale-95');
        modal.querySelector('.modal-content').classList.add('scale-100');
    }, 10);
}

// Función para cerrar modal
function cerrarModal() {
    const modal = document.getElementById('notification-modal');
    const content = modal.querySelector('.modal-content');
    
    content.classList.remove('scale-100');
    content.classList.add('scale-95');
    
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 200);
}
