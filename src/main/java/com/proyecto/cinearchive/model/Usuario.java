package com.proyecto.cinearchive.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    public enum Rol {
        ADMIN, USER
    }

    // Atributos

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // la clave primaria es autoincremental
    private int usuarioId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true) // La columna 'contraseña' es obligatoria y debe ser única en la tabla 'usuario'.
    private String contraseña;

    @Enumerated(EnumType.STRING)  // Mapea el enum como una cadena en la base de datos
    @Column(nullable = false)
    private Rol rol;

    // RELACIONES

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL) // la relación está mapeada por el atributo usuario en la entidad Contenido
    private List<Contenido> contenidos; // atributo que representa la lista de contenidos asociados a un usuario

    // cascade = CascadeType.ALL --> operaciones de persistencia se propagan de la tabla usuario a la tabla contenido

    // GETTERS Y SETTERS

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
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

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Contenido> getContenidos() {
        return contenidos;
    }

    public void setContenidos(List<Contenido> contenidos) {
        this.contenidos = contenidos;
    }
}
