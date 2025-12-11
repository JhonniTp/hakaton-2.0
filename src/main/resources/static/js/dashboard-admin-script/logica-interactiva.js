function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');

    sidebar.classList.toggle('-translate-x-full');
    overlay.classList.toggle('hidden');
}

function toggleUserMenu() {
    const userMenu = document.getElementById('userMenu');
    userMenu.classList.toggle('hidden');
}

function showSection(sectionName) {
    const sections = ['dashboard', 'hackathons', 'participants', 'teams', 'judges', 'evaluations', 'reports', 'settings'];
    sections.forEach(section => {
        document.getElementById(`${section}-section`).classList.add('hidden');
    });

    document.getElementById(`${sectionName}-section`).classList.remove('hidden');

    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('nav-link-active', 'text-primary-white');
        link.classList.add('text-gray-300');
    });

    const activeLink = document.getElementById(`nav-${sectionName}`);
    activeLink.classList.add('nav-link-active', 'text-primary-white');
    activeLink.classList.remove('text-gray-300');

    const titles = {
        'dashboard': 'Dashboard Administrativo',
        'hackathons': 'Gestión de Hackatones',
        'participants': 'Gestión de Participantes',
        'teams': 'Gestión de Equipos',
        'judges': 'Gestión de Jurados',
        'evaluations': 'Gestión de Evaluaciones',
        'reports': 'Reportes',
        'settings': 'Configuración'
    };
    document.getElementById('pageTitle').textContent = titles[sectionName];

    if (sectionName === 'hackathons') {
        if (typeof cargarHackatones === 'function') {
            cargarHackatones();
        }
    } else if (sectionName === 'participants') {
        if (typeof cargarUsuarios === 'function') {
            cargarUsuarios();
        }
    } else if (sectionName === 'judges') {
        if (typeof cargarAsignaciones === 'function') {
            cargarAsignaciones();
        }
    } else if (sectionName === 'teams') {
        if (typeof cargarEquipos === 'function') {
            cargarEquipos();
        }
    }

    if (window.innerWidth < 1024) {
        toggleSidebar();
    }
}

function openModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
    document.body.style.overflow = 'auto';
}

function openEditModal() {
    closeModal('viewDetailsModal');
    openModal('editHackathonModal');
}

function confirmDelete() {
    closeModal('editHackathonModal');
    openModal('deleteConfirmModal');
}

function createHackathon() {
    alert('¡Hackaton creado exitosamente!');
    closeModal('createHackathonModal');
}

function updateHackathon() {
    alert('¡Hackaton actualizado exitosamente!');
    closeModal('editHackathonModal');
}

function deleteHackathon() {
    alert('¡Hackaton eliminado exitosamente!');
    closeModal('deleteConfirmModal');
}

function addJudge() {
    alert('¡Jurado agregado exitosamente!');
    closeModal('addJudgeModal');
}

document.addEventListener('click', function (event) {
    const modals = ['createHackathonModal', 'viewDetailsModal', 'editHackathonModal', 'deleteConfirmModal', 'addJudgeModal'];

    modals.forEach(modalId => {
        const modal = document.getElementById(modalId);
        if (event.target === modal) {
            closeModal(modalId);
        }
    });
});

document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        const modals = ['createHackathonModal', 'viewDetailsModal', 'editHackathonModal', 'deleteConfirmModal', 'addJudgeModal'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (!modal.classList.contains('hidden')) {
                closeModal(modalId);
            }
        });
    }
});

document.addEventListener('click', function (event) {
    const userMenu = document.getElementById('userMenu');
    const userButton = event.target.closest('button');

    if (!userButton || !userButton.onclick || userButton.onclick.toString().indexOf('toggleUserMenu') === -1) {
        userMenu.classList.add('hidden');
    }
});

window.addEventListener('resize', function () {
    if (window.innerWidth >= 1024) {
        document.getElementById('sidebar').classList.remove('-translate-x-full');
        document.getElementById('sidebarOverlay').classList.add('hidden');
    } else {
        document.getElementById('sidebar').classList.add('-translate-x-full');
        document.getElementById('sidebarOverlay').classList.add('hidden');
    }
});

function showUserForm() {
    document.getElementById('user-list-view').classList.add('hidden');
    document.getElementById('user-form-view').classList.remove('hidden');
}

function showUserList() {
    document.getElementById('user-form-view').classList.add('hidden');
    document.getElementById('user-list-view').classList.remove('hidden');
}