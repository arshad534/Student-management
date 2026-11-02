package attendance.dao;

import java.util.List;

/** Generic data-access interface (interfaces + generics). */
public interface Dao<T> {
    void add(T item) throws Exception;

    List<T> findAll() throws Exception;
}
