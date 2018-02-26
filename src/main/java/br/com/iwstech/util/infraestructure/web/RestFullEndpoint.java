package br.com.iwstech.util.infraestructure.web;

import java.io.IOException;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import br.com.iwstech.util.infraestructure.domain.model.EntidadeBase;
import br.com.iwstech.util.infraestructure.dto.Token;
import br.com.iwstech.util.infraestructure.exception.NegocioException;
import br.com.iwstech.util.infraestructure.log.Log;
import br.com.iwstech.util.infraestructure.service.RestFullService;

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

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> findAllPageable(HttpServletRequest request) {
        try {

            Page<E> saida = service.findAllPageable(request.getParameterMap());
            if(saida == null || saida.getTotalElements() == 0) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(saida, HttpStatus.OK);
            }

        } catch (NegocioException ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);

        } catch (Exception ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<?> findOne(@PathVariable PK id) {
        try {

            E saida = service.findOne(id);
            if(saida == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(saida, HttpStatus.OK);
            }

        } catch (NegocioException ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);

        } catch (Exception ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Método para inclusão de novos registros.
     * Caso a entidade enviada possuir o atributo "id" preenchido, um erro 400 / BAD REQUEST é retornado.
     * @param input entidade a ser incluída
     * @return os dados da entidade registrada
    */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(
        HttpServletRequest request,
        UriComponentsBuilder b,
        @RequestBody E input) {

        try {

           if(input.getId() != null) {
               return new ResponseEntity<>("erro-id-desnecessario", HttpStatus.BAD_REQUEST);
           }

           E entity = service.add(input);

           String cp = request.getContextPath();
           String location = request.getRequestURI() + "/{id}";
           location = location.substring(location.indexOf(cp) + cp.length());

           UriComponents uric = b.path(location).buildAndExpand(entity.getId());
           return ResponseEntity.created(uric.toUri()).build();

       } catch (NegocioException ex) {
           Log.error(this.getClass(), ex);
           return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);

       } catch (Exception ex) {
           Log.error(this.getClass(), ex);
           return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }

   /**
    * Método para inclusão ou atualização de registros.
    * Caso a entidade enviada possuir o atributo "id" preenchido, será feita uma atualização do registro (retorno 200).
    * Caso a entidade enviada não possua o atributo "id" preenchido, será feita uma inclusão do registro (retorno 201).
    * @param input entidade a ser incluída ou atualizada
    * @return os dados da entidade registrada
    */
   @RequestMapping(method = RequestMethod.PUT)
   public ResponseEntity<?> addOrUpdateEntity(
       HttpServletRequest request,
       UriComponentsBuilder b,
       @RequestBody E input) {
       try {

           boolean creation = input.getId() == null;

           E entity = service.addOrUpdate(input);
           if(creation) {

               String cp = request.getContextPath();
               String location = request.getRequestURI() + "/{id}";
               location = location.substring(location.indexOf(cp) + cp.length());

               UriComponents uric = b.path(location).buildAndExpand(entity.getId());
               return ResponseEntity.created(uric.toUri()).build();

           } else {

               return new ResponseEntity<>(entity, HttpStatus.OK);

           }

       } catch (NegocioException ex) {
           Log.error(this.getClass(), ex);
           return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);

       } catch (Exception ex) {
           Log.error(this.getClass(), ex);
           return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }

    /**
     * Método para inclusão de novos registros.
     * Caso a entidade enviada possuir o atributo "id" preenchido, um erro 400 / BAD REQUEST é retornado.
     * @param input entidade a ser incluída
     * @return os dados da entidade registrada
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}")
    public ResponseEntity<?> update(
        @RequestBody String input,
        @PathVariable PK id) {
        try {

            if(id == null) {
                return new ResponseEntity<>("erro-id-obrigatorio", HttpStatus.BAD_REQUEST);
            }

            ObjectMapper objectMapper = new ObjectMapper();

            E original = service.findOne(id);
            ObjectReader updater = objectMapper.readerForUpdating(original);
            E entidade;

            try {
                entidade = updater.readValue(input);
            } catch (IOException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(service.update(entidade), HttpStatus.OK);

        } catch (NegocioException ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);

        } catch (Exception ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Método para atualização de registros.
     * O valor informado como id na URL irá sobrepor o id da entidade enviada (se houver). Será feita uma atualização do registro.
     * @param input entidade a ser atualizada
     * @return os dados da entidade registrada
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<?> addOrUpdateAllByPK(@RequestBody E input, @PathVariable PK id) {
        try {

            input.setId(id);
            return new ResponseEntity<>(service.addOrUpdate(input), HttpStatus.OK);

        } catch (NegocioException ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);

        } catch (Exception ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable PK id, String userEmail) {
        try {

            E saida = service.findOne(id);
            if(saida == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                service.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        } catch (NegocioException ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);

        } catch (Exception ex) {
            Log.error(this.getClass(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
