function showLoginForm() {
    document.getElementById('loginForm').classList.remove('hidden');
    document.getElementById('registerForm').classList.add('hidden');
    const fp = document.getElementById('forgotPasswordForm');
    if (fp) fp.classList.add('hidden');
    clearRegistrationMessages();
}

function showRegisterForm() {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.remove('hidden');
    const fp = document.getElementById('forgotPasswordForm');
    if (fp) fp.classList.add('hidden');
    clearLoginError();
}

function showForgotPassword() {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.add('hidden');
    const fp = document.getElementById('forgotPasswordForm');
    if (fp) fp.classList.remove('hidden');
    clearRegistrationMessages();
    clearLoginError();
}

function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
    input.setAttribute('type', type);

    const button = input.nextElementSibling; 
     if (button && button.querySelector('svg')) {

     }
}

function checkPasswordStrength(password) {
    const strengthDiv = document.getElementById('passwordStrength');
    const strength1 = document.getElementById('strength1');
    const strength2 = document.getElementById('strength2');
    const strength3 = document.getElementById('strength3');
    const strengthText = document.getElementById('strengthText');

    if (!strengthDiv || !strength1 || !strength2 || !strength3 || !strengthText) return;

    if (password.length === 0) {
        strengthDiv.classList.add('hidden');
        return;
    }

    strengthDiv.classList.remove('hidden');

    let score = 0;
    if (password.length >= 8) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/[a-z]/.test(password)) score++;
    if (/["\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) score++;
    let strengthLevel = 0;
    if (score <= 1) strengthLevel = 1;
    else if (score === 2) strengthLevel = 2;
    else if (score === 3) strengthLevel = 3;
    else if (score >= 4) strengthLevel = 3;
    strength1.className = 'h-full rounded-full transition-all';
    strength2.className = 'h-full rounded-full transition-all';
    strength3.className = 'h-full rounded-full transition-all';
    if (strengthLevel >= 1) {
        strength1.classList.add('bg-red-500');
        strengthText.textContent = 'Débil';
        strengthText.className = 'text-xs mt-1 text-red-600';
    }
    if (strengthLevel >= 2) {
        strength1.classList.remove('bg-red-500');
        strength1.classList.add('bg-yellow-500');
        strength2.classList.add('bg-yellow-500');
        strengthText.textContent = 'Media';
        strengthText.className = 'text-xs mt-1 text-yellow-600';
    }
    if (strengthLevel >= 3) {
        strength1.classList.remove('bg-yellow-500');
        strength1.classList.add('bg-green-500');
        strength2.classList.remove('bg-yellow-500');
        strength2.classList.add('bg-green-500');
        strength3.classList.add('bg-green-500');
        strengthText.textContent = 'Fuerte';
        strengthText.className = 'text-xs mt-1 text-green-600';
    }
}


function validatePasswordMatch() {
    const passwordInput = document.getElementById('registerPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const errorElement = document.getElementById('passwordMatchError');

    if (!passwordInput || !confirmPasswordInput || !errorElement) return;

    if (passwordInput.value === confirmPasswordInput.value) {
        errorElement.classList.add('hidden');
        confirmPasswordInput.classList.remove('border-red-500', 'focus:ring-red-500');
        confirmPasswordInput.classList.add('border-gray-300', 'focus:ring-academic-500');
        return true;
    } else {
        errorElement.classList.remove('hidden');
        confirmPasswordInput.classList.remove('border-gray-300', 'focus:ring-academic-500');
        confirmPasswordInput.classList.add('border-red-500', 'focus:ring-red-500');
        return false;
    }
}

function validateTelefono() {
    const telefonoInput = document.getElementById('registerTelefono');
    const telefonoError = document.getElementById('telefonoError');

    if (!telefonoInput || !telefonoError) {
        return true;
    }

    const telefonoRegex = /^\+51[0-9]{9}$/;

    if (telefonoInput.value === '' || telefonoRegex.test(telefonoInput.value)) {
        telefonoError.classList.add('hidden');
        return true;
    } else {
        telefonoError.classList.remove('hidden');
        return false;
    }
}

function validateRegistrationForm() {
    const passwordsMatch = validatePasswordMatch();
    const telefonoValido = validateTelefono();

    if (!passwordsMatch) {
        showNotification("Las contraseñas no coinciden", "error");
    }

    if (!telefonoValido) {
        showNotification("El formato del teléfono es inválido.", "error");
    }

    return passwordsMatch && telefonoValido;
}


function showNotification(message, type = 'info') {
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    const notification = document.createElement('div');
    // Colored background with white text, no rounded corners
    let iconPath = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>';
    let bgColor = 'bg-blue-500';

    if (type === 'success') {
        bgColor = 'bg-green-600';
        iconPath = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>';
    } else if (type === 'error') {
        bgColor = 'bg-red-600';
        iconPath = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>';
    } else if (type === 'warning') {
        bgColor = 'bg-yellow-500';
        iconPath = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>';
    }

    notification.className = `notification fixed top-16 right-5 z-[100] p-4 shadow max-w-sm w-full sm:w-auto ${bgColor} text-white text-sm transition-all duration-300 ease-out`;
    notification.style.transform = 'translateX(100%)';
    notification.style.opacity = '0';

    notification.innerHTML = `
        <div class="flex items-center space-x-3">
            <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                ${iconPath}
            </svg>
            <span>${message}</span>
        </div>
    `;

    document.body.appendChild(notification);
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
        notification.style.opacity = '1';
    }, 10);
    setTimeout(() => {
        if (notification.parentNode) {
            notification.style.transform = 'translateX(100%)';
            notification.style.opacity = '0';
            notification.addEventListener('transitionend', () => notification.remove());
        }
    }, 4000);
}


function showTerms() {
    alert('Términos y Condiciones:\n\n1. Aceptación de los términos...\n2. Uso del servicio...\n3. Propiedad intelectual...');
}

function showPrivacy() {
    alert('Política de Privacidad:\n\n1. Información recopilada...\n2. Uso de la información...\n3. Cookies...');
}

function showSupport() {
    alert('Soporte Técnico:\n\nContacto: soporte@eduportal.edu\nTeléfono: +1 (555) 123-4567\nHorario: Lunes a Viernes 8:00 AM - 6:00 PM');
}

function clearLoginError() {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.classList.add('hidden');
    }
}

function clearRegistrationMessages() {
    const errorDiv = document.getElementById('registration-error-message');
    const successDiv = document.getElementById('registration-success-message');
    if (errorDiv) errorDiv.style.display = 'none';
    if (successDiv) successDiv.style.display = 'none';
}


document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const hash = window.location.hash;

    const body = document.body;
    const registrationError = body.getAttribute('data-registration-error');
    const registrationSuccess = body.getAttribute('data-registration-success');

    if (urlParams.has('error')) {
        showLoginForm();
    } else if (registrationError) {
        showRegisterForm();
        showNotification(registrationError, 'error');
    } else if (registrationSuccess) {
        showLoginForm();
        showNotification(registrationSuccess, 'success');
    } else if (hash === '#register') {
        showRegisterForm();
    } else {
        showLoginForm();
    }

     const previousFormData = document.body.getAttribute('data-form-data');
     if (previousFormData) {
        try {
            const formData = JSON.parse(previousFormData);
            Object.keys(formData).forEach(key => {
                const input = document.querySelector(`#registerForm [name="${key}"]`);
                if (input && input.type !== 'password') {
                    input.value = formData[key];
                }
            });
        } catch (e) { console.error("Error al parsear formData previo:", e); }
     }
});