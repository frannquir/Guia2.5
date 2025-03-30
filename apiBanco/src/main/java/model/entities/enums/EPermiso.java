package model.entities.enums;

public enum EPermiso {
    CLIENTE ("Cliente"), ADMINISTRADOR("Administrador"), GESTOR("Gestor");

    private String permiso;

    EPermiso(String permiso) {
        this.permiso = permiso;
    }

    public String getPermiso() {
        return permiso;
    }
}
