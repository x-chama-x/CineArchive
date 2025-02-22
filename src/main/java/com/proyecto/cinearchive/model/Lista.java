package com.proyecto.cinearchive.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "lista")
public class Lista {
    @Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY) // Autoincremental
    private int listaId;

    @Column(nullable = false)
    private String nombre;

    // RELACIONES

    @ManyToOne
    @JoinColumn(name = "usuarioId", nullable = false) // mapear la clave foránea
    private Usuario usuario;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL)
    private List<ListaContenido> listaContenidos; //atributo que accede a los contenidos de una lista especifica

    // GETTERS Y SETTERS

    public int getListaId() {
        return listaId;
    }

    public void setListaId(int listaId) {
        this.listaId = listaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ListaContenido> getListaContenidos() {
        return listaContenidos;
    }

    public void setListaContenidos(List<ListaContenido> listaContenidos) {
        this.listaContenidos = listaContenidos;
    }
}
