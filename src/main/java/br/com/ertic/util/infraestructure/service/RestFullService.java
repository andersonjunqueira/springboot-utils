package br.com.ertic.util.infraestructure.service;

import static org.springframework.data.domain.ExampleMatcher.matching;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import br.com.ertic.util.infraestructure.domain.model.EntidadeBase;
import br.com.ertic.util.infraestructure.exception.InternalException;
import br.com.ertic.util.infraestructure.exception.NegocioException;
import br.com.ertic.util.infraestructure.jpa.RepositoryBase;

public class RestFullService<E extends EntidadeBase<PK>, PK extends Serializable> {

    private final String SORT_KEY = "sort";
    private final String DESC_KEY = "desc";
    private final String PAGESIZE_KEY = "page";
    private final String PAGESTART_KEY = "start";
    private final int PAGESIZE_DEFAULT = 10;
    private static List<String> IGNORED_KEYS = new ArrayList<>();

    private Class<E> modelClass;
    protected final RepositoryBase<E, PK> repository;

    @SuppressWarnings("unchecked")
    private void loadTypes() {
        if(modelClass == null) {
            final ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
            modelClass = (Class<E>)type.getActualTypeArguments()[0];
        }
    }

    protected RestFullService(RepositoryBase<E, PK> repository) {
        this.repository = repository;
        loadTypes();
    }

    protected RepositoryBase<E, PK> getRepository() {
        return repository;
    }

    public List<E> findAll() throws NegocioException {
        return repository.findAll();
    }

    public List<E> findAll(Example<E> example) throws NegocioException {
        return repository.findAll(example);
    }

    public List<E> findAll(Example<E> example, Sort sort) throws NegocioException {
        return repository.findAll(example, sort);
    }

    public List<E> findAll(Sort sort) throws NegocioException {
        return repository.findAll(sort);
    }

    public List<E> findAll(Map<String, String[]> params) throws NegocioException {
        return repository.findAll(getExample(params), getSort(params));
    }

    public Page<E> findAllPageable(Map<String, String[]> params) throws NegocioException {
        return repository.findAll(getExample(params), getPageRequest(params));
    }

    public E findOne(PK id) throws NegocioException {
        return repository.findOne(id);
    }

    @Transactional
    public void delete(PK id) throws NegocioException {
        repository.delete(id);
    }

    @Transactional
    public E update(E e) throws NegocioException {
        return save(e);
    }

    @Transactional
    public E add(E e) throws NegocioException {
        return save(e);
    }

    @Transactional
    public E addOrUpdate(E e) throws NegocioException {
        return save(e);
    }

    @Transactional
    private E save(E e) throws NegocioException {
        return repository.save(e);
    }

    @Transactional
    public Long nextVal(String sequenceName) throws NegocioException {
        return repository.nextVal(sequenceName);
    }

    protected Pageable getPageRequest(Map<String, String[]> params) {

        int start = 0;
        int size = 10;

        String[] startParam = params.get(PAGESTART_KEY);
        if(startParam != null) {
            start = Integer.parseInt(startParam[0]);
            start = start < 0 ? 0 : start;
        }

        String[] sizeParam = params.get(PAGESIZE_KEY);
        if(sizeParam != null) {
            size = Integer.parseInt(sizeParam[0]);
            size = size < 1 ? PAGESIZE_DEFAULT : size;
        }

        return new PageRequest(start, size, getSort(params));

    }

    protected Sort getSort(Map<String, String[]> params) {
        String[] sort = params.get(SORT_KEY);
        if(sort != null && sort.length == 1 && sort[0].length() > 0 ) {
            String field = sort[0];
            Sort.Direction direction = Sort.Direction.ASC;
            if(field.indexOf(",") > -1) {
                String[] tokens = field.split(",");
                field = tokens[0];
                direction = tokens[1].equalsIgnoreCase(DESC_KEY) ? Sort.Direction.DESC : Sort.Direction.ASC;
            }
            return new Sort(direction, field);
        }

        return null;
    }

    protected Example<E> getExample(Map<String, String[]> params) {

        if(IGNORED_KEYS.isEmpty()) {
            IGNORED_KEYS.add(SORT_KEY);
            IGNORED_KEYS.add(PAGESIZE_KEY);
            IGNORED_KEYS.add(PAGESTART_KEY);
        }

        try {

            E obj = modelClass.newInstance();
            ExampleMatcher em = matching();

            for(String key : params.keySet()) {

                if(IGNORED_KEYS.contains(key)) {
                    continue;
                }

                String[] values = params.get(key);
                if(values.length != 1) {
                    continue;
                }

                Method m = getMethodFromProperty(key);
                if(m != null) {

                    // TRATAMENTOS DE PARAMETROS DO TIPO STRING
                    if(m.getParameterTypes()[0].equals(String.class)) {

                        if(values[0].startsWith("*")) {
                            em = matching().withMatcher(key, matcher -> matcher.endsWith().ignoreCase());

                        } else if(values[0].endsWith("*")) {
                            em = matching().withMatcher(key, matcher -> matcher.startsWith().ignoreCase());

                        } else {
                            em = matching().withMatcher(key, matcher -> matcher.ignoreCase());
                        }
                    }

                    m.invoke(obj, values[0].replaceAll("\\*", ""));
                }

            }

            return Example.of(obj, em);

        } catch(IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            throw new InternalException("problemas-classe-findall", ex);
        }
    }

    protected Method getMethodFromProperty(String property) {
        String mName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
        for(Method m : modelClass.getMethods()) {
            if(m.getName().equals(mName) && m.getParameterTypes().length == 1) {
                return m;
            }
        }
        return null;
    }

}
