package com.fortagym.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="nutricion")
public class Nutricion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_nutricion")
    private Long id;

    @OneToOne
    @JoinColumn(name="id_usuario")
    private Usuario usuario;

    @Column(name="analisis_corporal", length = 255)
    private String analisisCorporal;

    @Column(name="fecha_registro")
    private LocalDateTime fechaRegistro;

    @Lob
    private String observaciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getAnalisisCorporal() {
        return analisisCorporal;
    }

    public void setAnalisisCorporal(String analisisCorporal) {
        this.analisisCorporal = analisisCorporal;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Nutricion() {}

    public Nutricion(Usuario usuario, String analisisCorporal, String observaciones) {
    this.usuario = usuario;
    this.analisisCorporal = analisisCorporal;
    this.observaciones = observaciones;
    this.fechaRegistro = LocalDateTime.now();
}

}
