package br.com.iwstech.util.infraestructure.domain.model.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.com.iwstech.util.infraestructure.domain.model.SituacaoAtivo;

public class SituacaoAtivoDeserializer extends JsonDeserializer<SituacaoAtivo> {

    @Override
    public SituacaoAtivo deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {

        final String value = p.getText();
        if(value != null) {
            if(value.equals("A")) {
                return SituacaoAtivo.A;
            } else if(value.equals("I")) {
                return SituacaoAtivo.I;
            }
        }

        return null;

    }
}