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
                console.log('Respuesta cruda del servidor:', response);
                if (!response.ok) {
                    response.text().then(text => {
                        console.error('Cuerpo de la respuesta de error:', text);
                        showNotification(`Error del servidor: ${response.status}`, 'error');
                    });
                    return Promise.reject(new Error(`Error del servidor: ${response.status}`));
                }
                return response.json();
            })
            .then(data => {
                console.log('Datos JSON recibidos del backend:', data);
                if (data.redirectUrl) {
                    showNotification('¡Bienvenido! Redirigiendo...', 'success');
                    window.location.href = data.redirectUrl;
                } else {
                    throw new Error('La respuesta del servidor no contenía una URL de redirección.');
                }
            })
            .catch((error) => {
                console.error('Error final en el proceso de login:', error);
                if (!document.querySelector('.notification')) {
                   showNotification('No se pudo completar el inicio de sesión.', 'error');
                }
            });
        })
        .catch((error) => {
            console.error("Error durante el popup de inicio de sesión con Google:", error);
            showNotification('Error al iniciar sesión con Google.', 'error');
        });
}

