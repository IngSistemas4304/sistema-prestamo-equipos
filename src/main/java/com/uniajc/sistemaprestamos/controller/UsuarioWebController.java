package com.uniajc.sistemaprestamos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioWebController {

    // Mostrar formulario para registrar usuarios
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioRegistro() {
        return "usuarios";
    }

    // Mostrar listado de usuarios
    @GetMapping("/usuarios")
    public String mostrarListadoUsuarios() {
        return "lista-usuarios";
    }

    // Mostrar formulario para editar usuarios
    @GetMapping("/usuarios/editar")
    public String mostrarFormularioEdicion() {
        return "editar-usuario";
    }
    // Mostrar menú de gestión de usuarios
    @GetMapping("/menu-usuario")
    public String mostrarMenuUsuario() {
        return "menu-usuario";
    }
}
