package br.com.ertic.util.infraestructure.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public class RestFullService<E, PK extends Serializable> {

    private Class<E> modelClass;
    protected final JpaRepository<E, PK> repository;

    @SuppressWarnings("unchecked")
    private void loadTypes() {
        if(modelClass == null) {
            final ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
            modelClass = (Class<E>)type.getActualTypeArguments()[0];
        }
    }

    protected RestFullService(JpaRepository<E, PK> repository) {
        this.repository = repository;
        loadTypes();
    }

    public List<E> findAll() {
        return repository.findAll();
    }

    public E findOne(PK id) {
        return repository.findOne(id);
    }

    @Transactional
    public void delete(PK id) {
        repository.delete(id);
    }

    @Transactional
    public E save(E e) {
        return repository.save(e);
    }

}
