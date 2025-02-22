package com.proyecto.cinearchive.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "contenido")
public class Contenido {

    public enum TipoContenido {
        SERIE, PELICULA;
    }

    @Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // Autoincremental
    private int contenidoId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private int año;

    private String descripcion;
    private double calificacion;
    private LocalDate fechaVista;
    private LocalDate fechaFinalizacion;
    private String imagenUrl;
    private int temporadas;

    @Enumerated(EnumType.STRING)
    private Boolean enEmision;

    @Column(nullable = false)
    private String director;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContenido tipo;

    // RELACIONES

    @ManyToOne // un usuario puede tener muchos contenidos
    @JoinColumn(name = "usuarioId", nullable = false) // mapear la clave foránea
    private Usuario usuario;

    // GETTERS Y SETTERS


    public int getContenidoId() {
        return contenidoId;
    }

    public void setContenidoId(int contenidoId) {
        this.contenidoId = contenidoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public LocalDate getFechaVista() {
        return fechaVista;
    }

    public void setFechaVista(LocalDate fechaVista) {
        this.fechaVista = fechaVista;
    }

    public LocalDate getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(LocalDate fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(int temporadas) {
        this.temporadas = temporadas;
    }

    public Boolean getEnEmision() {
        return enEmision;
    }

    public void setEnEmision(Boolean enEmision) {
        this.enEmision = enEmision;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public TipoContenido getTipo() {
        return tipo;
    }

    public void setTipo(TipoContenido tipo) {
        this.tipo = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
