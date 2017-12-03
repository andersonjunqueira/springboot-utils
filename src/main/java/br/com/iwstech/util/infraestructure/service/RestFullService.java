package br.com.iwstech.util.infraestructure.service;

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
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import br.com.iwstech.util.infraestructure.domain.model.EntidadeBase;
import br.com.iwstech.util.infraestructure.domain.model.ExclusaoLogica;
import br.com.iwstech.util.infraestructure.domain.model.SimNao;
import br.com.iwstech.util.infraestructure.exception.InternalException;
import br.com.iwstech.util.infraestructure.exception.NegocioException;
import br.com.iwstech.util.infraestructure.jpa.RepositoryBase;
import br.com.iwstech.util.infraestructure.log.Log;

public class RestFullService<E extends EntidadeBase<PK>, PK extends Serializable> {

    private final String PAGE_KEY = "page";
    private final String PAGELIMIT_KEY = "limit";
    private final String SORT_KEY = "sort";

    private final int PAGELIMIT_DEFAULT = 10;
    private final String DESC_KEY = "desc";
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

        // MARCADO COM A INTERFACE "ExclusaoLogica", UPDATE
        for(Class<?> iface : modelClass.getInterfaces()) {
            if(iface == ExclusaoLogica.class) {
                E e = repository.findOne(id);
                ((ExclusaoLogica)e).setExcluido(SimNao.S);
                repository.save(e);
                return;
            }
        }

        // SEM MARCAÇÃO, APAGA!
        repository.delete(id);
    }

    @Transactional
    public E update(E e) throws NegocioException {
        return save(e);
    }

    @Transactional
    public E add(E e) throws NegocioException {
        if(e instanceof ExclusaoLogica) {
            if(((ExclusaoLogica)e).getExcluido() == null) {
                ((ExclusaoLogica)e).setExcluido(SimNao.N);
            }
        }
        return save(e);
    }

    @Transactional
    public E addOrUpdate(E e) throws NegocioException {
        if(e.getId() == null) {
            return add(e);
        } else {
            return update(e);
        }
    }

    @Transactional
    protected E save(E e) throws NegocioException {
        return repository.save(e);
    }

    @Transactional
    public Long nextVal(String sequenceName) throws NegocioException {
        return repository.nextVal(sequenceName);
    }

    protected Pageable getPageRequest(Map<String, String[]> params) {

        int page = 0;
        int limit = 10;

        String[] pageParam = params.get(PAGE_KEY);
        if(pageParam != null) {
            page = Integer.parseInt(pageParam[0]);
            page = page < 0 ? 0 : page;
        }

        String[] limitParam = params.get(PAGELIMIT_KEY);
        if(limitParam != null) {
            limit = Integer.parseInt(limitParam[0]);
            limit = limit < 1 ? PAGELIMIT_DEFAULT : limit;
        }

        return new PageRequest(page, limit, getSort(params));

    }

    protected Sort getSort(Map<String, String[]> params) {

        String[] sortParams = params.get(SORT_KEY);
        if(sortParams != null && sortParams.length > 0 ) {

            List<Order> orders = new ArrayList<>();
            for(String sort : sortParams) {

                Sort.Direction direction = Sort.Direction.ASC;
                if(params.get(sort + ".dir").length > 0) {
                    if(params.get(sort + ".dir")[0].equalsIgnoreCase(DESC_KEY)) {
                        direction = Sort.Direction.DESC;
                    }
                }
                orders.add(new Order(direction, sort));
            }

            return new Sort(orders);
        }

        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Example<E> getExample(Map<String, String[]> params) {

        if(IGNORED_KEYS.isEmpty()) {
            IGNORED_KEYS.add(SORT_KEY);
            IGNORED_KEYS.add(PAGE_KEY);
            IGNORED_KEYS.add(PAGELIMIT_KEY);
        }

        try {

            E obj = modelClass.newInstance();
            ExampleMatcher em = matching();

            if(obj instanceof ExclusaoLogica) {
                ((ExclusaoLogica) obj).setExcluido(SimNao.N);
            }

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
                    Class<?> paramType = m.getParameterTypes()[0];

                    // TRATAMENTOS DE PARAMETROS DO TIPO STRING
                    if(paramType.isAssignableFrom(String.class)) {

                        if(values[0].startsWith("*")) {
                            em = matching().withMatcher(key, matcher -> matcher.endsWith().ignoreCase());

                        } else if(values[0].endsWith("*")) {
                            em = matching().withMatcher(key, matcher -> matcher.startsWith().ignoreCase());

                        } else {
                            em = matching().withMatcher(key, matcher -> matcher.ignoreCase());
                        }

                        m.invoke(obj, values[0].replaceAll("\\*", ""));

                    // TRATAMENTOS DE PARAMETROS DO TIPO SIMNAO ENUM
                    } else if(paramType.isEnum()) {

                        try {
                            Class<Enum> ecl = (Class<Enum>)paramType;
                            Enum<?> v = Enum.valueOf(ecl, values[0]);
                            if(v != null) {
                                m.invoke(obj, v);
                            }
                        } catch(IllegalArgumentException ex) {
                            Log.warn(this.getClass(), "Valor não válido para o enum, desconsiderando.");
                        }

                    }
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
