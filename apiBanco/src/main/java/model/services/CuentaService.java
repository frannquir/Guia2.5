package model.services;

import model.entities.enums.ETipo;
import model.entities.impl.CuentaEntity;
import model.repositories.impl.CuentaRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CuentaService {
    private static CuentaService instance;
    private final CuentaRepository cuentaRepository;

    private CuentaService() {
        cuentaRepository = CuentaRepository.getInstance();
    }

    public static CuentaService getInstance() {
        if (instance == null) instance = new CuentaService();
        return instance;
    }

    public boolean crearCuenta(Integer usuarioId, float saldo) {
        try {
            var nuevaCuenta = new CuentaEntity(saldo, ETipo.CUENTA_CORRIENTE, usuarioId);
            cuentaRepository.save(nuevaCuenta);
            return true;
        } catch (SQLException e) {
            System.out.println("Error al crear cuenta " + e.getMessage());
            return false;
        }
    }

    // Podria reemplazar este metodo por un findByUsuarioId() en CuentaRepository
    public List<CuentaEntity> listarCuentasUsuario(Integer usuarioId) {
        try {
            return cuentaRepository.findAll()
                    .stream()
                    .filter((cuenta) -> cuenta.getUsuarioId().equals(usuarioId))
                    .toList();
        } catch (SQLException e) {
            System.out.println("Error al listar las cuentas del usuario " + e.getMessage());
            return List.of();
        }
    }

    public CuentaEntity obtenerCuenta(Integer cuentaId) {
        var cuenta = new CuentaEntity();
        try {
            Optional<CuentaEntity> cuentaOpt = cuentaRepository.findByID(cuentaId);
            if(cuentaOpt.isPresent()) {
                cuenta = cuentaOpt.get();
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cuenta por id " + e.getMessage());
        }
        return cuenta;
    }

    public boolean depositar(Integer cuentaId, Float monto) {
        try {
            Optional<CuentaEntity> cuentaOpt = cuentaRepository.findByID(cuentaId);
            if (cuentaOpt.isEmpty()) return false;

            CuentaEntity cuenta = cuentaOpt.get();
            cuenta.setSaldo(cuenta.getSaldo() + monto);
            cuentaRepository.update(cuenta);
            return true;

        } catch (SQLException e) {
            System.out.println("Error al depositar " + e.getMessage());
            return false;
        }
    }

    public boolean retirar(Integer cuentaId, Float monto) {
        try {
            Optional<CuentaEntity> cuentaOpt = cuentaRepository.findByID(cuentaId);
            if (cuentaOpt.isEmpty() || cuentaOpt.get().getSaldo() < monto) return false;

            CuentaEntity cuenta = cuentaOpt.get();
            cuenta.setSaldo(cuenta.getSaldo() - monto);
            cuentaRepository.update(cuenta);
            return true;
        } catch (SQLException e) {
            System.out.println("Error al retirar " + e.getMessage());
            return false;
        }
    }

    public boolean transferir(Integer origenId, Integer destinoId, Float monto) {
        try {
            Optional<CuentaEntity> origenOpt = cuentaRepository.findByID(origenId);
            Optional<CuentaEntity> destinoOpt = cuentaRepository.findByID(destinoId);

            if (origenOpt.isEmpty() || destinoOpt.isEmpty()) {
                System.err.println("Error: Cuenta no encontrada");
                return false;
            }

            CuentaEntity origen = origenOpt.get();
            if (origen.getSaldo() < monto) {
                System.err.println("Error: Saldo insuficiente");
                return false;
            }

            // Retiro temporal
            origen.setSaldo(origen.getSaldo() - monto);
            cuentaRepository.update(origen);

            // Intento depósito
            boolean depositoExitoso = depositar(destinoId, monto);

            if (!depositoExitoso) {
                // Rollback del retiro
                origen.setSaldo(origen.getSaldo() + monto);
                cuentaRepository.update(origen);
                return false;
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error en transferencia: " + e.getMessage());
            return false;
        }
    }


}
