package model.entities.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsuarioEntity {
    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private LocalDateTime fecha_creacion;
    private List<CuentaEntity> cuentas;

    public UsuarioEntity(Integer id, String nombre, String apellido,
                         String dni, String email, LocalDateTime fecha_creacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.fecha_creacion = fecha_creacion;
        cuentas = new ArrayList<>();
    }
    public UsuarioEntity(String nombre, String apellido, String dni,
                         String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni =dni;
        this.email = email;
        cuentas = new ArrayList<>();
    }
    public UsuarioEntity() {
        this(0, "", "", "", "", null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", fecha_creacion=" + fecha_creacion +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioEntity that = (UsuarioEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
