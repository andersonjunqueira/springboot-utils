package br.com.ertic.util.infraestructure.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public class RestFullService<E, PK extends Serializable> {

    protected final JpaRepository<E, PK> repository;

    protected RestFullService(JpaRepository<E, PK> repository) {
        this.repository = repository;
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
