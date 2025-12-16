package edu.utn.inspt.cinearchive.backend.servicio;

import edu.utn.inspt.cinearchive.backend.modelo.Usuario;

/**
 * Servicio de autorización centralizado para gestionar permisos de usuarios.
 * Sigue el principio SRP separando la lógica de autorización de los controladores.
 * Sigue el principio OCP permitiendo agregar nuevos permisos sin modificar código existente.
 *
 * @author CineArchive Team
 * @version 1.0
 */
public interface AutorizacionService {

    /**
     * Verifica si el usuario tiene permiso para ver la lista de usuarios del sistema.
     * Roles permitidos: ADMINISTRADOR, CHUSMA
     *
     * @param usuario El usuario a verificar
     * @return true si puede ver usuarios, false en caso contrario
     */
    boolean puedeVerUsuarios(Usuario usuario);

    /**
     * Verifica si el usuario tiene permiso para modificar usuarios (crear, editar, eliminar).
     * Roles permitidos: ADMINISTRADOR
     *
     * @param usuario El usuario a verificar
     * @return true si puede modificar usuarios, false en caso contrario
     */
    boolean puedeModificarUsuarios(Usuario usuario);

    /**
     * Verifica si el usuario tiene permiso para gestionar el inventario.
     * Roles permitidos: ADMINISTRADOR, GESTOR_INVENTARIO
     *
     * @param usuario El usuario a verificar
     * @return true si puede gestionar inventario, false en caso contrario
     */
    boolean puedeGestionarInventario(Usuario usuario);

    /**
     * Verifica si el usuario tiene permiso para ver reportes y análisis.
     * Roles permitidos: ADMINISTRADOR, ANALISTA_DATOS
     *
     * @param usuario El usuario a verificar
     * @return true si puede ver reportes, false en caso contrario
     */
    boolean puedeVerReportes(Usuario usuario);

    /**
     * Obtiene la URL de redirección según el rol del usuario.
     *
     * @param usuario El usuario logueado
     * @return URL de redirección correspondiente al rol
     */
    String obtenerRedireccionPorRol(Usuario usuario);
}

