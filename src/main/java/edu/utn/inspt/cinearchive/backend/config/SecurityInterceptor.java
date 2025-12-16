package edu.utn.inspt.cinearchive.backend.config;

import edu.utn.inspt.cinearchive.backend.modelo.Usuario;
import edu.utn.inspt.cinearchive.backend.servicio.AutorizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Interceptor de seguridad para proteger rutas según roles de usuario
 * Se ejecuta ANTES de que el request llegue al Controller
 *
 * Verifica:
 * - Si hay sesión activa (usuario logueado)
 * - Si el usuario tiene el rol necesario para acceder a la ruta
 *
 * Configurado en WebMvcConfig.java
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private AutorizacionService autorizacionService;

    /**
     * Se ejecuta ANTES de que el request llegue al Controller
     *
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param handler Handler del controller
     * @return true si permite continuar, false si bloquea la petición
     * @throws Exception Si hay algún error
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Remover el context path de la URI para obtener la ruta relativa
        String path = uri.substring(contextPath.length());

        // ===== AGREGAR HEADERS DE NO-CACHE A TODAS LAS PÁGINAS =====
        // Esto previene que el navegador guarde en caché las páginas protegidas
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        // ===== RUTAS PÚBLICAS (no requieren autenticación) =====
        if (esRutaPublica(path)) {
            return true; // Permitir acceso
        }

        // ===== RUTAS PROTEGIDAS (requieren autenticación) =====
        HttpSession session = request.getSession(false);
        Usuario usuario = null;

        if (session != null) {
            usuario = (Usuario) session.getAttribute("usuarioLogueado");
        }

        // Si no hay sesión o no hay usuario logueado, redirigir a login
        if (usuario == null) {
            response.sendRedirect(contextPath + "/login");
            return false; // Bloquear acceso
        }

        // ===== RESTRICCIÓN ESPECIAL PARA ROL CHUSMA =====
        // CHUSMA solo puede acceder a /admin/usuarios (listado y detalle)
        // No puede acceder a ninguna otra funcionalidad del sistema
        if (usuario.getRol() == Usuario.Rol.CHUSMA) {
            // Rutas permitidas para CHUSMA
            boolean rutaPermitida = path.equals("/admin/usuarios") ||
                                    path.startsWith("/admin/usuarios/detalle/") ||
                                    path.equals("/logout") ||
                                    path.equals("/perfil") ||
                                    path.equals("/acceso-denegado");

            if (!rutaPermitida) {
                response.sendRedirect(contextPath + "/acceso-denegado");
                return false;
            }
            // Si es ruta permitida, continuar
            return true;
        }

        // ===== VERIFICAR PERMISOS POR ROL =====

        // Rutas de ADMINISTRADOR (también accesibles para CHUSMA en modo lectura)
        if (path.startsWith("/admin")) {
            // Usar AutorizacionService para verificar si puede ver usuarios
            if (!autorizacionService.puedeVerUsuarios(usuario)) {
                response.sendRedirect(contextPath + "/acceso-denegado");
                return false;
            }
        }

        // Rutas de GESTOR DE INVENTARIO
        if (path.startsWith("/inventario")) {
            if (usuario.getRol() != Usuario.Rol.GESTOR_INVENTARIO &&
                usuario.getRol() != Usuario.Rol.ADMINISTRADOR) {
                response.sendRedirect(contextPath + "/acceso-denegado");
                return false;
            }
        }

        // Rutas de ANALISTA DE DATOS
        if (path.startsWith("/reportes") || path.startsWith("/analytics")) {
            if (usuario.getRol() != Usuario.Rol.ANALISTA_DATOS &&
                usuario.getRol() != Usuario.Rol.ADMINISTRADOR) {
                response.sendRedirect(contextPath + "/acceso-denegado");
                return false;
            }
        }

        // Usuario autenticado con permisos correctos
        return true;
    }

    /**
     * Verifica si una ruta es pública (no requiere autenticación)
     *
     * @param path Ruta a verificar
     * @return true si es pública, false si requiere autenticación
     */
    private boolean esRutaPublica(String path) {
        // Rutas públicas exactas
        return path.equals("/login") ||
               path.equals("/registro") ||
               path.equals("/") ||
               path.equals("/index") ||
               path.equals("/acceso-denegado") || // Permitir acceso a la página de acceso denegado
               path.equals("/test-acceso-denegado") || // Ruta de prueba
               path.startsWith("/api/") || // Permitir acceso a los endpoints de la API
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/img/") ||
               path.startsWith("/disenio/");
    }

    /**
     * Se ejecuta DESPUÉS de que el Controller procese la petición
     * pero ANTES de renderizar la vista
     *
     * Útil para logging, agregar headers, etc.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, org.springframework.web.servlet.ModelAndView modelAndView)
            throws Exception {

        // Agregar información del usuario a todas las vistas
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
                if (usuario != null && !modelAndView.getModel().containsKey("usuarioActual")) {
                    modelAndView.addObject("usuarioActual", usuario);
                }
            }
        }
    }

    /**
     * Se ejecuta DESPUÉS de que se complete toda la petición
     * (después de renderizar la vista)
     *
     * Útil para limpieza de recursos, logging final, etc.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex)
            throws Exception {

        // Logging de acceso (opcional)
        if (ex != null) {
            // Hubo una excepción durante el procesamiento
            System.err.println("Error en request " + request.getRequestURI() + ": " + ex.getMessage());
        }
    }
}
