package com.uniajc.sistemaprestamos.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String mostrarInicio(HttpSession session) {

        // Comprobar si existe un usuario autenticado
        if (session.getAttribute("idUsuario") == null) {
            return "redirect:/login";
        }

        return "inicio";
    }
}