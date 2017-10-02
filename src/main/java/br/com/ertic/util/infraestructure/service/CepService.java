package br.com.ertic.util.infraestructure.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.ertic.util.infraestructure.dto.CepDTO;

@Service
public class CepService {

    public CepDTO find(String zipcode) {

        RestTemplate rest = new RestTemplate();
        String result = rest.getForObject("http://www.consultaenderecos.com.br/busca-cep/" + zipcode, String.class);

        CepDTO saida = parseHtml(zipcode, result);

        if(saida == null) {
            return null;
        }

        saida.setCep(zipcode);
        return saida;
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
