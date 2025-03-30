package model.repositories.impl;

import model.entities.impl.CredencialEntity;
import model.repositories.interfaces.IRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CredencialRepository implements IRepository<CredencialEntity> {
    private static CredencialRepository instance;

    private CredencialRepository () {}

    public static CredencialRepository getInstance() {
        if(instance == null ) instance = new CredencialRepository();
        return instance;
    }

    @Override
    public void save(CredencialEntity entity) throws SQLException {

    }

    @Override
    public List<CredencialEntity> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<CredencialEntity> findByID(Integer id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public void deleteByID(Integer id) throws SQLException {

    }

    @Override
    public void update(CredencialEntity entity) throws SQLException {

    }
}
