package com.fortagym.model;

import jakarta.persistence.*;

@Entity
@Table(name = "membresias")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    @Column(name="duracion_meses")
    private int duracionMeses;

    @Column(length = 500)
    private String descripcion;

    private double precio;
    @Column(name = "imagen_url")
    private String imagenUrl; // 👈 URL o nombre del archivo de imagen

    public Membresia() {
    }

    public Membresia(String tipo, int duracionMeses, String descripcion, double precio, String imagenUrl) {
        this.tipo = tipo;
        this.duracionMeses = duracionMeses;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(int duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
