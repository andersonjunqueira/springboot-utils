package br.com.ertic.util.infraestructure.service;

import java.io.IOException;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ertic.util.infraestructure.dto.CepDTO;
import br.com.ertic.util.infraestructure.log.Log;

@Service
public class CepService {

    public CepDTO find(String zipcode) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            CepDTO saida = mapper.readValue(new URL("https://viacep.com.br/ws/" + zipcode + "/json/"), CepDTO.class);
            return saida;
        } catch(IOException ex) {
            Log.error(CepService.class, ex);
            return null;
        }
    }

    private CepDTO parseHtml(String cep, String html) {

        CepDTO dto = new CepDTO();

        String ref = "<td>" + cep + "</td>";
        int refPos = html.indexOf(ref);
        if(refPos > -1) {

            String data = html.substring(refPos + ref.length());
            data = data.substring(0, data.indexOf("</tr>"));
            data = data.trim().replaceAll("<td>", "");
            String[] tokens = data.split("</td>");

            dto.setLogradouro(tokens[0].trim());
            dto.setBairro(tokens[1].trim());
            dto.setCidade(tokens[2].trim());
            dto.setUf(tokens[3].trim());
            return dto;
        }

        return null;

    }

}
