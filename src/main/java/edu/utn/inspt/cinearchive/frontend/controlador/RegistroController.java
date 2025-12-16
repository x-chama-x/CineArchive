package edu.utn.inspt.cinearchive.frontend.controlador;

import edu.utn.inspt.cinearchive.backend.modelo.Usuario;
import edu.utn.inspt.cinearchive.backend.servicio.AutorizacionService;
import edu.utn.inspt.cinearchive.backend.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.time.LocalDate;

/**
 * Controlador para gestionar el registro de nuevos usuarios
 * Maneja la creación de cuentas y validación de datos
 */
@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AutorizacionService autorizacionService;

    /**
     * Muestra el formulario de registro
     * GET /registro
     *
     * @param model Modelo para pasar datos a la vista
     * @param session Sesión HTTP para verificar si el usuario ya está logueado
     * @return Vista del formulario de registro (registro.jsp) o redirección si ya está logueado
     */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model, HttpSession session) {
        // Si ya hay un usuario logueado, redirigir a su página principal según su rol
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado != null) {
            // Redirigir según el rol del usuario (usando AutorizacionService - SOLID)
            return autorizacionService.obtenerRedireccionPorRol(usuarioLogueado);
        }

        // Crear un objeto Usuario vacío para el formulario
        model.addAttribute("usuario", new Usuario());
        return "registro"; // Retorna registro.jsp
    }

    /**
     * Procesa el formulario de registro - Versión con campos separados
     * POST /registro
     *
     * Este método recibe los campos del formulario de forma individual
     * y realiza todas las validaciones necesarias
     *
     * @param nombre Nombre completo del usuario
     * @param email Email único del usuario
     * @param password Contraseña ingresada
     * @param passwordConfirm Confirmación de contraseña
     * @param fechaNacimiento Fecha de nacimiento (opcional)
     * @param redirectAttributes Para pasar mensajes al redirigir
     * @param model Para pasar datos a la vista en caso de error
     * @return Redirección a login si exitoso, o vuelta a registro si hay error
     */
    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("passwordConfirm") String passwordConfirm,
            @RequestParam(value = "fechaNacimiento", required = false) String fechaNacimiento,
            RedirectAttributes redirectAttributes,
            Model model) {

        // DEBUG: Log de entrada
        System.out.println("=== PROCESANDO REGISTRO ===");
        System.out.println("Nombre: " + nombre);
        System.out.println("Email: " + email);
        System.out.println("Password length: " + (password != null ? password.length() : "null"));
        System.out.println("PasswordConfirm length: " + (passwordConfirm != null ? passwordConfirm.length() : "null"));
        System.out.println("FechaNacimiento: " + fechaNacimiento);

        // VALIDACIÓN 1: Verificar que los campos obligatorios no estén vacíos
        if (nombre == null || nombre.trim().isEmpty()) {
            model.addAttribute("error", "El nombre es obligatorio");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";
        }

        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "El email es obligatorio");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";
        }

        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "La contraseña es obligatoria");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";
        }

        if (passwordConfirm == null || passwordConfirm.trim().isEmpty()) {
            model.addAttribute("error", "Debes confirmar la contraseña");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";
        }

        // VALIDACIÓN 2: Verificar que las contraseñas coincidan
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";
        }

        // VALIDACIÓN 3: Verificar formato de email básico
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            model.addAttribute("error", "El formato del email no es válido");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";
        }

        try {
            // Crear objeto Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre.trim());
            nuevoUsuario.setEmail(email.trim().toLowerCase());
            nuevoUsuario.setContrasena(password); // El service lo encriptará
            nuevoUsuario.setRol(Usuario.Rol.USUARIO_REGULAR); // Por defecto
            nuevoUsuario.setFechaRegistro(LocalDate.now());
            nuevoUsuario.setActivo(true);

            // Parsear fecha de nacimiento si se proporcionó
            if (fechaNacimiento != null && !fechaNacimiento.trim().isEmpty()) {
                try {
                    nuevoUsuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
                } catch (Exception e) {
                    model.addAttribute("error", "La fecha de nacimiento no tiene un formato válido (YYYY-MM-DD)");
                    model.addAttribute("nombre", nombre);
                    model.addAttribute("email", email);
                    return "registro";
                }
            }

            // Llamar al servicio para registrar
            // El servicio validará:
            // - Que el email no exista
            // - Que la contraseña sea segura
            // - Encriptará la contraseña
            usuarioService.registrar(nuevoUsuario);

            // Registro exitoso - redirigir a login con mensaje
            redirectAttributes.addAttribute("mensaje", "registroExitoso");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Errores de validación del servicio (email duplicado, contraseña débil, etc.)
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";

        } catch (Exception e) {
            // Cualquier otro error inesperado
            System.err.println("ERROR EN REGISTRO: " + e.getMessage());
            model.addAttribute("error", "Error al registrar el usuario. Por favor, intenta nuevamente.");
            model.addAttribute("nombre", nombre);
            model.addAttribute("email", email);
            return "registro";
        }
    }

    /**
     * Versión alternativa: Procesa el registro usando @ModelAttribute
     * Esta versión es útil si usas data binding de Spring
     *
     * @param usuario Usuario con datos del formulario
     * @param result Resultado de la validación
     * @param passwordConfirm Confirmación de contraseña (no está en el modelo)
     * @param redirectAttributes Para mensajes de redirección
     * @param model Para pasar datos a la vista
     * @return Redirección o vista según el resultado
     */
    @PostMapping("/registro-alt")
    public String procesarRegistroConModelAttribute(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam("passwordConfirm") String passwordConfirm,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validar errores de Bean Validation (@NotNull, @Email, etc.)
        if (result.hasErrors()) {
            return "registro";
        }

        // Validar que las contraseñas coincidan
        if (!usuario.getContrasena().equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "registro";
        }

        try {
            // Establecer valores por defecto
            usuario.setRol(Usuario.Rol.USUARIO_REGULAR);
            usuario.setFechaRegistro(LocalDate.now());
            usuario.setActivo(true);

            // Registrar (el service valida y encripta)
            usuarioService.registrar(usuario);

            // Éxito
            redirectAttributes.addAttribute("mensaje", "registroExitoso");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario");
            return "registro";
        }
    }

    /**
     * Verifica si un email ya está registrado (AJAX)
     * GET /registro/verificar-email
     *
     * Este endpoint puede ser llamado desde JavaScript para validar
     * el email en tiempo real mientras el usuario escribe
     *
     * @param email Email a verificar
     * @return "true" si está disponible, "false" si ya existe
     */
    @GetMapping("/registro/verificar-email")
    public String verificarEmail(@RequestParam("email") String email, Model model) {
        boolean existe = usuarioService.existeEmail(email);
        model.addAttribute("existe", existe);
        return "json-response"; // Vista que retorna JSON simple
    }
}
