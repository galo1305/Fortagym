package com.fortagym.model;

import jakarta.persistence.*;

@Entity
public class DetalleRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ejercicio;
    private String seriesReps;
    private String descanso;
    private String dias; // Ejemplo: L-M-V o Lun/Mie/Vie

    @ManyToOne
    @JoinColumn(name = "rutina_id")
    private Rutina rutina;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEjercicio() { return ejercicio; }
    public void setEjercicio(String ejercicio) { this.ejercicio = ejercicio; }

    public String getSeriesReps() { return seriesReps; }
    public void setSeriesReps(String seriesReps) { this.seriesReps = seriesReps; }

    public String getDescanso() { return descanso; }
    public void setDescanso(String descanso) { this.descanso = descanso; }

    public String getDias() { return dias; }
    public void setDias(String dias) { this.dias = dias; }

    public Rutina getRutina() { return rutina; }
    public void setRutina(Rutina rutina) { this.rutina = rutina; }
}
