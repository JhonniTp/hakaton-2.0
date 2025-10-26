// User menu toggle
function toggleUserMenu() {
    const userMenu = document.getElementById('userMenu');
    userMenu.classList.toggle('hidden');
}

// Section navigation
function showSection(sectionName) {
    // Hide all sections
    const sections = ['dashboard', 'hackathons', 'my-hackathons', 'teams', 'projects', 'leaderboard', 'profile'];
    sections.forEach(section => {
        const sectionElement = document.getElementById(`${section}-section`);
        if (sectionElement) {
            sectionElement.classList.add('hidden');
        }
    });

    // Show selected section
    const targetSection = document.getElementById(`${sectionName}-section`);
    if (targetSection) {
        targetSection.classList.remove('hidden');
    }

    // Update navigation active state
    document.querySelectorAll('.nav-item').forEach(link => {
        link.classList.remove('active-nav', 'text-primary-blue');
        link.classList.add('text-gray-medium');
    });

    const activeLink = document.getElementById(`nav-${sectionName}`);
    if (activeLink) {
        activeLink.classList.add('active-nav', 'text-primary-blue');
        activeLink.classList.remove('text-gray-medium');
    }
}

// Generate QR Code
function generateQR() {
    const canvas = document.getElementById('qrCanvas');
    if (canvas && typeof QRCode !== 'undefined') {
        const participantData = {
            id: 'CERT-2024-001',
            name: 'María González',
            email: 'maria.gonzalez@certus.edu.pe',
            career: 'Ingeniería de Sistemas',
            profile: 'https://hackaton.certus.edu.pe/participant/maria-gonzalez'
        };

        QRCode.toCanvas(canvas, JSON.stringify(participantData), {
            width: 120,
            margin: 1,
            color: {
                dark: '#031F56',
                light: '#FFFFFF'
            }
        }, function (error) {
            if (error) console.error(error);
        });
    }
}

// Download QR Code
function downloadQR() {
    const canvas = document.getElementById('qrCanvas');
    if (canvas) {
        const link = document.createElement('a');
        link.download = 'mi-qr-certus-hackaton.png';
        link.href = canvas.toDataURL();
        link.click();
    }
}

// Modal functions
function openModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
    document.body.style.overflow = 'auto';
}

// Join hackathon function
function joinHackathon(hackathonName) {
    document.getElementById('hackathonName').textContent = `Inscribirse a ${hackathonName}`;
    openModal('joinHackathonModal');
}

function confirmJoinHackathon() {
    alert('¡Te has inscrito exitosamente al hackaton!');
    closeModal('joinHackathonModal');
}

// Create team function
function createTeam() {
    alert('¡Equipo creado exitosamente!');
    closeModal('createTeamModal');
}

// Save profile function
function saveProfile() {
    alert('¡Perfil actualizado exitosamente!');
}

// Close modals when clicking outside
document.addEventListener('click', function (event) {
    const modals = ['joinHackathonModal', 'createTeamModal', 'projectModal'];

    modals.forEach(modalId => {
        const modal = document.getElementById(modalId);
        if (event.target === modal) {
            closeModal(modalId);
        }
    });
});

// Close modals with Escape key
document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        const modals = ['joinHackathonModal', 'createTeamModal', 'projectModal'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (!modal.classList.contains('hidden')) {
                closeModal(modalId);
            }
        });
    }
});

// Close user menu when clicking outside
document.addEventListener('click', function (event) {
    const userMenu = document.getElementById('userMenu');
    const userButton = event.target.closest('button');

    if (!userButton || !userButton.onclick || userButton.onclick.toString().indexOf('toggleUserMenu') === -1) {
        userMenu.classList.add('hidden');
    }
});

// Initialize page
document.addEventListener('DOMContentLoaded', function () {
    // Generate QR code when page loads
    setTimeout(generateQR, 500);

    // Add floating animation to elements
    const floatingElements = document.querySelectorAll('.floating-element');
    floatingElements.forEach((element, index) => {
        element.style.animationDelay = `${index * 0.5}s`;
    });
});