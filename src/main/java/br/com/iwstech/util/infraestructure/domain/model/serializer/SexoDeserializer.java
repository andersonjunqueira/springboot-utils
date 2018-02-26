package br.com.iwstech.util.infraestructure.domain.model.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.com.iwstech.util.infraestructure.domain.model.Sexo;

public class SexoDeserializer extends JsonDeserializer<Sexo> {

    @Override
    public Sexo deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {

        final String value = p.getText();
        if(value != null) {
            if(value.equals("M")) {
                return Sexo.M;
            } else if(value.equals("F")) {
                return Sexo.F;
            }
        }

        return null;

    }
}