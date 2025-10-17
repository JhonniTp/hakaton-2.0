import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-app.js";
import { getAuth, GoogleAuthProvider, signInWithPopup } from "https://www.gstatic.com/firebasejs/10.12.2/firebase-auth.js";

const firebaseConfig = {
    apiKey: "AIzaSyDkh6-FZiYVnbow2vJjEz10-IiE3ymYqRY",
    authDomain: "medischedule-lbvw1.firebaseapp.com",
    projectId: "medischedule-lbvw1",
    storageBucket: "medischedule-lbvw1.firebasestorage.app",
    messagingSenderId: "840046459796",
    appId: "1:840046459796:web:3177f8cf202f5853cdad81"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const provider = new GoogleAuthProvider();

window.handleGoogleLogin = function () {
    signInWithPopup(auth, provider)
        .then((result) => {
            const user = result.user;
            const userData = {
                nombre: user.displayName.split(' ')[0],
                apellido: user.displayName.split(' ').slice(1).join(' '),
                correoElectronico: user.email,
                googleId: user.uid
            };

            showNotification('Autenticación con Google exitosa. Verificando...', 'info');
            fetch('/auth/google', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error('Error en la respuesta del servidor.');
                })
                .then(data => {
                    console.log('Respuesta del backend recibida:', data);
                    console.log('Rol del usuario:', data.rol);
                    showNotification('¡Bienvenido! Redirigiendo al dashboard...', 'success');
                    let redirectUrl = '/';
                    if (data.rol === 'ADMIN') {
                        redirectUrl = '/admin/dashboard';
                    } else if (data.rol === 'PARTICIPANTE') {
                        redirectUrl = '/participante/dashboard';
                    } else if (data.rol === 'JURADO') {
                        redirectUrl = '/jurado/dashboard';
                    }
                    console.log('Redirigiendo a:', redirectUrl);
                    window.location.href = redirectUrl;
                })
                .catch((error) => {
                    console.error('Error:', error);
                    showNotification('No se pudo completar el inicio de sesión. Inténtalo de nuevo.', 'error');
                });
        })
        .catch((error) => {
            console.error("Error durante el inicio de sesión con Google:", error);
            showNotification('Error al iniciar sesión con Google.', 'error');
        });
}