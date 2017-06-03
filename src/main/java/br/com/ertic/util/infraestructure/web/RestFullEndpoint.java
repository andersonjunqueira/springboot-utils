package br.com.ertic.util.infraestructure.web;

import java.io.Serializable;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.ertic.util.infraestructure.service.RestFullService;

public class RestFullEndpoint<E, PK extends Serializable> {

    protected RestFullService<E, PK> service;

    protected RestFullEndpoint(RestFullService<E, PK> service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<E>> getAll() {
        return new ResponseEntity<>(service.findAll(),HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<E> getWithId(@PathVariable PK id) {
        return new ResponseEntity<>(service.findById(id),HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody E input) {
        return new ResponseEntity<>(service.create(input), HttpStatus.CREATED);
    }

}