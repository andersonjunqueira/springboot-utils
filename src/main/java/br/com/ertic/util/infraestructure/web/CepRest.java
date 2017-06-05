package br.com.ertic.util.infraestructure.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.ertic.util.infraestructure.dto.CepDTO;
import br.com.ertic.util.infraestructure.service.CepService;

@RestController
@RequestMapping("/ceps")
public class CepRest {

    private final CepService service;

    @Autowired
    CepRest(CepService service) {
        this.service = service;
    }

    @RequestMapping(value="/{cep}", method = RequestMethod.GET)
    public CepDTO getDocument(@PathVariable String cep, HttpServletResponse response) {
        CepDTO saida = service.find(cep);
        if(null == saida){
           response.setStatus(404);
        }
        return saida;
     }
}
