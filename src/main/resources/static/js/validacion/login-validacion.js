function showLoginForm() {
    document.getElementById('loginForm').classList.remove('hidden');
    document.getElementById('registerForm').classList.add('hidden');
    document.getElementById('forgotPasswordForm').classList.add('hidden');
}

function showRegisterForm() {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.remove('hidden');
    document.getElementById('forgotPasswordForm').classList.add('hidden');
}

function showForgotPassword() {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.add('hidden');
    document.getElementById('forgotPasswordForm').classList.remove('hidden');
}

function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
    input.setAttribute('type', type);
}

function checkPasswordStrength(password) {
    const strengthDiv = document.getElementById('passwordStrength');
    const strength1 = document.getElementById('strength1');
    const strength2 = document.getElementById('strength2');
    const strength3 = document.getElementById('strength3');
    const strengthText = document.getElementById('strengthText');

    if (password.length === 0) {
        strengthDiv.classList.add('hidden');
        return;
    }

    strengthDiv.classList.remove('hidden');

    let score = 0;
    if (password.length >= 8) score++;
    if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score++;
    if (/\d/.test(password) && /[!@#$%^&*]/.test(password)) score++;

    [strength1, strength2, strength3].forEach(bar => {
        bar.className = 'h-full rounded-full transition-all';
    });

    if (score >= 1) {
        strength1.classList.add('bg-red-500');
        strengthText.textContent = 'Débil';
        strengthText.className = 'text-xs mt-1 text-red-600';
    }
    if (score >= 2) {
        strength2.classList.add('bg-yellow-500');
        strengthText.textContent = 'Media';
        strengthText.className = 'text-xs mt-1 text-yellow-600';
    }
    if (score >= 3) {
        strength1.classList.remove('bg-red-500');
        strength1.classList.add('bg-green-500');
        strength2.classList.remove('bg-yellow-500');
        strength2.classList.add('bg-green-500');
        strength3.classList.add('bg-green-500');
        strengthText.textContent = 'Fuerte';
        strengthText.className = 'text-xs mt-1 text-green-600';
    }
}



function showNotification(message, type) {
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    const notification = document.createElement('div');
    notification.className = `notification fixed top-4 right-4 z-50 p-4 rounded-lg shadow-lg max-w-sm ${type === 'success' ? 'bg-green-500 text-white' :
            type === 'error' ? 'bg-red-500 text-white' :
                'bg-blue-500 text-white'
        }`;

    notification.innerHTML = `
                <div class="flex items-center space-x-2">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        ${type === 'success' ?
            '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>' :
            type === 'error' ?
                '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>' :
                '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>'
        }
                    </svg>
                    <span>${message}</span>
                </div>
            `;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

function showTerms() {
    alert('Términos y Condiciones:\n\nEste es un sistema demo. Los términos reales serían mostrados en un modal apropiado.');
}

function showPrivacy() {
    alert('Política de Privacidad:\n\nEste es un sistema demo. La política real sería mostrada en un modal apropiado.');
}

function showSupport() {
    alert('Soporte Técnico:\n\nContacto: soporte@eduportal.edu\nTeléfono: +1 (555) 123-4567\nHorario: Lunes a Viernes 8:00 AM - 6:00 PM');
}

document.addEventListener('DOMContentLoaded', function () {
    showLoginForm();
});