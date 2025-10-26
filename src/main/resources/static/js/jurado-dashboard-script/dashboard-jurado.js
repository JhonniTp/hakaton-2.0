// User menu toggle
function toggleUserMenu() {
    const userMenu = document.getElementById('userMenu');
    userMenu.classList.toggle('hidden');
}

// Section navigation
function showSection(sectionName) {
    // Hide all sections
    const sections = ['dashboard', 'evaluations', 'projects', 'rankings', 'schedule', 'profile'];
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
    document.querySelectorAll('.nav-item-judge').forEach(link => {
        link.classList.remove('active', 'text-primary-blue');
        link.classList.add('text-gray-medium');
    });

    const activeLink = document.getElementById(`nav-${sectionName}`);
    if (activeLink) {
        activeLink.classList.add('active', 'text-primary-blue');
        activeLink.classList.remove('text-gray-medium');
    }
}

// Evaluation modal functions
function openEvaluationModal(projectName) {
    document.getElementById('evaluationTitle').textContent = `Evaluar ${projectName}`;
    document.getElementById('evaluationModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeEvaluationModal() {
    document.getElementById('evaluationModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
}

// Rating system
function setRating(criteria, rating) {
    const stars = document.querySelectorAll(`#${criteria}-stars .rating-star`);
    const scoreInput = document.getElementById(`${criteria}-score`);

    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('active');
            star.classList.remove('text-gray-300');
        } else {
            star.classList.remove('active');
            star.classList.add('text-gray-300');
        }
    });

    scoreInput.value = (rating * 2).toFixed(1);
    calculateFinalScore();
}

// Calculate final score
function calculateFinalScore() {
    const innovation = parseFloat(document.getElementById('innovation-score').value) || 0;
    const technical = parseFloat(document.getElementById('technical-score').value) || 0;
    const impact = parseFloat(document.getElementById('impact-score').value) || 0;
    const presentation = parseFloat(document.getElementById('presentation-score').value) || 0;

    const weights = {
        innovation: 0.25,
        technical: 0.30,
        impact: 0.25,
        presentation: 0.20
    };

    const finalScore = (innovation * weights.innovation) +
        (technical * weights.technical) +
        (impact * weights.impact) +
        (presentation * weights.presentation);

    document.getElementById('finalScore').textContent = finalScore.toFixed(1);
    document.getElementById('innovationWeight').textContent = (innovation * weights.innovation).toFixed(1);
    document.getElementById('technicalWeight').textContent = (technical * weights.technical).toFixed(1);
    document.getElementById('impactWeight').textContent = (impact * weights.impact).toFixed(1);
    document.getElementById('presentationWeight').textContent = (presentation * weights.presentation).toFixed(1);
}

// Save functions
function saveDraft() {
    alert('Borrador guardado exitosamente');
}

function submitEvaluation() {
    const finalScore = document.getElementById('finalScore').textContent;
    if (parseFloat(finalScore) === 0) {
        alert('Por favor, completa la evaluación antes de enviar');
        return;
    }
    alert('Evaluación enviada exitosamente');
    closeEvaluationModal();
}

function saveJudgeProfile() {
    alert('Perfil actualizado exitosamente');
}

function viewEvaluationDetails(projectName) {
    alert(`Mostrando detalles de evaluación para ${projectName}`);
}

// Close modals when clicking outside
document.addEventListener('click', function (event) {
    const modal = document.getElementById('evaluationModal');
    if (event.target === modal) {
        closeEvaluationModal();
    }
});

// Close modals with Escape key
document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        const modal = document.getElementById('evaluationModal');
        if (!modal.classList.contains('hidden')) {
            closeEvaluationModal();
        }
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

// Add event listeners for score inputs
document.addEventListener('DOMContentLoaded', function () {
    const scoreInputs = document.querySelectorAll('.score-input');
    scoreInputs.forEach(input => {
        input.addEventListener('input', calculateFinalScore);
    });
});