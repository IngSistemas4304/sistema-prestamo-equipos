package com.uniajc.sistemaprestamos.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uniajc.sistemaprestamos.entity.Usuario;
import com.uniajc.sistemaprestamos.entity.Rol;
import com.uniajc.sistemaprestamos.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarUsuario(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        usuario.setPasswordHash(
            passwordEncoder.encode(usuario.getPasswordHash())
        );

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Usuario no encontrado"
            ));
    }

    public Usuario actualizarUsuario(Integer id, String nombre,
                                     String email, Rol rol) {

        Usuario usuario = buscarPorId(id);

        usuarioRepository.findByEmail(email).ifPresent(existente -> {
            if (!existente.getIdUsuario().equals(id)) {
                throw new IllegalArgumentException(
                    "El correo ya está registrado por otro usuario");
            }
        });

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public Usuario desactivarUsuario(Integer id) {
        Usuario usuario = buscarPorId(id);

        usuario.setEstado("INACTIVO");

        return usuarioRepository.save(usuario);
    }


}
