/**
 * UI Module - Maneja toda la interacción y actualización del DOM
 * Este módulo encapsula las operaciones de interfaz de usuario
 */

const HackatonUI = (() => {
  // Referencias a elementos del DOM
  const elements = {
    modal: null,
    modalTitle: null,
    form: null,
    tableBody: null,
    searchInput: null,
    filterSelect: null,
    deleteModal: null,

    // Inicialización lazy de elementos
    get: function (key) {
      if (!this[key]) {
        switch (key) {
          case "modal":
            this[key] = document.getElementById("hackaton-modal");
            break;
          case "modalTitle":
            this[key] = document.getElementById("hackaton-modal-title");
            break;
          case "form":
            this[key] = document.getElementById("hackaton-form");
            break;
          case "tableBody":
            this[key] = document.getElementById("hackaton-table-body");
            break;
          case "searchInput":
            this[key] = document.getElementById("hackaton-search");
            break;
          case "filterSelect":
            this[key] = document.getElementById("hackaton-filter");
            break;
          case "deleteModal":
            this[key] = document.getElementById("hackaton-delete-modal");
            break;
        }
      }
      return this[key];
    },
  };

  /**
   * Formatea una fecha para mostrar en la UI
   */
  const formatearFecha = (fechaString) => {
    const fecha = new Date(fechaString);
    const opciones = {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    };
    return fecha.toLocaleDateString("es-ES", opciones);
  };

  /**
   * Obtiene el badge HTML según el estado
   */
  const obtenerBadgeEstado = (estado) => {
    const badges = {
      PROXIMO:
        '<span class="bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-sm font-medium">Próximo</span>',
      EN_CURSO:
        '<span class="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-medium">En Curso</span>',
      FINALIZADO:
        '<span class="bg-gray-100 text-gray-800 px-3 py-1 rounded-full text-sm font-medium">Finalizado</span>',
    };
    return badges[estado] || estado;
  };

  return {
    /**
     * Muestra el modal para crear/editar
     */
    mostrarModal: (hackaton = null) => {
      const modal = elements.get("modal");
      const modalTitle = elements.get("modalTitle");
      const form = elements.get("form");

      if (hackaton) {
        modalTitle.textContent = "Editar Hackatón";
        HackatonUI.llenarFormulario(hackaton);
      } else {
        modalTitle.textContent = "Nuevo Hackatón";
        form.reset();
        document.getElementById("hackaton-id").value = "";
      }

      modal.classList.remove("hidden");
    },

    /**
     * Oculta el modal
     */
    ocultarModal: () => {
      const modal = elements.get("modal");
      modal.classList.add("hidden");
      elements.get("form").reset();
    },

    /**
     * Muestra el modal de confirmación para eliminar
     */
    mostrarModalEliminar: (id) => {
      const modal = elements.get("deleteModal");
      modal.classList.remove("hidden");
      modal.dataset.hackatonId = id;
    },

    /**
     * Oculta el modal de eliminación
     */
    ocultarModalEliminar: () => {
      const modal = elements.get("deleteModal");
      modal.classList.add("hidden");
      delete modal.dataset.hackatonId;
    },

    /**
     * Llena el formulario con datos de un hackatón
     */
    llenarFormulario: (hackaton) => {
      document.getElementById("hackaton-id").value = hackaton.idHackaton || "";
      document.getElementById("hackaton-name").value = hackaton.nombre || "";
      document.getElementById("hackaton-category").value =
        hackaton.idCategoria || "";
      document.getElementById("hackaton-description").value =
        hackaton.descripcion || "";
      document.getElementById("hackaton-image").value = hackaton.urlImg || "";

      // Formatear fechas para datetime-local
      if (hackaton.fechaInicio) {
        const fechaInicio = new Date(hackaton.fechaInicio);
        document.getElementById("hackaton-start-date").value = fechaInicio
          .toISOString()
          .slice(0, 16);
      }
      if (hackaton.fechaFin) {
        const fechaFin = new Date(hackaton.fechaFin);
        document.getElementById("hackaton-end-date").value = fechaFin
          .toISOString()
          .slice(0, 16);
      }

      document.getElementById("hackaton-max-participants").value =
        hackaton.maximoParticipantes || "";
      document.getElementById("hackaton-team-size").value =
        hackaton.grupoCantidadParticipantes || "";
      document.getElementById("hackaton-status").value =
        hackaton.estado || "PROXIMO";
    },

    /**
     * Obtiene los datos del formulario
     */
    obtenerDatosFormulario: () => {
      const descripcion = document
        .getElementById("hackaton-description")
        .value.trim();
      const urlImg = document.getElementById("hackaton-image").value.trim();
      const idCategoria = document.getElementById("hackaton-category").value;
      const maxParticipantes = document.getElementById(
        "hackaton-max-participants"
      ).value;
      const teamSize = document.getElementById("hackaton-team-size").value;

      return {
        nombre: document.getElementById("hackaton-name").value.trim(),
        idCategoria: idCategoria ? parseInt(idCategoria) : null,
        descripcion: descripcion || null, // null si está vacío
        urlImg: urlImg || null, // null si está vacío
        fechaInicio: document.getElementById("hackaton-start-date").value,
        fechaFin: document.getElementById("hackaton-end-date").value,
        maximoParticipantes: maxParticipantes
          ? parseInt(maxParticipantes)
          : null,
        grupoCantidadParticipantes: teamSize ? parseInt(teamSize) : null,
      };
    },

    /**
     * Renderiza la tabla de hackatones
     */
    renderizarTabla: (hackatones) => {
      const tbody = elements.get("tableBody");

      if (!hackatones || hackatones.length === 0) {
        tbody.innerHTML = `
                    <tr>
                        <td colspan="6" class="px-4 py-8 text-center text-gray-500">
                            <i class="fas fa-inbox text-4xl mb-2"></i>
                            <p>No hay hackatones registrados</p>
                        </td>
                    </tr>
                `;
        return;
      }

      tbody.innerHTML = hackatones
        .map(
          (hackaton) => `
                <tr class="hover:bg-gray-50">
                    <td class="px-4 py-3">
                        <div class="font-medium text-gray-900">${
                          hackaton.nombre
                        }</div>
                        <div class="text-sm text-gray-500">${
                          hackaton.descripcion
                            ? hackaton.descripcion.substring(0, 50) + "..."
                            : "Sin descripción"
                        }</div>
                    </td>
                    <td class="px-4 py-3 text-sm text-gray-900">${
                      hackaton.nombreCategoria || "Sin categoría"
                    }</td>
                    <td class="px-4 py-3 text-sm text-gray-900">
                        <div>${formatearFecha(hackaton.fechaInicio)}</div>
                        <div class="text-gray-500">${formatearFecha(
                          hackaton.fechaFin
                        )}</div>
                    </td>
                    <td class="px-4 py-3">${obtenerBadgeEstado(
                      hackaton.estado
                    )}</td>
                    <td class="px-4 py-3 text-sm text-gray-900">
                        ${hackaton.totalInscritos || 0} / ${
            hackaton.maximoParticipantes
          }
                    </td>
                    <td class="px-4 py-3 text-sm font-medium">
                        <button onclick="HackatonController.verDetalle(${
                          hackaton.idHackaton
                        })" 
                                class="text-secondary-cyan hover:text-cyan-700 mr-3">
                            <i class="fas fa-eye"></i> Ver
                        </button>
                        <button onclick="HackatonController.editar(${
                          hackaton.idHackaton
                        })" 
                                class="text-blue-600 hover:text-blue-800 mr-3">
                            <i class="fas fa-edit"></i> Editar
                        </button>
                        <button onclick="HackatonController.confirmarEliminar(${
                          hackaton.idHackaton
                        })" 
                                class="text-red-600 hover:text-red-800">
                            <i class="fas fa-trash"></i> Eliminar
                        </button>
                    </td>
                </tr>
            `
        )
        .join("");
    },

    /**
     * Carga las categorías en el select
     */
    cargarCategorias: async () => {
      try {
        const categorias = await HackatonAPI.listarCategorias();
        const select = document.getElementById("hackaton-category");

        select.innerHTML =
          '<option value="">Seleccione una categoría</option>' +
          categorias
            .map(
              (cat) =>
                `<option value="${cat.idCategoria}">${cat.nombreCategoria}</option>`
            )
            .join("");
      } catch (error) {
        HackatonUI.mostrarNotificacion("Error al cargar categorías", "error");
      }
    },

    /**
     * Muestra una notificación toast
     */
    mostrarNotificacion: (mensaje, tipo = "success") => {
      const colores = {
        success: "bg-green-500",
        error: "bg-red-500",
        warning: "bg-yellow-500",
        info: "bg-blue-500",
      };

      const iconos = {
        success: "fa-check-circle",
        error: "fa-exclamation-circle",
        warning: "fa-exclamation-triangle",
        info: "fa-info-circle",
      };

      const notificacion = document.createElement("div");
      notificacion.className = `fixed top-4 right-4 ${colores[tipo]} text-white px-6 py-4 rounded-lg shadow-lg z-50 flex items-center space-x-3 animate-slide-in`;
      notificacion.innerHTML = `
                <i class="fas ${iconos[tipo]}"></i>
                <span>${mensaje}</span>
            `;

      document.body.appendChild(notificacion);

      setTimeout(() => {
        notificacion.classList.add("animate-fade-out");
        setTimeout(() => notificacion.remove(), 300);
      }, 3000);
    },

    /**
     * Muestra un loader en la tabla
     */
    mostrarLoader: () => {
      const tbody = elements.get("tableBody");
      tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="px-4 py-8 text-center">
                        <i class="fas fa-spinner fa-spin text-4xl text-secondary-cyan"></i>
                        <p class="mt-2 text-gray-500">Cargando hackatones...</p>
                    </td>
                </tr>
            `;
    },

    /**
     * Actualiza el contador de resultados
     */
    actualizarContador: (inicio, fin, total) => {
      document.getElementById("hackaton-start").textContent = inicio;
      document.getElementById("hackaton-end").textContent = fin;
      document.getElementById("hackaton-total").textContent = total;
    },
  };
})();

// Exportar para uso global
window.HackatonUI = HackatonUI;
