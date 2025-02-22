package com.proyecto.cinearchive.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lista_contenido")
public class ListaContenido {
    // Clave primaria compuesta
    @EmbeddedId
    private ListaContenidoId id; // Clase que representa la clave primaria compuesta

    // Relación con Lista
    @ManyToOne
    @MapsId("listaid") // Mapea el campo "listaid" de la clave primaria compuesta
    @JoinColumn(name = "listaid", nullable = false)
    private Lista lista;

    // Relación con Contenido
    @ManyToOne
    @MapsId("contenidoid") // Mapea el campo "contenidoid" de la clave primaria compuesta
    @JoinColumn(name = "contenidoid", nullable = false)
    private Contenido contenido;

}
