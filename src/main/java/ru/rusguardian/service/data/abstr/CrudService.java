package ru.rusguardian.service.data.abstr;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class CrudService<T> {

    private final JpaRepository<T, Long> repository;

    public T findById(Long id) {
        return findByIdOptional(id).orElseThrow();
    }

    public Optional<T> findByIdOptional(Long id) {
        return repository.findById(id);
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public T update(T entity) {
        findById(getIdFromEntity(entity));
        return repository.save(entity);
    }

    protected abstract Long getIdFromEntity(T entity);
}
