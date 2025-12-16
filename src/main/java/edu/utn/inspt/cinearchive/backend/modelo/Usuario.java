package edu.utn.inspt.cinearchive.backend.modelo;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

public class Usuario {

    public enum Rol {
        USUARIO_REGULAR,
        ADMINISTRADOR,
        GESTOR_INVENTARIO,
        ANALISTA_DATOS,
        CHUSMA
    }

    private Long id;

    @NotNull(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotNull(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;

    @NotNull(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private LocalDate fechaRegistro;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    public Usuario() {
    }

    /**
     * Constructor con parámetros principales
     * Establece valores por defecto para fechaRegistro y activo
     */
    public Usuario(String nombre, String email, String contrasena, Rol rol) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.rol = rol;
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean tieneRol(Rol rolEsperado) {
        return this.rol == rolEsperado;
    }

    /**
     * Verifica si el usuario está activo
     */
    public boolean estaActivo() {
        return this.activo != null && this.activo;
    }

    /**
     * Calcula la edad del usuario basándose en su fecha de nacimiento
     * Útil para reportes demográficos
     */
    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}