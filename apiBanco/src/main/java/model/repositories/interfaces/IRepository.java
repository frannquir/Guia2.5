package model.repositories.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    public void save(T entity) throws SQLException;
    public List<T> findAll () throws SQLException;
    public Optional<T> findByID (Integer id) throws SQLException;
    public void deleteByID (Integer id) throws SQLException;
    public void update (T entity) throws SQLException;
}
