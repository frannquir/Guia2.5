package model.entities.impl;

import model.entities.enums.ETipo;

import java.time.LocalDateTime;
import java.util.Objects;

public class CuentaEntity {
    private Integer id;
    private Integer usuarioId;
    private ETipo tipo;
    private Float saldo;
    private LocalDateTime fecha_creacion;

    public CuentaEntity(Float saldo, ETipo tipo, Integer usuarioId) {
        this.saldo = saldo;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
    }

    public CuentaEntity(Integer id, Integer usuarioId, ETipo tipo, Float saldo, LocalDateTime fecha_creacion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.saldo = saldo;
        this.fecha_creacion = fecha_creacion;
    }
    public CuentaEntity () {
        this(0, 0, null, 0.0F, null);
    }

    public LocalDateTime getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Float getSaldo() {
        return saldo;
    }

    public void setSaldo(Float saldo) {
        this.saldo = saldo;
    }

    public ETipo getTipo() {
        return tipo;
    }

    public void setTipo(ETipo tipo) {
        this.tipo = tipo;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CuentaEntity that = (CuentaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CuentaEntity{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", tipo=" + tipo +
                ", saldo=" + saldo +
                ", fecha_creacion=" + fecha_creacion +
                '}';
    }
}
