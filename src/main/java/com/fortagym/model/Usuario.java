package com.fortagym.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido; // 👈 NUEVO CAMPO
    @Column(unique = true)
    private String email;
    private String password;

    @Lob
    private byte[] fotoPerfil;

    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido; // 👈 NUEVO
        this.email = email;
        this.password = password;
        this.fotoPerfil = fotoPerfil;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getFotoPerfil(){
        return fotoPerfil;
    }
    
    public void setFotoPerfil(byte[] fotoPerfil) {
    this.fotoPerfil = fotoPerfil;
}
    
}
