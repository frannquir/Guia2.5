package model.services;

import model.entities.enums.EPermiso;
import model.entities.enums.ETipo;
import model.entities.impl.CredencialEntity;
import model.entities.impl.CuentaEntity;
import model.entities.impl.UsuarioEntity;
import model.exceptions.NoAutorizadoException;
import model.repositories.impl.CuentaRepository;
import model.repositories.impl.UsuarioRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CuentaService {
    private static CuentaService instance;
    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    private CuentaService() {
        cuentaRepository = CuentaRepository.getInstance();
        usuarioRepository = UsuarioRepository.getInstance();
    }

    public static CuentaService getInstance() {
        if (instance == null) instance = new CuentaService();
        return instance;
    }

    public boolean crearCuenta(CredencialEntity credencial, Integer usuarioId, Float saldo) throws NoAutorizadoException {
        try {
            if (usuarioRepository.findByID(usuarioId).isEmpty()) {
                throw new NoSuchElementException("Usuario no existente.");
            }
            if (saldo < 0) {
                System.out.println("El saldo inicial debe ser 0 o mayor.");
            }
            if(credencial.getPermiso() == EPermiso.CLIENTE) {
                if (credencial.getUsuarioId().equals(usuarioId)) { // el usuario se crea una cuenta a el mismo
                    CuentaEntity cuenta = new CuentaEntity(saldo, ETipo.CUENTA_CORRIENTE, usuarioId);
                    cuentaRepository.save(cuenta);
                    return true;
                } else {
                    throw new NoAutorizadoException("El CLIENTE solo puede crear cuentas para él.");
                }
            } else {
                CuentaEntity cuenta = new CuentaEntity(saldo, ETipo.CUENTA_CORRIENTE, usuarioId);
                cuentaRepository.save(cuenta);
                return true;
            }
        } catch(SQLException e) {
            System.out.println("Error al crear cuenta: " + e.getMessage());
        }
        return false;
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
            if (cuentaOpt.isPresent()) {
                cuenta = cuentaOpt.get();
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cuenta por id " + e.getMessage());
        }
        return cuenta;
    }

    public boolean depositar(CredencialEntity credencial, Integer cuentaId, Float monto) throws NoAutorizadoException {
        try {
            if (monto <= 0) {
                System.out.println("El monto debe ser mayor a cero");
                return false;
            }

            Optional<CuentaEntity> cuentaOpt = cuentaRepository.findByID(cuentaId);
            if (cuentaOpt.isEmpty()) {
                throw new NoSuchElementException("No se encontró la cuenta.");
            }

            CuentaEntity cuenta = cuentaOpt.get();

            switch (credencial.getPermiso()) {
                case CLIENTE -> {
                    if (!cuenta.getUsuarioId().equals(credencial.getUsuarioId())) {
                        throw new NoAutorizadoException("Como CLIENTE, solo puedes depositar en tus propias cuentas.");
                    }
                }
                case GESTOR -> {
                    // Verificar si la cuenta pertenece a un cliente
                    Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByID(cuenta.getUsuarioId());
                    if (usuarioOpt.isEmpty() || usuarioOpt.get().getCredencial().getPermiso() != EPermiso.CLIENTE) {
                        throw new NoAutorizadoException("Como GESTOR, solo puedes depositar en cuentas de CLIENTES.");
                    }
                }
                case ADMINISTRADOR -> {
                    // Administrador puede depositar en cualquier cuenta
                }
            }

            // Realizar el depósito
            cuenta.setSaldo(cuenta.getSaldo() + monto);
            cuentaRepository.update(cuenta);
            return true;

        } catch (SQLException e) {
            System.out.println("Error al depositar: " + e.getMessage());
            return false;
        }
    }

    public boolean retirar(CredencialEntity credencial, Integer cuentaId, Float monto) throws NoAutorizadoException {
        try {
            if (monto <= 0) {
                System.out.println("El monto debe ser mayor a cero");
                return false;
            }

            Optional<CuentaEntity> cuentaOpt = cuentaRepository.findByID(cuentaId);
            if (cuentaOpt.isEmpty()) {
                throw new NoSuchElementException("No se encontró la cuenta.");
            }

            CuentaEntity cuenta = cuentaOpt.get();

            // Verificar saldo
            if (cuenta.getSaldo() < monto) {
                System.out.println("Saldo insuficiente");
                return false;
            }

            // Verificar autorización
            if (credencial.getPermiso() == EPermiso.CLIENTE && !cuenta.getUsuarioId().equals(credencial.getUsuarioId())) {
                throw new NoAutorizadoException("Como CLIENTE, solo puedes retirar de tus propias cuentas.");
            }

            // Realizar retiro
            cuenta.setSaldo(cuenta.getSaldo() - monto);
            cuentaRepository.update(cuenta);
            return true;

        } catch (SQLException e) {
            System.out.println("Error al retirar: " + e.getMessage());
            return false;
        }
    }

    public boolean transferir(CredencialEntity credencial, Integer origenId, Integer destinoId, Float monto) throws NoAutorizadoException {
        try {
            if (monto <= 0) {
                System.out.println("El monto debe ser mayor a cero");
                return false;
            }

            // Verificar existencia de cuentas
            Optional<CuentaEntity> origenOpt = cuentaRepository.findByID(origenId);
            Optional<CuentaEntity> destinoOpt = cuentaRepository.findByID(destinoId);

            if (origenOpt.isEmpty() || destinoOpt.isEmpty()) {
                System.out.println("No se encontró alguna de las cuentas");
                return false;
            }

            CuentaEntity origen = origenOpt.get();
            CuentaEntity destino = destinoOpt.get();

            // Verificar saldo
            if (origen.getSaldo() < monto) {
                System.out.println("Saldo insuficiente");
                return false;
            }

            // Verificar permisos según el rol
            switch (credencial.getPermiso()) {
                case CLIENTE -> {
                    // CLIENTE solo puede transferir desde sus propias cuentas
                    if (!origen.getUsuarioId().equals(credencial.getUsuarioId())) {
                        throw new NoAutorizadoException("Como CLIENTE, solo puedes transferir desde tus propias cuentas.");
                    }
                }
                case GESTOR, ADMINISTRADOR -> {
                    // Pueden transferir entre cuentas de diferentes usuarios
                }
            }

            // Realizar la transferencia usando transacciones
            origen.setSaldo(origen.getSaldo() - monto);
            cuentaRepository.update(origen);

            try {
                destino.setSaldo(destino.getSaldo() + monto);
                cuentaRepository.update(destino);
                return true;
            } catch (SQLException e) {
                // Rollback en caso de error
                origen.setSaldo(origen.getSaldo() + monto);
                cuentaRepository.update(origen);
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error en transferencia: " + e.getMessage());
            return false;
        }
    }


}
