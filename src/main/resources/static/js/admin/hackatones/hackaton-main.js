/**
 * Controller Module - Coordina la lógica de negocio entre API y UI
 * Este módulo maneja todas las acciones del usuario
 */

const HackatonController = (() => {
  // Estado de la aplicación
  let hackatonesCache = [];
  let filtroActual = { estado: "all", nombre: "" };

  /**
   * Inicializa el controlador
   */
  const init = () => {
    console.log("Inicializando HackatonController...");

    // Configurar event listeners
    configurarEventListeners();

    // Cargar datos iniciales
    cargarDatosIniciales();
  };

  /**
   * Configura todos los event listeners
   */
  const configurarEventListeners = () => {
    // Búsqueda
    const searchInput = document.getElementById("hackaton-search");
    if (searchInput) {
      let timeoutId;
      searchInput.addEventListener("input", (e) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
          filtroActual.nombre = e.target.value;
          cargarHackatones();
        }, 500);
      });
    }

    // Filtro por estado
    const filterSelect = document.getElementById("hackaton-filter");
    if (filterSelect) {
      filterSelect.addEventListener("change", (e) => {
        filtroActual.estado = e.target.value;
        cargarHackatones();
      });
    }

    // Prevenir submit del formulario
    const form = document.getElementById("hackaton-form");
    if (form) {
      form.addEventListener("submit", (e) => {
        e.preventDefault();
      });
    }
  };

  /**
   * Carga datos iniciales
   */
  const cargarDatosIniciales = async () => {
    await HackatonUI.cargarCategorias();
    await cargarHackatones();
  };

  /**
   * Carga los hackatones con los filtros actuales
   */
  const cargarHackatones = async () => {
    try {
      HackatonUI.mostrarLoader();

      const hackatones = await HackatonAPI.listarTodos(filtroActual);
      hackatonesCache = hackatones;

      HackatonUI.renderizarTabla(hackatones);
      HackatonUI.actualizarContador(
        hackatones.length > 0 ? 1 : 0,
        hackatones.length,
        hackatones.length
      );
    } catch (error) {
      console.error("Error al cargar hackatones:", error);
      HackatonUI.mostrarNotificacion("Error al cargar los hackatones", "error");
    }
  };

  return {
    /**
     * Inicializa el módulo
     */
    init: init,

    /**
     * Abre el modal para crear un nuevo hackatón
     */
    nuevo: () => {
      HackatonUI.mostrarModal();
    },

    /**
     * Guarda un hackatón (crear o actualizar)
     */
    guardar: async () => {
      try {
        const id = document.getElementById("hackaton-id").value;
        const datos = HackatonUI.obtenerDatosFormulario();

        // Validación básica
        if (
          !datos.nombre ||
          !datos.idCategoria ||
          !datos.fechaInicio ||
          !datos.fechaFin
        ) {
          HackatonUI.mostrarNotificacion(
            "Por favor complete todos los campos obligatorios",
            "warning"
          );
          return;
        }

        let resultado;
        if (id) {
          // Actualizar
          resultado = await HackatonAPI.actualizar(id, datos);
          HackatonUI.mostrarNotificacion(
            "Hackatón actualizado exitosamente",
            "success"
          );
        } else {
          // Crear
          resultado = await HackatonAPI.crear(datos);
          HackatonUI.mostrarNotificacion(
            "Hackatón creado exitosamente",
            "success"
          );
        }

        HackatonUI.ocultarModal();
        await cargarHackatones();
      } catch (error) {
        console.error("Error al guardar:", error);
        HackatonUI.mostrarNotificacion(
          error.message || "Error al guardar el hackatón",
          "error"
        );
      }
    },

    /**
     * Carga un hackatón para editar
     */
    editar: async (id) => {
      try {
        const hackaton = await HackatonAPI.obtenerPorId(id);
        HackatonUI.mostrarModal(hackaton);
      } catch (error) {
        console.error("Error al cargar hackatón:", error);
        HackatonUI.mostrarNotificacion("Error al cargar el hackatón", "error");
      }
    },

    /**
     * Muestra el modal de confirmación para eliminar
     */
    confirmarEliminar: (id) => {
      HackatonUI.mostrarModalEliminar(id);
    },

    /**
     * Elimina un hackatón
     */
    eliminar: async () => {
      const modal = document.getElementById("hackaton-delete-modal");
      const id = modal.dataset.hackatonId;

      if (!id) {
        console.error("ID de hackatón no encontrado");
        return;
      }

      try {
        await HackatonAPI.eliminar(id);
        HackatonUI.mostrarNotificacion(
          "Hackatón eliminado exitosamente",
          "success"
        );
        HackatonUI.ocultarModalEliminar();
        await cargarHackatones();
      } catch (error) {
        console.error("Error al eliminar:", error);
        HackatonUI.mostrarNotificacion(
          error.message || "Error al eliminar el hackatón",
          "error"
        );
      }
    },

    /**
     * Ver detalles de un hackatón
     */
    verDetalle: async (id) => {
      try {
        const hackaton = await HackatonAPI.obtenerPorId(id);

        // Mostrar detalles en un modal o alert
        const mensaje = `
                    Hackatón: ${hackaton.nombre}
                    Categoría: ${hackaton.nombreCategoria}
                    Estado: ${hackaton.estado}
                    Fechas: ${new Date(
                      hackaton.fechaInicio
                    ).toLocaleDateString()} - ${new Date(
          hackaton.fechaFin
        ).toLocaleDateString()}
                    Participantes: ${hackaton.totalInscritos} / ${
          hackaton.maximoParticipantes
        }
                    Tamaño de equipo: ${hackaton.grupoCantidadParticipantes}
                    ${
                      hackaton.descripcion
                        ? "\nDescripción: " + hackaton.descripcion
                        : ""
                    }
                `;

        alert(mensaje);
        // TODO: Crear un modal de detalles más bonito
      } catch (error) {
        console.error("Error al ver detalles:", error);
        HackatonUI.mostrarNotificacion("Error al cargar los detalles", "error");
      }
    },

    /**
     * Cambia el estado de un hackatón
     */
    cambiarEstado: async (id, nuevoEstado) => {
      try {
        await HackatonAPI.cambiarEstado(id, nuevoEstado);
        HackatonUI.mostrarNotificacion(
          "Estado actualizado exitosamente",
          "success"
        );
        await cargarHackatones();
      } catch (error) {
        console.error("Error al cambiar estado:", error);
        HackatonUI.mostrarNotificacion("Error al cambiar el estado", "error");
      }
    },

    /**
     * Refresca la lista de hackatones
     */
    refrescar: async () => {
      await cargarHackatones();
    },
  };
})();

