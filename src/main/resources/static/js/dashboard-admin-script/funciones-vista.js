let users = [];
let currentUserPage = 1;
const usersPerPage = 10;
let currentUserId = null;

function openUserModal(userId = null) {
    currentUserId = userId;
    const modal = document.getElementById('user-modal');
    const title = document.getElementById('user-modal-title');

    if (userId) {
        title.textContent = 'Editar Usuario';
        loadUserData(userId);
    } else {
        title.textContent = 'Nuevo Usuario';
        document.getElementById('user-form').reset();
        document.getElementById('user-id').value = '';
    }

    modal.classList.remove('hidden');
}

function closeUserModal() {
    document.getElementById('user-modal').classList.add('hidden');
}

function openDeleteUserModal(userId) {
    currentUserId = userId;
    document.getElementById('user-delete-modal').classList.remove('hidden');
}

function closeDeleteUserModal() {
    document.getElementById('user-delete-modal').classList.add('hidden');
}

function openRoleModal(userId) {
    currentUserId = userId;
    document.getElementById('user-role-modal').classList.remove('hidden');
}

function closeRoleModal() {
    document.getElementById('user-role-modal').classList.add('hidden');
}

function loadUserData(userId) {
    fetch(`/admin/usuarios/detalles/${userId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('user-id').value = data.idUsuario;
            document.getElementById('user-name').value = data.nombre;
            document.getElementById('user-lastname').value = data.apellido;
            document.getElementById('user-email').value = data.correoElectronico;
            document.getElementById('user-phone').value = data.telefono || '';
            document.getElementById('user-dni').value = data.documentoDni || '';
            document.getElementById('user-role').value = data.rol.toLowerCase();
            document.getElementById('user-experience').value = data.perfilExperiencia || '';
            document.getElementById('user-google-id').value = data.googleId || '';
            document.getElementById('user-qr').value = data.urlCodigoQr || '';
        })
        .catch(error => {
            console.error('Error al cargar los datos del usuario:', error);
            alert('Error al cargar los datos del usuario');
        });
}

function saveUser() {
    const formData = {
        nombre: document.getElementById('user-name').value,
        apellido: document.getElementById('user-lastname').value,
        correoElectronico: document.getElementById('user-email').value,
        telefono: document.getElementById('user-phone').value,
        documentoDni: document.getElementById('user-dni').value,
        rol: document.getElementById('user-role').value,
        perfilExperiencia: document.getElementById('user-experience').value,
        googleId: document.getElementById('user-google-id').value,
        urlCodigoQr: document.getElementById('user-qr').value
    };

    const userId = document.getElementById('user-id').value;
    const url = userId ? `/admin/usuarios/actualizar/${userId}` : '/admin/usuarios/crear';
    const method = userId ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (response.ok) {
                closeUserModal();
                loadUsers();
                showNotification('Usuario guardado exitosamente', 'success');
            } else {
                throw new Error('Error al guardar el usuario');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Error al guardar el usuario', 'error');
        });
}

// ! Por implementar Eliminar usuario
// function confirmDeleteUser() {
//     fetch(`/admin/usuarios/eliminar/${currentUserId}`, {
//         method: 'DELETE'
//     })
//         .then(response => {
//             if (response.ok) {
//                 closeDeleteUserModal();
//                 loadUsers();
//                 showNotification('Usuario eliminado exitosamente', 'success');
//             } else {
//                 throw new Error('Error al eliminar el usuario');
//             }
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             showNotification('Error al eliminar el usuario', 'error');
//         });
// }

// ! Por implementar Cambiar rol de usuario
// function confirmChangeRole() {
//     const newRole = document.getElementById('new-user-role').value;

//     fetch(`/admin/usuarios/cambiar-rol/${currentUserId}`, {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json',
//         },
//         body: JSON.stringify({ rol: newRole })
//     })
//         .then(response => {
//             if (response.ok) {
//                 closeRoleModal();
//                 loadUsers(); // Recargar la lista de usuarios
//                 showNotification('Rol de usuario actualizado exitosamente', 'success');
//             } else {
//                 throw new Error('Error al cambiar el rol del usuario');
//             }
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             showNotification('Error al cambiar el rol del usuario', 'error');
//         });
// }

// Cargar la lista de usuarios
function loadUsers() {
    fetch('/admin/usuarios/dashboard')
        .then(response => response.json())
        .then(data => {
            users = data.usuarios || data;
            renderUserTable();
        })
        .catch(error => {
            console.error('Error al cargar los usuarios:', error);
        });
}

// Renderizar la tabla de usuarios
function renderUserTable() {
    const tableBody = document.getElementById('user-table-body');
    const searchTerm = document.getElementById('user-search').value.toLowerCase();
    const filterRole = document.getElementById('user-filter').value;

    // Filtrar usuarios
    let filteredUsers = users.filter(user => {
        const matchesSearch = user.nombre.toLowerCase().includes(searchTerm) ||
            user.apellido.toLowerCase().includes(searchTerm) ||
            user.correoElectronico.toLowerCase().includes(searchTerm);
        const matchesFilter = filterRole === 'all' || user.rol === filterRole;
        return matchesSearch && matchesFilter;
    });

    // Paginación
    const totalUsers = filteredUsers.length;
    const totalPages = Math.ceil(totalUsers / usersPerPage);
    const startIndex = (currentUserPage - 1) * usersPerPage;
    const endIndex = Math.min(startIndex + usersPerPage, totalUsers);
    const paginatedUsers = filteredUsers.slice(startIndex, endIndex);

    // Actualizar información de paginación
    document.getElementById('user-start').textContent = startIndex + 1;
    document.getElementById('user-end').textContent = endIndex;
    document.getElementById('user-total').textContent = totalUsers;

    // Habilitar/deshabilitar botones de paginación
    document.getElementById('user-prev').disabled = currentUserPage === 1;
    document.getElementById('user-next').disabled = currentUserPage === totalPages;

    // Renderizar filas de la tabla
    tableBody.innerHTML = '';
    paginatedUsers.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
                <td class="px-4 py-3 whitespace-nowrap">
                    <div class="flex items-center">
                        <div class="h-10 w-10 flex-shrink-0 bg-primary-blue rounded-full flex items-center justify-center">
                            <span class="text-white font-medium">${user.nombre.charAt(0)}${user.apellido.charAt(0)}</span>
                        </div>
                        <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900">${user.nombre} ${user.apellido}</div>
                            <div class="text-sm text-gray-500">${user.correoElectronico}</div>
                        </div>
                    </div>
                </td>
                <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                    <div>${user.telefono || 'N/A'}</div>
                    <div>${user.documentoDni || 'N/A'}</div>
                </td>
                <td class="px-4 py-3 whitespace-nowrap">
                    <span class="px-2 py-1 text-xs font-medium rounded-full 
                        ${user.rol === 'administrador' ? 'bg-purple-100 text-purple-800' :
                user.rol === 'jurado' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-green-100 text-green-800'}">
                        ${user.rol}
                    </span>
                </td>
                <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-500">
                    <span class="px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">
                        Activo
                    </span>
                </td>
                <td class="px-4 py-3 whitespace-nowrap text-sm font-medium">
                    <button onclick="openUserModal(${user.idUsuario})" class="text-primary-blue hover:text-blue-700 mr-2">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="openRoleModal(${user.idUsuario})" class="text-yellow-600 hover:text-yellow-800 mr-2">
                        <i class="fas fa-user-tag"></i>
                    </button>
                    <button onclick="openDeleteUserModal(${user.idUsuario})" class="text-red-600 hover:text-red-800">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
        tableBody.appendChild(row);
    });
}

function showNotification(message, type) {
    alert(`${type.toUpperCase()}: ${message}`);
}

// Event listeners
document.addEventListener('DOMContentLoaded', function () {
    loadUsers();
    document.getElementById('user-search').addEventListener('input', renderUserTable);
    document.getElementById('user-filter').addEventListener('change', renderUserTable);
    document.getElementById('user-prev').addEventListener('click', () => {
        if (currentUserPage > 1) {
            currentUserPage--;
            renderUserTable();
        }
    });

    document.getElementById('user-next').addEventListener('click', () => {
        const totalPages = Math.ceil(users.length / usersPerPage);
        if (currentUserPage < totalPages) {
            currentUserPage++;
            renderUserTable();
        }
    });
});