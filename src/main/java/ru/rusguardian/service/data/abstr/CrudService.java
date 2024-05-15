package ru.rusguardian.service.data.abstr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class CrudService<T, ID> {

    public T findById(ID id) {
        return findByIdOptional(id).orElseThrow();
    }

    public Optional<T> findByIdOptional(ID id) {
        return getRepository().findById(id);
    }

    public T save(T entity) {
        return getRepository().save(entity);
    }

    public T update(T entity) {
        findById(getIdFromEntity(entity));
        return getRepository().save(entity);
    }

    public List<T> getAll() {
        return getRepository().findAll();
    }

    public void saveAll(List<T> entities) {
        getRepository().saveAll(entities);
    }

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract ID getIdFromEntity(T entity);
}