// ==========================================
// GESTIÓN DE CATEGORÍAS
// ==========================================

/**
 * Controlador para gestión de categorías
 */
const CategoriaController = {
  /**
   * Inicializa los event listeners para el modal de categorías
   */
  init: () => {
    const input = document.getElementById("categoria-name");
    if (input) {
      // Agregar soporte para Enter
      input.addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
          e.preventDefault();
          CategoriaController.guardar();
        }
      });
    }

    // Cerrar con ESC
    document.addEventListener("keydown", (e) => {
      const modal = document.getElementById("categoria-modal");
      if (e.key === "Escape" && modal && !modal.classList.contains("hidden")) {
        CategoriaController.cerrar();
      }
    });
  },

  /**
   * Abre el modal para crear una nueva categoría
   */
  nuevo: () => {
    const modal = document.getElementById("categoria-modal");
    const input = document.getElementById("categoria-name");

    if (modal && input) {
      input.value = "";
      modal.classList.remove("hidden");
      input.focus();
    }
  },

  /**
   * Cierra el modal de categoría
   */
  cerrar: () => {
    const modal = document.getElementById("categoria-modal");
    const input = document.getElementById("categoria-name");

    if (modal) {
      modal.classList.add("hidden");
      if (input) input.value = "";
    }
  },

  /**
   * Guarda una nueva categoría
   */
  guardar: async () => {
    const input = document.getElementById("categoria-name");
    const nombreCategoria = input?.value?.trim();

    if (!nombreCategoria) {
      HackatonUI.mostrarNotificacion(
        "Por favor ingresa un nombre para la categoría",
        "warning"
      );
      return;
    }

    try {
      const resultado = await HackatonAPI.crearCategoria({
        nombreCategoria: nombreCategoria,
      });

      HackatonUI.mostrarNotificacion(
        "Categoría creada exitosamente",
        "success"
      );

      // Cerrar modal
      CategoriaController.cerrar();

      // Recargar categorías en el select
      await HackatonUI.cargarCategorias();

      // Seleccionar automáticamente la categoría recién creada
      const select = document.getElementById("hackaton-category");
      if (select && resultado.categoria) {
        select.value = resultado.categoria.idCategoria;
      }
    } catch (error) {
      console.error("Error al crear categoría:", error);
      HackatonUI.mostrarNotificacion(
        error.message || "Error al crear la categoría",
        "error"
      );
    }
  },
};

// Funciones globales para los event handlers en HTML
window.openHackatonModal = () => HackatonController.nuevo();
window.closeHackatonModal = () => HackatonUI.ocultarModal();
window.saveHackaton = () => HackatonController.guardar();
window.closeDeleteModal = () => HackatonUI.ocultarModalEliminar();
window.confirmDeleteHackaton = () => HackatonController.eliminar();

// Funciones globales para gestión de categorías
window.openCategoriaModal = () => CategoriaController.nuevo();
window.closeCategoriaModal = () => CategoriaController.cerrar();
window.saveCategoria = () => CategoriaController.guardar();

// Inicializar cuando el DOM esté listo
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => {
    HackatonController.init();
    CategoriaController.init();
  });
} else {
  HackatonController.init();
  CategoriaController.init();
}

// Exportar para uso global
window.HackatonController = HackatonController;
window.CategoriaController = CategoriaController;
