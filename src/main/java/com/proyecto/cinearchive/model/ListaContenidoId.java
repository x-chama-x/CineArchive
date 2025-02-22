package com.proyecto.cinearchive.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListaContenidoId implements Serializable {

    private int listaid;
    private int contenidoid;

    // Getters y Setters
    public int getListaid() {
        return listaid;
    }

    public void setListaid(int listaid) {
        this.listaid = listaid;
    }

    public int getContenidoid() {
        return contenidoid;
    }

    public void setContenidoid(int contenidoid) {
        this.contenidoid = contenidoid;
    }

    /* Sobrescribir equals() y hashCode()
        equals() y hashCode() son métodos que se deben sobrescribir en clases que implementan Serializable. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaContenidoId that = (ListaContenidoId) o;
        return listaid == that.listaid && contenidoid == that.contenidoid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(listaid, contenidoid);
    }
}

/* ListaContenidoId es una clase embebida que representa la clave primaria compuesta de ListaContenido
    - ListaContenidoId es una clave primaria compuesta, y JPA requiere que todas las claves primarias (simples o compuestas) sean serializables.
    - Si no implementa Serializable, JPA lanzará una excepción al intentar persistir o recuperar entidades que usen esta clave primaria compuesta.
 */
