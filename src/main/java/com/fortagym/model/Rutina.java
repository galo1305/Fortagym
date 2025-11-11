package com.fortagym.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String observaciones;
    private String nombreEntrenador;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRutina> detalles= new ArrayList<>();

    public Rutina() {}

    public Rutina(String observaciones, String nombreEntrenador, Usuario usuario) {
        this.observaciones = observaciones;
        this.nombreEntrenador = nombreEntrenador;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNombreEntrenador() { return nombreEntrenador; }
    public void setNombreEntrenador(String nombreEntrenador) { this.nombreEntrenador = nombreEntrenador; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<DetalleRutina> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleRutina> detalles) { this.detalles = detalles; }
}
