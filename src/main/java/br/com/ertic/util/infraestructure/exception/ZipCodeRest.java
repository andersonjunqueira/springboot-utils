package br.com.ertic.util.infraestructure.exception;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.ertic.util.infraestructure.dto.CepDTO;
import br.com.ertic.util.infraestructure.service.CepService;

@RestController
@RequestMapping("/zipcodes")
public class ZipCodeRest {

    private final CepService service;

    @Autowired
    ZipCodeRest(CepService service) {
        this.service = service;
    }

    @RequestMapping(value="/{zipcode}", method = RequestMethod.GET)
    public CepDTO getDocument(@PathVariable String zipcode, HttpServletResponse response) {
        CepDTO saida = service.find(zipcode);
        if(null == saida){
           response.setStatus(404);
        }
        return saida;
     }
}
