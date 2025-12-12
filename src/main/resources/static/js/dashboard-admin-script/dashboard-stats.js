document.addEventListener('DOMContentLoaded', function () {
    cargarEstadisticas();
    setInterval(cargarEstadisticas, 30000);
});

async function cargarEstadisticas() {
    try {
        const response = await fetch('/api/dashboard/estadisticas');
        if (!response.ok) {
            console.error('Error al cargar estadísticas');
            return;
        }

        const stats = await response.json();
        actualizarTarjetas(stats);
    } catch (error) {
        console.error('Error:', error);
    }
}

function actualizarTarjetas(stats) {
    document.getElementById('stat-hackatones-activos').textContent = stats.hackatonesActivos || 0;
    document.getElementById('stat-hackatones-incremento').textContent =
        `+${stats.incrementoHackatonesEsteMes || 0} este mes`;

    document.getElementById('stat-participantes').textContent = stats.totalParticipantes || 0;
    document.getElementById('stat-participantes-incremento').textContent =
        `+${stats.incrementoParticipantesEstaSemana || 0} esta semana`;

    document.getElementById('stat-proyectos').textContent = stats.proyectosEntregados || 0;
    document.getElementById('stat-proyectos-incremento').textContent =
        `+${stats.incrementoProyectosHoy || 0} hoy`;

    document.getElementById('stat-evaluaciones').textContent = stats.evaluacionesPendientes || 0;
}
