package com.myth.repository.impl;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Transactional
public class BaseRepository<T> {
    private final Provider<EntityManager> entityManager;

    protected BaseRepository(Provider<EntityManager> entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<List<T>> find(final Class<T> clazz) {
        return Optional.ofNullable(entityManager.get().createQuery("Select t from " + clazz.getSimpleName() + " t").getResultList());
    }

    public void persist(final T object) {
        entityManager.get().persist(object);
    }

    public void remove(final T object) {
        entityManager.get().remove(object);
    }

    public <ID> Optional<T> findById(final Class<T> clazz, final ID id) {
        return Optional.ofNullable(entityManager.get().find(clazz, id));
    }

    public T merge(final T object) {
        return entityManager.get().merge(object);
    }

    public EntityManager getEntityManager() {
        return entityManager.get();
    }
}
