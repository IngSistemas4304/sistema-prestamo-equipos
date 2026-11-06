package com.uniajc.sistemaprestamos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniajc.sistemaprestamos.entity.Rol;
import com.uniajc.sistemaprestamos.entity.Usuario;
import com.uniajc.sistemaprestamos.repository.RolRepository;
import com.uniajc.sistemaprestamos.service.UsuarioService;

@Controller
public class UsuarioWebController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public UsuarioWebController(UsuarioService usuarioService,
                                RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("roles", rolRepository.findAll());
        return "usuarios";
    }

    @PostMapping("/usuarios/registrar")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Integer idRol,
            RedirectAttributes redirectAttributes) {

        try {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() ->
                        new IllegalArgumentException("Rol no encontrado"));

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPasswordHash(password);
            usuario.setRol(rol);

            usuarioService.registrarUsuario(usuario);

            redirectAttributes.addFlashAttribute(
                "mensaje", "Usuario registrado correctamente");

            return "redirect:/usuarios/nuevo";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                "error", e.getMessage());

            return "redirect:/usuarios/nuevo";
        }
    }

    @GetMapping("/usuarios")
    public String mostrarListadoUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "lista-usuarios";
    }

    @GetMapping("/usuarios/editar")
    public String mostrarFormularioEdicion() {
        return "editar-usuario";
    }

    @GetMapping("/menu-usuario")
    public String mostrarMenuUsuario() {
        return "menu-usuario";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEdicion(
            @org.springframework.web.bind.annotation.PathVariable Integer id,
            Model model) {

        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles", rolRepository.findAll());

        return "editar-usuario";
    }

    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(
            @RequestParam Integer idUsuario,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam Integer idRol,
            RedirectAttributes redirectAttributes) {

        try {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() ->
                        new IllegalArgumentException("Rol no encontrado"));

            usuarioService.actualizarUsuario(
                idUsuario, nombre, email, rol);

            redirectAttributes.addFlashAttribute(
                "mensaje", "Usuario actualizado correctamente");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                "error", e.getMessage());

            return "redirect:/usuarios/editar/" + idUsuario;
        }

        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/desactivar")
    public String desactivarUsuario(
            @RequestParam Integer idUsuario,
            RedirectAttributes redirectAttributes) {

        usuarioService.desactivarUsuario(idUsuario);

        redirectAttributes.addFlashAttribute(
            "mensaje", "Usuario desactivado correctamente");

        return "redirect:/usuarios";
    }


}
