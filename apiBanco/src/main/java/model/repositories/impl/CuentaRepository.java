package model.repositories.impl;

import model.repositories.interfaces.IRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CuentaRepository implements IRepository {
    private static CuentaRepository instance;

    public CuentaRepository() {
    }

    public static CuentaRepository getInstance() {
        if(instance == null) instance = new CuentaRepository();
        return instance;
    }

    @Override
    public void save(Object entity) throws SQLException {

    }

    @Override
    public List findAll() throws SQLException {
        return List.of();
    }

    @Override
    public Optional findByID(Integer id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public void deleteByID(Integer id) throws SQLException {

    }

    @Override
    public void update(Object entity) throws SQLException {

    }
}
