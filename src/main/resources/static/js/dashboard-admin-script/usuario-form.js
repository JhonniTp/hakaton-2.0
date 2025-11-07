
let modoEdicion = false;
let usuarioIdActual = null;

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('usuarioForm');
    if (form) {
        form.addEventListener('submit', handleSubmit);
        
        setupRealTimeValidation();
        
        const urlParams = new URLSearchParams(window.location.search);
        const usuarioId = urlParams.get('id');
        if (usuarioId) {
            cargarUsuarioParaEditar(usuarioId);
        }
    }
});

function setupRealTimeValidation() {
    const correoInput = document.getElementById('correo-electronico');
    const telefonoInput = document.getElementById('telefono');
    const contrasenaInput = document.getElementById('contrasena');
    const confirmarContrasenaInput = document.getElementById('confirmar-contrasena');
    
    if (correoInput) {
        correoInput.addEventListener('blur', async function() {
            const correo = this.value.trim();
            if (correo && validarEmail(correo)) {
                await verificarCorreoDisponible(correo);
            }
        });
    }
    
    if (telefonoInput) {
        telefonoInput.addEventListener('blur', async function() {
            const telefono = this.value.trim();
            if (telefono) {
                await verificarTelefonoDisponible(telefono);
            }
        });
    }
    
    if (confirmarContrasenaInput) {
        confirmarContrasenaInput.addEventListener('input', function() {
            validarCoincidenciaContrasenas();
        });
    }
    
    if (contrasenaInput) {
        contrasenaInput.addEventListener('input', function() {
            validarCoincidenciaContrasenas();
        });
    }
}

