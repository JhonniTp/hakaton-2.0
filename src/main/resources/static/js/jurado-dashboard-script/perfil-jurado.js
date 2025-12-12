let perfilJurado = {};

async function cargarPerfil() {
    try {
        const response = await fetch('/jurado/api/perfil');

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error al cargar perfil:', response.status, errorText);
            return;
        }

        perfilJurado = await response.json();
        console.log('Perfil cargado:', perfilJurado);
        renderizarPerfil();
    } catch (error) {
        console.error('Error en cargarPerfil:', error);
    }
}

function renderizarPerfil() {
    document.getElementById('perfil-nombre-completo').value = perfilJurado.nombreCompleto || '';
    document.getElementById('perfil-email').value = perfilJurado.correoElectronico || '';
    document.getElementById('perfil-telefono').value = perfilJurado.telefono || '';
    document.getElementById('perfil-biografia').value = perfilJurado.perfilExperiencia || '';

    document.getElementById('perfil-header-nombre').textContent = perfilJurado.nombreCompleto || 'Jurado';
    document.getElementById('perfil-header-iniciales').textContent = perfilJurado.iniciales || 'JU';

    if (perfilJurado.estadisticas) {
        document.getElementById('stat-proyectos-evaluados').textContent = perfilJurado.estadisticas.proyectosEvaluados || 0;
        document.getElementById('stat-promedio-puntuacion').textContent = (perfilJurado.estadisticas.promedioCalificacion || 0).toFixed(1);
        document.getElementById('stat-hackathons-activos').textContent = perfilJurado.estadisticas.hackatonesActivos || 0;
    }
}

async function guardarPerfilJurado() {
    const datos = {
        nombre: document.getElementById('perfil-nombre-completo').value.split(' ')[0],
        apellido: document.getElementById('perfil-nombre-completo').value.split(' ').slice(1).join(' '),
        telefono: document.getElementById('perfil-telefono').value,
        perfilExperiencia: document.getElementById('perfil-biografia').value
    };

    try {
        const response = await fetch('/jurado/api/perfil', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        const data = await response.json();

        if (data.success) {
            await cargarPerfil();
            alert(data.mensaje);
        } else {
            alert('Error: ' + data.mensaje);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar el perfil');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const perfilSection = document.getElementById('profile-section');

    const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            if (mutation.attributeName === 'class') {
                if (!perfilSection.classList.contains('hidden')) {
                    cargarPerfil();
                }
            }
        });
    });

    if (perfilSection) {
        observer.observe(perfilSection, { attributes: true });
    }
});

window.saveJudgeProfile = guardarPerfilJurado;
