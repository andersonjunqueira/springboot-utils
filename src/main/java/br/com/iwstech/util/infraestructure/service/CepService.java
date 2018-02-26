package br.com.iwstech.util.infraestructure.service;

import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.iwstech.util.infraestructure.dto.CepDTO;
import br.com.iwstech.util.infraestructure.dto.ViaCepDTO;
import br.com.iwstech.util.infraestructure.log.Log;

@Service
public class CepService {

    public CepDTO find(String zipcode) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            ViaCepDTO t = mapper.readValue(new URL("https://viacep.com.br/ws/" + zipcode + "/json/"), ViaCepDTO.class);
            return parse(t);
        } catch(IOException ex) {
            Log.error(CepService.class, ex);
            return null;
        }
    }

    private CepDTO parse(ViaCepDTO cep) {
        if(cep != null) {
            CepDTO dto = new CepDTO();
            dto.setBairro(cep.getBairro());
            dto.setCep(cep.getCep());
            dto.setCidade(cep.getLocalidade());
            dto.setComplemento(cep.getComplemento());
            dto.setIbge(cep.getIbge());
            dto.setLogradouro(cep.getLogradouro());
            dto.setUf(cep.getUf());
            return dto;
        }
        return null;
    }

}