async function handleSubmit(event) {
    event.preventDefault();
    
    limpiarErrores();
    
    if (!validarFormulario()) {
        mostrarAlerta('Por favor, corrija los errores en el formulario', 'error');
        return;
    }
    
    const formData = recopilarDatosFormulario();
    
    const submitBtn = document.getElementById('submit-btn');
    const submitText = document.getElementById('submit-text');
    const originalText = submitText.textContent;
    submitBtn.disabled = true;
    submitText.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Guardando...';
    
    try {
        let response;
        if (modoEdicion && usuarioIdActual) {
            response = await fetch(`/api/admin/usuarios/${usuarioIdActual}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
        } else {

            response = await fetch('/api/admin/usuarios', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });
        }
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            mostrarAlerta(data.mensaje, 'success');
            
            const paginaActualGuardada = typeof paginaActual !== 'undefined' ? paginaActual : 1;
            
            setTimeout(async () => {
                if (typeof cargarUsuarios === 'function') {
                    await cargarUsuarios();
                    if (typeof paginaActual !== 'undefined') {
                        paginaActual = paginaActualGuardada;
                    }
                }
                volverAListaUsuarios();
            }, 1500);
        } else {
            mostrarAlerta(data.mensaje || 'Error al guardar el usuario', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error de conexión. Por favor, intente nuevamente.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitText.textContent = originalText;
    }
}

function recopilarDatosFormulario() {
    const formData = {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        correoElectronico: document.getElementById('correo-electronico').value.trim(),
        documentoDni: document.getElementById('documento-dni').value.trim() || null,
        telefono: document.getElementById('telefono').value.trim() || null,
        rol: document.getElementById('rol').value,
        perfilExperiencia: document.getElementById('perfil-experiencia').value.trim() || null
    };
    
    const contrasena = document.getElementById('contrasena').value;
    if (contrasena) {
        formData.contrasena = contrasena;
    }
    
    if (modoEdicion && usuarioIdActual) {
        formData.idUsuario = usuarioIdActual;
    }
    
    return formData;
}

function validarFormulario() {
    let valido = true;
    
    const nombre = document.getElementById('nombre').value.trim();
    if (!nombre) {
        mostrarError('nombre', 'El nombre es obligatorio');
        valido = false;
    } else if (nombre.length > 100) {
        mostrarError('nombre', 'El nombre no puede exceder 100 caracteres');
        valido = false;
    }
    
    const apellido = document.getElementById('apellido').value.trim();
    if (!apellido) {
        mostrarError('apellido', 'El apellido es obligatorio');
        valido = false;
    } else if (apellido.length > 100) {
        mostrarError('apellido', 'El apellido no puede exceder 100 caracteres');
        valido = false;
    }
    
    const correo = document.getElementById('correo-electronico').value.trim();
    if (!correo) {
        mostrarError('correo-electronico', 'El correo electrónico es obligatorio');
        valido = false;
    } else if (!validarEmail(correo)) {
        mostrarError('correo-electronico', 'El formato del correo no es válido');
        valido = false;
    }
    
    const rol = document.getElementById('rol').value;
    if (!rol) {
        mostrarError('rol', 'Debe seleccionar un rol');
        valido = false;
    }
    
    const contrasena = document.getElementById('contrasena').value;
    if (!modoEdicion && !contrasena) {
        mostrarError('contrasena', 'La contraseña es obligatoria');
        valido = false;
    } else if (contrasena && contrasena.length < 6) {
        mostrarError('contrasena', 'La contraseña debe tener al menos 6 caracteres');
        valido = false;
    }
    
    const confirmarContrasena = document.getElementById('confirmar-contrasena').value;
    if (contrasena && contrasena !== confirmarContrasena) {
        mostrarError('confirmar-contrasena', 'Las contraseñas no coinciden');
        valido = false;
    }
    
    return valido;
}

function validarEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

async function verificarCorreoDisponible(correo) {
    try {
        const response = await fetch(`/api/admin/usuarios/verificar-correo?correo=${encodeURIComponent(correo)}`);
        const data = await response.json();
        
        if (data.existe && (!modoEdicion || correo !== correoOriginal)) {
            mostrarError('correo-electronico', 'Este correo ya está registrado');
            return false;
        } else {
            limpiarError('correo-electronico');
            return true;
        }
    } catch (error) {
        console.error('Error al verificar correo:', error);
        return true; 
    }
}

async function verificarTelefonoDisponible(telefono) {
    try {
        const response = await fetch(`/api/admin/usuarios/verificar-telefono?telefono=${encodeURIComponent(telefono)}`);
        const data = await response.json();
        
        if (data.existe && (!modoEdicion || telefono !== telefonoOriginal)) {
            mostrarError('telefono', 'Este teléfono ya está registrado');
            return false;
        } else {
            limpiarError('telefono');
            return true;
        }
    } catch (error) {
        console.error('Error al verificar teléfono:', error);
        return true;
    }
}


function validarCoincidenciaContrasenas() {
    const contrasena = document.getElementById('contrasena').value;
    const confirmarContrasena = document.getElementById('confirmar-contrasena').value;
    
    if (confirmarContrasena && contrasena !== confirmarContrasena) {
        mostrarError('confirmar-contrasena', 'Las contraseñas no coinciden');
        return false;
    } else {
        limpiarError('confirmar-contrasena');
        return true;
    }
}

async function cargarUsuarioParaEditar(id) {
    try {
        const response = await fetch(`/api/admin/usuarios/${id}`);
        if (!response.ok) {
            throw new Error('Usuario no encontrado');
        }
        
        const usuario = await response.json();

        modoEdicion = true;
        usuarioIdActual = id;

        document.getElementById('form-title').textContent = 'Editar Usuario';
        document.getElementById('submit-text').textContent = 'Actualizar Usuario';
        
        document.getElementById('usuario-id').value = usuario.idUsuario;
        document.getElementById('nombre').value = usuario.nombre;
        document.getElementById('apellido').value = usuario.apellido;
        document.getElementById('correo-electronico').value = usuario.correoElectronico;
        document.getElementById('documento-dni').value = usuario.documentoDni || '';
        document.getElementById('telefono').value = usuario.telefono || '';
        document.getElementById('rol').value = usuario.rol;
        document.getElementById('perfil-experiencia').value = usuario.perfilExperiencia || '';
        
        window.correoOriginal = usuario.correoElectronico;
        window.telefonoOriginal = usuario.telefono;
        
        document.getElementById('contrasena').removeAttribute('required');
        document.getElementById('confirmar-contrasena').removeAttribute('required');
        document.getElementById('contrasena-required').classList.add('hidden');
        document.getElementById('confirmar-contrasena-required').classList.add('hidden');
        document.getElementById('contrasena-hint').classList.remove('hidden');
        
    } catch (error) {
        console.error('Error al cargar usuario:', error);
        mostrarAlerta('Error al cargar los datos del usuario', 'error');
        setTimeout(() => {
            volverAListaUsuarios();
        }, 2000);
    }
}

function mostrarError(campo, mensaje) {
    const input = document.getElementById(campo);
    const errorSpan = document.getElementById(`${campo}-error`);
    
    if (input) {
        input.classList.add('input-error');
        input.classList.remove('input-valid');
    }
    
    if (errorSpan) {
        errorSpan.textContent = mensaje;
        errorSpan.classList.remove('hidden');
    }
}

function limpiarError(campo) {
    const input = document.getElementById(campo);
    const errorSpan = document.getElementById(`${campo}-error`);
    
    if (input) {
        input.classList.remove('input-error');
        input.classList.add('input-valid');
    }
    
    if (errorSpan) {
        errorSpan.classList.add('hidden');
    }
}

function limpiarErrores() {
    const campos = ['nombre', 'apellido', 'correo-electronico', 'documento-dni', 
                    'telefono', 'rol', 'contrasena', 'confirmar-contrasena', 'perfil-experiencia'];
    
    campos.forEach(campo => limpiarError(campo));
}


function limpiarFormulario() {
    if (confirm('¿Está seguro de que desea limpiar el formulario?')) {
        document.getElementById('usuarioForm').reset();
        limpiarErrores();
        
        const inputs = document.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.classList.remove('input-error', 'input-valid');
        });
    }
}

function volverAListaUsuarios() {
    const formView = document.getElementById('user-form-view');
    const listView = document.getElementById('user-list-view');
    
    if (formView && listView) {
        formView.classList.add('hidden');
        listView.classList.remove('hidden');
    }
    if (typeof renderizarTablaUsuarios === 'function') {
        renderizarTablaUsuarios();
    }
}

function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(`${inputId}-icon`);
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}


function mostrarAlerta(mensaje, tipo = 'info') {
    const alertContainer = document.getElementById('alert-container');
    
    const colores = {
        success: 'bg-green-50 border-green-200 text-green-800',
        error: 'bg-red-50 border-red-200 text-red-800',
        warning: 'bg-yellow-50 border-yellow-200 text-yellow-800',
        info: 'bg-blue-50 border-blue-200 text-blue-800'
    };
    
    const iconos = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };
    
    const alert = document.createElement('div');
    alert.className = `${colores[tipo]} border rounded-lg p-4 mb-4 flex items-center shadow-lg alert-slide-in`;
    alert.innerHTML = `
        <i class="fas ${iconos[tipo]} mr-3 text-lg"></i>
        <span class="flex-1">${mensaje}</span>
        <button onclick="this.parentElement.remove()" class="ml-3 text-gray-500 hover:text-gray-700">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    alertContainer.appendChild(alert);
    setTimeout(() => {
        alert.classList.remove('alert-slide-in');
        alert.classList.add('alert-slide-out');
        setTimeout(() => alert.remove(), 300);
    }, 5000);
}


function prepararFormularioNuevo() {
    modoEdicion = false;
    usuarioIdActual = null;
    
    document.getElementById('form-title').textContent = 'Crear Nuevo Usuario';
    document.getElementById('submit-text').textContent = 'Crear Usuario';
    document.getElementById('usuarioForm').reset();
    limpiarErrores();
    
    document.getElementById('contrasena').setAttribute('required', 'required');
    document.getElementById('confirmar-contrasena').setAttribute('required', 'required');
    document.getElementById('contrasena-required').classList.remove('hidden');
    document.getElementById('confirmar-contrasena-required').classList.remove('hidden');
    document.getElementById('contrasena-hint').classList.add('hidden');
}
