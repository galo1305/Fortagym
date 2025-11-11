package com.fortagym.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pase_diario")
public class PaseDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String duracion; // 1 día, 3 días, 7 días
    private String acceso; // tipo de acceso
    private double precio; // costo
    private String imagenUrl; // ruta o URL de imagen

    // === Constructores ===
    public PaseDiario() {
    }

    public PaseDiario(String duracion, String acceso, double precio, String imagenUrl) {
        this.duracion = duracion;
        this.acceso = acceso;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    // === Getters y Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getAcceso() {
        return acceso;
    }

    public void setAcceso(String acceso) {
        this.acceso = acceso;
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
