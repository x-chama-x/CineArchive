package edu.utn.inspt.cinearchive.backend.servicio;

import edu.utn.inspt.cinearchive.backend.modelo.Usuario;
import edu.utn.inspt.cinearchive.backend.modelo.Usuario.Rol;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementación del servicio de autorización.
 * Centraliza toda la lógica de permisos siguiendo principios SOLID.
 *
 * - SRP: Solo se encarga de la autorización
 * - OCP: Para agregar un nuevo rol, solo se modifica este archivo
 * - DIP: Los controladores dependen de la interfaz, no de la implementación
 *
 * @author CineArchive Team
 * @version 1.0
 */
@Service
public class AutorizacionServiceImpl implements AutorizacionService {

    // Roles que pueden VER usuarios (solo lectura)
    private static final Set<Rol> ROLES_VER_USUARIOS = EnumSet.of(
            Rol.ADMINISTRADOR,
            Rol.CHUSMA
    );

    // Roles que pueden MODIFICAR usuarios (crear, editar, eliminar)
    private static final Set<Rol> ROLES_MODIFICAR_USUARIOS = EnumSet.of(
            Rol.ADMINISTRADOR
    );

    // Roles que pueden gestionar inventario
    private static final Set<Rol> ROLES_GESTIONAR_INVENTARIO = EnumSet.of(
            Rol.ADMINISTRADOR,
            Rol.GESTOR_INVENTARIO
    );

    // Roles que pueden ver reportes
    private static final Set<Rol> ROLES_VER_REPORTES = EnumSet.of(
            Rol.ADMINISTRADOR,
            Rol.ANALISTA_DATOS
    );

    // Mapa de redirecciones por rol (Strategy Pattern simplificado)
    private static final Map<Rol, String> REDIRECCIONES_POR_ROL = new EnumMap<>(Rol.class);

    static {
        REDIRECCIONES_POR_ROL.put(Rol.ADMINISTRADOR, "redirect:/admin/usuarios");
        REDIRECCIONES_POR_ROL.put(Rol.GESTOR_INVENTARIO, "redirect:/inventario/panel");
        REDIRECCIONES_POR_ROL.put(Rol.ANALISTA_DATOS, "redirect:/reportes/panel");
        REDIRECCIONES_POR_ROL.put(Rol.CHUSMA, "redirect:/admin/usuarios");
        REDIRECCIONES_POR_ROL.put(Rol.USUARIO_REGULAR, "redirect:/catalogo");
    }

    @Override
    public boolean puedeVerUsuarios(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }
        return ROLES_VER_USUARIOS.contains(usuario.getRol());
    }

    @Override
    public boolean puedeModificarUsuarios(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }
        return ROLES_MODIFICAR_USUARIOS.contains(usuario.getRol());
    }

    @Override
    public boolean puedeGestionarInventario(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }
        return ROLES_GESTIONAR_INVENTARIO.contains(usuario.getRol());
    }

    @Override
    public boolean puedeVerReportes(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }
        return ROLES_VER_REPORTES.contains(usuario.getRol());
    }

    @Override
    public String obtenerRedireccionPorRol(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return "redirect:/login";
        }
        return REDIRECCIONES_POR_ROL.getOrDefault(usuario.getRol(), "redirect:/catalogo");
    }
}

