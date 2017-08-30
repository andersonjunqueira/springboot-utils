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

import br.com.ertic.util.infraestructure.domain.model.EntidadeBase;
import br.com.ertic.util.infraestructure.dto.Token;
import br.com.ertic.util.infraestructure.service.RestFullService;

public class RestFullEndpoint<E extends EntidadeBase<PK>, PK extends Serializable> {

    @Autowired
    protected Token token;

    protected RestFullService<E, PK> service;

    protected RestFullEndpoint(RestFullService<E, PK> service) {
        this.service = service;
    }

    protected RestFullService<E, PK> getService() {
        return service;
    }

    /**
     * Método para inclusão de novos registros.
     * Caso a entidade enviada possuir o atributo "id" preenchido, um erro 400 / BAD REQUEST é retornado.
     * @param input entidade a ser incluída
     * @return os dados da entidade registrada
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody E input) {
        if(input.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(service.save(input), HttpStatus.CREATED);
    }

    /**
     * Método para inclusão ou atualização de registros.
     * Caso a entidade enviada possuir o atributo "id" preenchido, será feita uma atualização do registro (retorno 200).
     * Caso a entidade enviada não possua o atributo "id" preenchido, será feita uma inclusão do registro (retorno 201).
     * @param input entidade a ser incluída ou atualizada
     * @return os dados da entidade registrada
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> addOrUpdate(@RequestBody E input) {
        return new ResponseEntity<>(
            service.save(input),
            input.getId() == null ? HttpStatus.CREATED : HttpStatus.OK);
    }

    /**
     * Método para atualização de registros.
     * O valor informado como id na URL irá sobrepor o id da entidade enviada (se houver). Será feita uma atualização do registro.
     * @param input entidade a ser atualizada
     * @return os dados da entidade registrada
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<?> addOrUpdateByPK(@RequestBody E input, @PathVariable PK id) {
        input.setId(id);
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
