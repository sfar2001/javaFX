package Interfaces;

import java.util.List;

public interface IService<T> {

    public void create(T entity);
    public void update(T entity);

    public void delete(T entity);
    public List<T> getAll();

}
