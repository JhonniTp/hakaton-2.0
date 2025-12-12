document.addEventListener('DOMContentLoaded', function () {
    cargarActividades();
    setInterval(cargarActividades, 15000);

    const btnExportar = document.getElementById('btn-exportar-datos');
    if (btnExportar) {
        btnExportar.addEventListener('click', exportarDatos);
    }
});

async function cargarActividades() {
    try {
        const response = await fetch('/api/dashboard/actividades');
        if (!response.ok) {
            console.error('Error al cargar actividades');
            return;
        }

        const actividades = await response.json();
        renderizarActividades(actividades);
    } catch (error) {
        console.error('Error:', error);
    }
}

function renderizarActividades(actividades) {
    const container = document.getElementById('actividades-container');

    if (!actividades || actividades.length === 0) {
        container.innerHTML = `
            <div class="text-center py-8">
                <i class="fas fa-inbox text-gray-300 text-4xl mb-3"></i>
                <p class="text-gray-500">No hay actividades recientes</p>
            </div>
        `;
        return;
    }

    container.innerHTML = actividades.map(actividad => `
        <div class="flex items-start space-x-3">
            <div class="w-8 h-8 bg-${actividad.color.replace('-', '-')}-100 rounded-full flex items-center justify-center flex-shrink-0">
                <i class="fas ${actividad.icono} text-${actividad.color} text-sm"></i>
            </div>
            <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-dark">${getTituloActividad(actividad.tipo)}</p>
                <p class="text-sm text-gray-medium">${actividad.descripcion}</p>
                <p class="text-xs text-gray-400 mt-1">${actividad.tiempoTranscurrido}</p>
            </div>
        </div>
    `).join('');
}

function getTituloActividad(tipo) {
    const titulos = {
        'PARTICIPANTE_REGISTRADO': 'Nuevo participante',
        'PROYECTO_ENTREGADO': 'Proyecto entregado',
        'EVALUACION_COMPLETADA': 'Evaluación completada',
        'EQUIPO_FORMADO': 'Equipo formado',
        'HACKATON_CREADO': 'Hackatón creado',
        'JURADO_ASIGNADO': 'Jurado asignado'
    };
    return titulos[tipo] || 'Actividad';
}

async function exportarDatos() {
    try {
        const response = await fetch('/api/dashboard/exportar');
        if (!response.ok) {
            alert('Error al exportar datos');
            return;
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `dashboard_export_${new Date().getTime()}.csv`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    } catch (error) {
        console.error('Error:', error);
        alert('Error al exportar datos');
    }
}
