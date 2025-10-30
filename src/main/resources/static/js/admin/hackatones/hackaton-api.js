/**
 * API Module - Maneja todas las llamadas HTTP al backend
 * Este módulo encapsula las operaciones CRUD de hackatones
 */

const HackatonAPI = (() => {
  const BASE_URL = "/admin/hackatones";

  /**
   * Manejo genérico de errores de fetch
   */
  const handleFetchError = async (response) => {
    if (!response.ok) {
      const error = await response
        .json()
        .catch(() => ({ message: "Error desconocido" }));

      console.log("🔍 Error del servidor:", error);

      // Si hay errores de validación específicos
      if (error.errors) {
        const erroresFormateados = Object.entries(error.errors)
          .map(([campo, mensaje]) => `${campo}: ${mensaje}`)
          .join("\n");
        throw new Error(`Errores de validación:\n${erroresFormateados}`);
      }

      // Si hay un mensaje de error general
      if (error.message) {
        throw new Error(error.message);
      }

      // Error genérico
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  };

  return {
    /**
     * Obtiene todos los hackatones con filtros opcionales
     */
    listarTodos: async (filtros = {}) => {
      const params = new URLSearchParams();
      // Solo agregar estado si no es "all"
      if (filtros.estado && filtros.estado !== "all") {
        params.append("estado", filtros.estado);
      }
      if (filtros.nombre) {
        params.append("nombre", filtros.nombre);
      }

      const url = `${BASE_URL}${
        params.toString() ? "?" + params.toString() : ""
      }`;

      try {
        const response = await fetch(url, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        });
        return await handleFetchError(response);
      } catch (error) {
        console.error("Error al listar hackatones:", error);
        throw error;
      }
    },

    /**
     * Obtiene un hackatón por ID
     */
    obtenerPorId: async (id) => {
      try {
        const response = await fetch(`${BASE_URL}/${id}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        });
        return await handleFetchError(response);
      } catch (error) {
        console.error(`Error al obtener hackatón ${id}:`, error);
        throw error;
      }
    },

    /**
     * Crea un nuevo hackatón
     */
    crear: async (hackatonData) => {
      try {
        console.log("📤 Enviando petición POST a:", BASE_URL);
        console.log(
          "📦 Datos del hackaton:",
          JSON.stringify(hackatonData, null, 2)
        );

        const response = await fetch(BASE_URL, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(hackatonData),
        });

        console.log(
          "📨 Respuesta del servidor:",
          response.status,
          response.statusText
        );

        const data = await handleFetchError(response);
        console.log("✅ Datos parseados:", data);
        return data;
      } catch (error) {
        console.error("❌ Error al crear hackatón:", error);
        throw error;
      }
    },

    /**
     * Actualiza un hackatón existente
     */
    actualizar: async (id, hackatonData) => {
      try {
        const response = await fetch(`${BASE_URL}/${id}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(hackatonData),
        });
        return await handleFetchError(response);
      } catch (error) {
        console.error(`Error al actualizar hackatón ${id}:`, error);
        throw error;
      }
    },

    /**
     * Elimina un hackatón
     */
    eliminar: async (id) => {
      try {
        const response = await fetch(`${BASE_URL}/${id}`, {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
        });
        return await handleFetchError(response);
      } catch (error) {
        console.error(`Error al eliminar hackatón ${id}:`, error);
        throw error;
      }
    },

    /**
     * Cambia el estado de un hackatón
     */
    cambiarEstado: async (id, nuevoEstado) => {
      try {
        const response = await fetch(
          `${BASE_URL}/${id}/estado?estado=${nuevoEstado}`,
          {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json",
            },
          }
        );
        return await handleFetchError(response);
      } catch (error) {
        console.error(`Error al cambiar estado del hackatón ${id}:`, error);
        throw error;
      }
    },

    /**
     * Obtiene estadísticas de hackatones
     */
    obtenerEstadisticas: async () => {
      try {
        const response = await fetch(`${BASE_URL}/estadisticas`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        });
        return await handleFetchError(response);
      } catch (error) {
        console.error("Error al obtener estadísticas:", error);
        throw error;
      }
    },

    /**
     * Obtiene todas las categorías
     */
    listarCategorias: async () => {
      try {
        const response = await fetch(`${BASE_URL}/categorias`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        });
        return await handleFetchError(response);
      } catch (error) {
        console.error("Error al listar categorías:", error);
        throw error;
      }
    },

    /**
     * Crea una nueva categoría
     */
    crearCategoria: async (categoriaData) => {
      try {
        const response = await fetch(`${BASE_URL}/categorias`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(categoriaData),
        });
        return await handleFetchError(response);
      } catch (error) {
        console.error("Error al crear categoría:", error);
        throw error;
      }
    },
  };
})();

// Exportar para uso global
window.HackatonAPI = HackatonAPI;
