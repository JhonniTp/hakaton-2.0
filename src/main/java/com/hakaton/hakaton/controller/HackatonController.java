package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.service.HackatonService;
import com.hakaton.hakaton.service.CategoriaService;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/hackatones")
public class HackatonController {

    @Autowired
    private HackatonService hackatonService;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private UsuarioService usuarioService;

    // Mostrar el dashboard de administrador con la lista de hackatones
    @GetMapping("/dashboard")
    public String mostrarDashboardAdmin(Model model) {
        List<HackatonModel> hackatones = hackatonService.obtenerTodosLosHackatones();
        model.addAttribute("hackatones", hackatones);
        model.addAttribute("hackaton", new HackatonModel());
        model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        model.addAttribute("juradosDisponibles", usuarioService.obtenerJuradosDisponibles());
        
        // Estadísticas para el dashboard
        model.addAttribute("totalHackatones", hackatones.size());
        model.addAttribute("hackatonesActivos", hackatonService.obtenerHackatonesPorEstado(HackatonModel.Estado.en_curso).size());
        model.addAttribute("hackatonesProximos", hackatonService.obtenerHackatonesPorEstado(HackatonModel.Estado.proximo).size());
        
        return "admin/dashboard_admin";
    }

    // Crear un nuevo hackaton
    @PostMapping("/crear")
    public String crearHackaton(@ModelAttribute HackatonModel hackaton, RedirectAttributes redirectAttributes) {
        try {
            hackatonService.crearHackaton(hackaton);
            redirectAttributes.addFlashAttribute("mensaje", "Hackaton creado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear hackaton: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/admin/hackatones/dashboard";
    }

    // Actualizar un hackaton existente
    @PostMapping("/actualizar/{id}")
    public String actualizarHackaton(@PathVariable Long id, @ModelAttribute HackatonModel hackaton, 
                                    RedirectAttributes redirectAttributes) {
        try {
            hackatonService.actualizarHackaton(id, hackaton);
            redirectAttributes.addFlashAttribute("mensaje", "Hackaton actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar hackaton: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/admin/hackatones/dashboard";
    }

    // Eliminar un hackaton
    @GetMapping("/eliminar/{id}")
    public String eliminarHackaton(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            hackatonService.eliminarHackaton(id);
            redirectAttributes.addFlashAttribute("mensaje", "Hackaton eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar hackaton: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/admin/hackatones/dashboard";
    }

    // Obtener detalles de un hackaton para edición (podría usarse con AJAX)
    @GetMapping("/detalles/{id}")
    @ResponseBody
    public HackatonModel obtenerDetallesHackaton(@PathVariable Long id) {
        return hackatonService.obtenerHackatonPorId(id)
                .orElseThrow(() -> new RuntimeException("Hackaton no encontrado"));
    }

    // Cambiar estado de un hackaton
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstadoHackaton(@PathVariable Long id, @RequestParam String estado, 
                                       RedirectAttributes redirectAttributes) {
        try {
            HackatonModel hackaton = hackatonService.obtenerHackatonPorId(id)
                    .orElseThrow(() -> new RuntimeException("Hackaton no encontrado"));
            
            hackaton.setEstado(HackatonModel.Estado.valueOf(estado));
            hackatonService.actualizarHackaton(id, hackaton);
            
            redirectAttributes.addFlashAttribute("mensaje", "Estado del hackaton actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cambiar estado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/admin/hackatones/dashboard";
    }
}