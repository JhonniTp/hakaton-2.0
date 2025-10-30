function showLoginForm() {
  document.getElementById("loginForm").classList.remove("hidden");
  document.getElementById("registerForm").classList.add("hidden");
  document.getElementById("forgotPasswordForm").classList.add("hidden");
}

function showRegisterForm() {
  document.getElementById("loginForm").classList.add("hidden");
  document.getElementById("registerForm").classList.remove("hidden");
  document.getElementById("forgotPasswordForm").classList.add("hidden");
}

function showForgotPassword() {
  document.getElementById("loginForm").classList.add("hidden");
  document.getElementById("registerForm").classList.add("hidden");
  document.getElementById("forgotPasswordForm").classList.remove("hidden");
}

function togglePassword(inputId) {
  const input = document.getElementById(inputId);
  const type = input.getAttribute("type") === "password" ? "text" : "password";
  input.setAttribute("type", type);
}

function checkPasswordStrength(password) {
  const strengthDiv = document.getElementById("passwordStrength");
  const strength1 = document.getElementById("strength1");
  const strength2 = document.getElementById("strength2");
  const strength3 = document.getElementById("strength3");
  const strengthText = document.getElementById("strengthText");

  if (password.length === 0) {
    strengthDiv.classList.add("hidden");
    return;
  }

  strengthDiv.classList.remove("hidden");

  let score = 0;
  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score++;
  if (/\d/.test(password) && /[!@#$%^&*]/.test(password)) score++;

  [strength1, strength2, strength3].forEach((bar) => {
    bar.className = "h-full rounded-full transition-all";
  });

  if (score >= 1) {
    strength1.classList.add("bg-red-500");
    strengthText.textContent = "Débil";
    strengthText.className = "text-xs mt-1 text-red-600";
  }
  if (score >= 2) {
    strength2.classList.add("bg-yellow-500");
    strengthText.textContent = "Media";
    strengthText.className = "text-xs mt-1 text-yellow-600";
  }
  if (score >= 3) {
    strength1.classList.remove("bg-red-500");
    strength1.classList.add("bg-green-500");
    strength2.classList.remove("bg-yellow-500");
    strength2.classList.add("bg-green-500");
    strength3.classList.add("bg-green-500");
    strengthText.textContent = "Fuerte";
    strengthText.className = "text-xs mt-1 text-green-600";
  }
}

/**
 * Maneja el registro de un nuevo usuario
 */
async function handleRegister(event) {
  event.preventDefault();

  const form = event.target;
  const submitButton = form.querySelector('button[type="submit"]');
  const originalButtonText = submitButton.innerHTML;

  // Obtener valores del formulario
  const formData = {
    nombre: form.querySelector("#firstName").value.trim(),
    apellido: form.querySelector("#lastName").value.trim(),
    correoElectronico: form.querySelector("#registerEmail").value.trim(),
    password: form.querySelector("#registerPassword").value,
    confirmPassword: form.querySelector("#confirmPassword").value,
    documentoDni: form.querySelector("#studentNumber").value.trim(),
    perfilExperiencia: `${form.querySelector("#faculty").value} - ${form
      .querySelector("#career")
      .value.trim()}`,
  };

  // Validaciones del lado del cliente
  if (
    !formData.nombre ||
    !formData.apellido ||
    !formData.correoElectronico ||
    !formData.password ||
    !formData.confirmPassword
  ) {
    showNotification(
      "Por favor, completa todos los campos obligatorios",
      "error"
    );
    return;
  }

  if (formData.password !== formData.confirmPassword) {
    showNotification("Las contraseñas no coinciden", "error");
    return;
  }

  if (formData.password.length < 8) {
    showNotification("La contraseña debe tener al menos 8 caracteres", "error");
    return;
  }

  if (!form.querySelector("#terms").checked) {
    showNotification("Debes aceptar los términos y condiciones", "error");
    return;
  }

  // Deshabilitar botón y mostrar loading
  submitButton.disabled = true;
  submitButton.innerHTML = `
        <svg class="animate-spin h-5 w-5 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
    `;

  try {
    const response = await fetch("/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    });

    const data = await response.json();

    if (data.success) {
      showNotification("¡Registro exitoso! Redirigiendo...", "success");

      // Esperar 1.5 segundos antes de redirigir
      setTimeout(() => {
        window.location.href = data.redirectUrl;
      }, 1500);
    } else {
      // Mostrar errores
      if (data.errors) {
        const errorMessages = Object.values(data.errors).join("\n");
        showNotification(errorMessages, "error");
      } else if (data.message) {
        showNotification(data.message, "error");
      } else {
        showNotification("Error al registrar el usuario", "error");
      }

      // Restaurar botón
      submitButton.disabled = false;
      submitButton.innerHTML = originalButtonText;
    }
  } catch (error) {
    console.error("Error en el registro:", error);
    showNotification(
      "Error de conexión. Por favor, intenta nuevamente.",
      "error"
    );

    // Restaurar botón
    submitButton.disabled = false;
    submitButton.innerHTML = originalButtonText;
  }
}

/**
 * Verifica en tiempo real si el correo ya está registrado
 */
async function checkEmailAvailability(email) {
  if (!email || email.length < 5) return;

  try {
    const response = await fetch(
      `/auth/check-email?email=${encodeURIComponent(email)}`
    );
    const data = await response.json();

    const emailInput = document.querySelector("#registerEmail");
    const emailContainer = emailInput.parentElement.parentElement;

    // Remover mensajes previos
    const existingMessage = emailContainer.querySelector(
      ".email-availability-message"
    );
    if (existingMessage) {
      existingMessage.remove();
    }

    if (data.existe) {
      const message = document.createElement("p");
      message.className =
        "email-availability-message text-xs text-red-600 mt-1";
      message.textContent = "Este correo ya está registrado";
      emailContainer.appendChild(message);
      emailInput.classList.add("border-red-500");
    } else {
      emailInput.classList.remove("border-red-500");
      emailInput.classList.add("border-green-500");
    }
  } catch (error) {
    console.error("Error al verificar el correo:", error);
  }
}

/**
 * Maneja la recuperación de contraseña
 */
async function handleForgotPassword(event) {
  event.preventDefault();

  const form = event.target;
  const email = form.querySelector("#forgotEmail").value.trim();

  if (!email) {
    showNotification("Por favor, ingresa tu correo electrónico", "error");
    return;
  }

  // Por ahora, solo mostrar mensaje (implementar envío de email después)
  showNotification(
    "Se ha enviado un enlace de recuperación a tu correo",
    "success"
  );

  setTimeout(() => {
    showLoginForm();
  }, 2000);
}

function showNotification(message, type) {
  const existingNotification = document.querySelector(".notification");
  if (existingNotification) {
    existingNotification.remove();
  }

  const notification = document.createElement("div");
  notification.className = `notification fixed top-4 right-4 z-50 p-4 rounded-lg shadow-lg max-w-sm ${
    type === "success"
      ? "bg-green-500 text-white"
      : type === "error"
      ? "bg-red-500 text-white"
      : "bg-blue-500 text-white"
  }`;

  notification.innerHTML = `
                <div class="flex items-center space-x-2">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        ${
                          type === "success"
                            ? '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>'
                            : type === "error"
                            ? '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>'
                            : '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>'
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
  alert(
    "Términos y Condiciones:\n\nEste es un sistema demo. Los términos reales serían mostrados en un modal apropiado."
  );
}

function showPrivacy() {
  alert(
    "Política de Privacidad:\n\nEste es un sistema demo. La política real sería mostrada en un modal apropiado."
  );
}

function showSupport() {
  alert(
    "Soporte Técnico:\n\nContacto: soporte@eduportal.edu\nTeléfono: +1 (555) 123-4567\nHorario: Lunes a Viernes 8:00 AM - 6:00 PM"
  );
}

document.addEventListener("DOMContentLoaded", function () {
  showLoginForm();

  // Event listener para verificar disponibilidad del correo
  const registerEmailInput = document.querySelector("#registerEmail");
  if (registerEmailInput) {
    let emailCheckTimeout;
    registerEmailInput.addEventListener("input", function (e) {
      clearTimeout(emailCheckTimeout);
      emailCheckTimeout = setTimeout(() => {
        checkEmailAvailability(e.target.value);
      }, 500); // Esperar 500ms después de que el usuario deja de escribir
    });
  }

  // Event listener para verificar fortaleza de contraseña
  const passwordInput = document.querySelector("#registerPassword");
  if (passwordInput) {
    passwordInput.addEventListener("input", function (e) {
      checkPasswordStrength(e.target.value);
    });
  }

  // Event listener para verificar que las contraseñas coincidan
  const confirmPasswordInput = document.querySelector("#confirmPassword");
  if (confirmPasswordInput) {
    confirmPasswordInput.addEventListener("input", function (e) {
      const password = document.querySelector("#registerPassword").value;
      const confirmPassword = e.target.value;

      if (confirmPassword.length > 0) {
        if (password === confirmPassword) {
          e.target.classList.remove("border-red-500");
          e.target.classList.add("border-green-500");
        } else {
          e.target.classList.remove("border-green-500");
          e.target.classList.add("border-red-500");
        }
      } else {
        e.target.classList.remove("border-red-500", "border-green-500");
      }
    });
  }
});
