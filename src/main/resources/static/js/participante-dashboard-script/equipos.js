// =============================================
// MIS EQUIPOS - Carga dinámica
// =============================================

document.addEventListener('DOMContentLoaded', function() {
    const navTeams = document.getElementById('nav-teams');
    if (navTeams) {
        navTeams.addEventListener('click', function() {
            setTimeout(() => cargarMisEquipos(), 100);
        });
    }
});

async function cargarMisEquipos() {
    try {
        const response = await fetch('/participante/api/mis-equipos');
        const equipos = await response.json();
        
        console.log('👥 Mis equipos:', equipos);
        
        const container = document.getElementById('mis-equipos-container');
        if (!container) return;
        
        // Actualizar estadísticas
        const totalEquipos = equipos.length;
        const equiposActivos = equipos.filter(e => e.estadoHackathon === 'EN_CURSO').length;
        const proyectos = equipos.length; // Asumiendo 1 proyecto por equipo
        
        document.getElementById('stat-total-equipos').textContent = totalEquipos;
        document.getElementById('stat-equipos-activos').textContent = equiposActivos;
        document.getElementById('stat-proyectos-equipo').textContent = proyectos;
        
        if (!equipos || equipos.length === 0) {
            container.innerHTML = `
                <div class="col-span-full text-center py-16">
                    <div class="mb-6">
                        <i class="fas fa-users text-8xl text-gray-300 mb-4"></i>
                    </div>
                    <h3 class="text-2xl font-bold text-gray-600 mb-2">No tienes equipos aún</h3>
                    <p class="text-gray-400 text-sm mt-2 mb-8">Inscríbete en un hackathon para formar parte de un equipo o crear uno nuevo</p>
                    <div class="flex gap-4 justify-center">
                        <button onclick="showSection('hackathons')" class="bg-gradient-to-r from-blue-600 to-cyan-600 text-white px-8 py-4 rounded-xl font-semibold hover:shadow-lg transform hover:scale-105 transition-all">
                            <i class="fas fa-search mr-2"></i>Explorar Hackathons
                        </button>
                        <button onclick="mostrarFormularioCrearEquipo()" class="bg-gradient-to-r from-purple-600 to-pink-600 text-white px-8 py-4 rounded-xl font-semibold hover:shadow-lg transform hover:scale-105 transition-all">
                            <i class="fas fa-plus mr-2"></i>Crear Equipo
                        </button>
                    </div>
                </div>
            `;
            return;
        }
        
        container.innerHTML = equipos.map((equipo, index) => `
            <div class="bg-white rounded-xl shadow-md overflow-hidden hover-lift card-animated" style="animation-delay: ${index * 0.1}s">
                <!-- Header con gradiente dinámico -->
                <div class="bg-gradient-to-br from-purple-600 via-pink-600 to-red-500 p-6 text-white relative overflow-hidden">
                    <div class="absolute inset-0 bg-black/10"></div>
                    <div class="relative z-10">
                        <div class="flex items-start justify-between mb-3">
                            <div class="flex-1">
                                <h3 class="text-2xl font-bold mb-2 drop-shadow-lg">${equipo.nombreEquipo}</h3>
                                <p class="text-white/90 text-sm flex items-center">
                                    <i class="fas fa-trophy mr-2"></i>${equipo.nombreHackaton}
                                </p>
                            </div>
                            <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-white/20 backdrop-blur-sm border border-white/30">
                                ${getEstadoEquipo(equipo.estadoHackathon)}
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- Body mejorado -->
                <div class="p-6">
                    <!-- Info del equipo con íconos mejorados -->
                    <div class="space-y-3 mb-6">
                        <div class="flex items-center p-3 bg-yellow-50 rounded-lg border border-yellow-200">
                            <div class="w-10 h-10 bg-yellow-500 rounded-full flex items-center justify-center mr-3">
                                <i class="fas fa-crown text-white"></i>
                            </div>
                            <div>
                                <p class="text-xs text-yellow-600 font-medium">Líder del Equipo</p>
                                <p class="text-sm font-bold text-yellow-900">${equipo.nombreLider || 'Sin asignar'}</p>
                            </div>
                        </div>
                        
                        <div class="grid grid-cols-2 gap-3">
                            <div class="flex items-center p-3 bg-blue-50 rounded-lg border border-blue-200">
                                <i class="fas fa-users text-blue-600 text-xl mr-2"></i>
                                <div>
                                    <p class="text-xs text-blue-600 font-medium">Miembros</p>
                                    <p class="text-lg font-bold text-blue-900">${equipo.cantidadMiembros}</p>
                                </div>
                            </div>
                            
                            <div class="flex items-center p-3 bg-green-50 rounded-lg border border-green-200">
                                <i class="far fa-calendar text-green-600 text-xl mr-2"></i>
                                <div>
                                    <p class="text-xs text-green-600 font-medium">Creado</p>
                                    <p class="text-xs font-bold text-green-900">${formatDate(equipo.fechaCreacion)}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Botones de acción mejorados -->
                    <div class="flex gap-3">
                        <button onclick="verDetalleEquipo(${equipo.idEquipo})" 
                                class="flex-1 bg-gradient-to-r from-purple-600 to-pink-600 text-white py-3 rounded-lg font-semibold hover:shadow-lg transform hover:scale-105 transition-all">
                            <i class="fas fa-eye mr-2"></i>Ver Detalle
                        </button>
                        <button onclick="chatEquipo(${equipo.idEquipo})" 
                                class="px-4 py-3 bg-green-100 text-green-700 rounded-lg font-semibold hover:bg-green-200 transition-all">
                            <i class="fas fa-comments"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        console.error('Error al cargar equipos:', error);
    }
}

function getEstadoEquipo(estado) {
    switch(estado) {
        case 'EN_CURSO': return '🔥 Activo';
        case 'PROXIMO': return '📅 Próximo';
        case 'FINALIZADO': return '✓ Finalizado';
        default: return estado;
    }
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
}

function verDetalleEquipo(idEquipo) {
    alert(`Ver detalle del equipo ID: ${idEquipo}`);
}

function chatEquipo(idEquipo) {
    alert(`Chat del equipo ID: ${idEquipo} - Próximamente disponible`);
}

async function mostrarFormularioCrearEquipo() {
    // Cargar hackathons disponibles
    const response = await fetch('/participante/api/hackathons-activos');
    const hackathons = await response.json();
    
    const select = document.getElementById('hackathon-select');
    
    if (hackathons.length === 0) {
        select.innerHTML = '<option value="">No estás inscrito en ningún hackathon</option>';
        mostrarModal('warning', 'Sin Hackathons', 'Debes inscribirte en un hackathon antes de crear un equipo');
        return;
    }
    
    // Llenar el select con los hackathons
    select.innerHTML = '<option value="">-- Selecciona un hackathon --</option>' +
        hackathons.map(h => `<option value="${h.idHackathon}">${h.nombre}</option>`).join('');
    
    // Mostrar el modal
    const modal = document.getElementById('crear-equipo-modal');
    modal.classList.remove('hidden');
    setTimeout(() => {
        modal.querySelector('.modal-content').classList.remove('scale-95');
        modal.querySelector('.modal-content').classList.add('scale-100');
    }, 10);
    
    // Configurar el formulario
    const form = document.getElementById('form-crear-equipo');
    form.onsubmit = async (e) => {
        e.preventDefault();
        
        const nombreEquipo = document.getElementById('nombre-equipo').value.trim();
        const idHackaton = parseInt(document.getElementById('hackathon-select').value);
        
        if (!nombreEquipo || !idHackaton) {
            mostrarModal('warning', 'Campos Incompletos', 'Por favor completa todos los campos');
            return;
        }
        
        await crearEquipo({ nombreEquipo, idHackaton });
    };
}

function cerrarModalCrearEquipo() {
    const modal = document.getElementById('crear-equipo-modal');
    const content = modal.querySelector('.modal-content');
    
    content.classList.remove('scale-100');
    content.classList.add('scale-95');
    
    setTimeout(() => {
        modal.classList.add('hidden');
        // Limpiar formulario
        document.getElementById('form-crear-equipo').reset();
    }, 200);
}

async function crearEquipo(datos) {
    try {
        const response = await fetch('/participante/api/crear-equipo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });
        
        const result = await response.json();
        
        if (response.ok && result.success) {
            cerrarModalCrearEquipo();
            mostrarModal('success', '¡Equipo Creado!', 
                result.mensaje + ' Se creó automáticamente el proyecto del equipo.');
            setTimeout(() => cargarMisEquipos(), 1000);
        } else {
            mostrarModal('error', 'Error al Crear Equipo', result.mensaje || 'No se pudo crear el equipo');
        }
    } catch (error) {
        console.error('Error al crear equipo:', error);
        mostrarModal('error', 'Error de Conexión', 'No se pudo conectar con el servidor');
    }
}
