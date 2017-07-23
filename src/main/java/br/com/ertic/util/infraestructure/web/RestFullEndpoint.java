package br.com.ertic.util.infraestructure.web;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.ertic.util.infraestructure.dto.Token;
import br.com.ertic.util.infraestructure.service.RestFullService;

public class RestFullEndpoint<E, PK extends Serializable> {

    @Autowired
    protected Token token;

    protected RestFullService<E, PK> service;

    protected RestFullEndpoint(RestFullService<E, PK> service) {
        this.service = service;
    }

    protected RestFullService<E, PK> getService() {
        return service;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody E input) {
        return new ResponseEntity<>(service.save(input), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Page<E>> getAll(HttpServletRequest request) {
        Page<E> saida = service.findAllPageable(request.getParameterMap());
        if(saida == null || saida.getSize() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(saida, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<E> getWithId(@PathVariable PK id) {
        E saida = service.findOne(id);
        if(saida == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(saida, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable PK id, String userEmail) {
        E saida = service.findOne(id);
        if(saida == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            service.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

}
