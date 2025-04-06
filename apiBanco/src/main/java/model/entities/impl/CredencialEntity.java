package model.entities.impl;

import model.entities.enums.EPermiso;

import java.util.Objects;

public class CredencialEntity {
    private Integer id;
    private Integer usuarioId;
    private String username;
    private String password;
    private EPermiso permiso;

    public CredencialEntity(Integer id, Integer usuarioId, String username, String password, EPermiso permiso) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.username = username;
        this.password = password;
        this.permiso = permiso;
    }
    public CredencialEntity(Integer usuarioId, String username, String password, EPermiso permiso) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.password = password;
        this.permiso = permiso;
    }
    public CredencialEntity() {
        this(0, "", "", null);
    }

    public EPermiso getPermiso() {
        return permiso;
    }

    public void setPermiso(EPermiso permiso) {
        this.permiso = permiso;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        CredencialEntity that = (CredencialEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(usuarioId, that.usuarioId) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, usuarioId, username);
    }
}
