package br.com.ertic.util.infraestructure.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ertic.util.infraestructure.exception.InternalException;

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

    protected JpaRepository<E, PK> getRepository() {
        return repository;
    }

    public List<E> findAll() {
        return repository.findAll();
    }

    public List<E> findAll(Example<E> example) {
        return repository.findAll(example);
    }

    public List<E> findAll(Example<E> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    public List<E> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public List<E> findAll(Map<String, String[]> params) {
        try {
            E obj = modelClass.newInstance();

            for(String key : params.keySet()) {
                if(params.get(key).length != 1) {
                    continue;
                }

                Method m = getMethodFromProperty(key);
                if(m != null) {
                    if(m.getParameterTypes()[0].isInstance(Long.class)) {
                        m.invoke(obj, Long.parseLong(params.get(key)[0]));

                    } else if(m.getParameterTypes()[0].isInstance(Double.class)) {
                        m.invoke(obj, Double.parseDouble(params.get(key)[0]));

                    } else if(m.getParameterTypes()[0].isInstance(String.class)) {
                        m.invoke(obj, params.get(key)[0] + "%");
                    }
                }

            }

            return repository.findAll(Example.of(obj));

        } catch(IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            throw new InternalException("problemas-classe-findall", ex);
        }
    }

    private Method getMethodFromProperty(String property) {
        String mName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
        for(Method m : modelClass.getMethods()) {
            if(m.getName().equals(mName) && m.getParameterTypes().length == 1) {
                return m;
            }
        }
        return null;
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
