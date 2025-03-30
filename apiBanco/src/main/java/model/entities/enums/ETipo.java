package model.entities.enums;

public enum ETipo {
    CAJA_AHORRO ("Ahorro"), CUENTA_CORRIENTE ("Corriente");

    private String tipo;

    ETipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
