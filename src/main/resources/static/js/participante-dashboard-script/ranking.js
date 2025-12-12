// =============================================
// RANKING - Carga dinámica
// =============================================

document.addEventListener('DOMContentLoaded', function() {
    const navRanking = document.getElementById('nav-leaderboard');
    if (navRanking) {
        navRanking.addEventListener('click', function() {
            setTimeout(() => cargarRanking(), 100);
        });
    }
});

async function cargarRanking() {
    try {
        const response = await fetch('/participante/api/ranking');
        const ranking = await response.json();
        
        console.log('🏆 Ranking:', ranking);
        
        const tbody = document.getElementById('ranking-tbody');
        if (!tbody) return;
        
        if (!ranking || ranking.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-16">
                        <i class="fas fa-trophy text-8xl text-gray-300 mb-4"></i>
                        <p class="text-gray-500 text-xl">No hay datos de ranking</p>
                        <p class="text-gray-400 text-sm mt-2">Participa en hackathons para aparecer en el ranking</p>
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = ranking.map(participante => `
            <tr class="hover:bg-gray-50 transition-colors ${participante.esUsuarioActual ? 'bg-blue-50 font-semibold' : ''}">
                <!-- Posición -->
                <td class="px-6 py-4 whitespace-nowrap text-center">
                    ${getPosicionBadge(participante.posicion)}
                </td>
                
                <!-- Participante -->
                <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                        <div class="flex-shrink-0 h-10 w-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white font-bold">
                            ${participante.nombreCompleto.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()}
                        </div>
                        <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900">
                                ${participante.nombreCompleto}
                                ${participante.esUsuarioActual ? '<span class="ml-2 text-blue-600">(Tú)</span>' : ''}
                            </div>
                            <div class="text-sm text-gray-500">${participante.carrera}</div>
                        </div>
                    </div>
                </td>
                
                <!-- Hackathons -->
                <td class="px-6 py-4 whitespace-nowrap text-center">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        ${participante.hackathonesParticipados}
                    </span>
                </td>
                
                <!-- Proyectos -->
                <td class="px-6 py-4 whitespace-nowrap text-center">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                        ${participante.proyectosCompletados}
                    </span>
                </td>
                
                <!-- Puntuación -->
                <td class="px-6 py-4 whitespace-nowrap text-center">
                    <div class="flex items-center justify-center">
                        <i class="fas fa-star text-yellow-500 mr-1"></i>
                        <span class="text-sm font-semibold text-gray-900">
                            ${participante.puntuacionPromedio ? participante.puntuacionPromedio.toFixed(2) : '0.00'}
                        </span>
                    </div>
                </td>
                
                <!-- Logros -->
                <td class="px-6 py-4 whitespace-nowrap text-center">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                        <i class="fas fa-award mr-1"></i>${participante.logrosObtenidos}
                    </span>
                </td>
                
                <!-- Acciones -->
                <td class="px-6 py-4 whitespace-nowrap text-center">
                    <button onclick="verPerfilParticipante(${participante.idUsuario})" 
                            class="text-blue-600 hover:text-blue-900 transition-colors">
                        <i class="fas fa-eye"></i>
                    </button>
                </td>
            </tr>
        `).join('');
        
        // Actualizar estadísticas generales
        actualizarEstadisticasRanking(ranking);
        
    } catch (error) {
        console.error('Error al cargar ranking:', error);
    }
}

function getPosicionBadge(posicion) {
    if (posicion === 1) {
        return `<span class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-gradient-to-br from-yellow-400 to-yellow-600 text-white text-lg font-bold shadow-lg">
                    🥇
                </span>`;
    } else if (posicion === 2) {
        return `<span class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-gradient-to-br from-gray-300 to-gray-500 text-white text-lg font-bold shadow-lg">
                    🥈
                </span>`;
    } else if (posicion === 3) {
        return `<span class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-gradient-to-br from-orange-400 to-orange-600 text-white text-lg font-bold shadow-lg">
                    🥉
                </span>`;
    } else {
        return `<span class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-gray-200 text-gray-700 text-sm font-semibold">
                    ${posicion}
                </span>`;
    }
}

function actualizarEstadisticasRanking(ranking) {
    // Calcular promedios y totales
    const totalParticipantes = ranking.length;
    const promedioHackathons = (ranking.reduce((sum, p) => sum + p.hackathonesParticipados, 0) / totalParticipantes).toFixed(1);
    const promedioProyectos = (ranking.reduce((sum, p) => sum + p.proyectosCompletados, 0) / totalParticipantes).toFixed(1);
    const promedioPuntuacion = (ranking.reduce((sum, p) => sum + (p.puntuacionPromedio || 0), 0) / totalParticipantes).toFixed(2);
    
    // Actualizar UI si existen los elementos
    const statsContainer = document.getElementById('ranking-stats');
    if (statsContainer) {
        statsContainer.innerHTML = `
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
                <div class="bg-white rounded-lg shadow p-4 text-center">
                    <div class="text-3xl font-bold text-blue-600">${totalParticipantes}</div>
                    <div class="text-sm text-gray-600">Total Participantes</div>
                </div>
                <div class="bg-white rounded-lg shadow p-4 text-center">
                    <div class="text-3xl font-bold text-green-600">${promedioHackathons}</div>
                    <div class="text-sm text-gray-600">Promedio Hackathons</div>
                </div>
                <div class="bg-white rounded-lg shadow p-4 text-center">
                    <div class="text-3xl font-bold text-purple-600">${promedioProyectos}</div>
                    <div class="text-sm text-gray-600">Promedio Proyectos</div>
                </div>
                <div class="bg-white rounded-lg shadow p-4 text-center">
                    <div class="text-3xl font-bold text-yellow-600">${promedioPuntuacion}</div>
                    <div class="text-sm text-gray-600">Puntuación Promedio</div>
                </div>
            </div>
        `;
    }
}

function verPerfilParticipante(idUsuario) {
    alert(`Ver perfil del participante ID: ${idUsuario}`);
}

function exportarRanking() {
    alert('Funcionalidad de exportar ranking en desarrollo');
}
