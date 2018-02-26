package br.com.iwstech.util.infraestructure.domain.model.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.com.iwstech.util.infraestructure.domain.model.SituacaoAtivo;

public class SituacaoAtivoSerializer extends JsonSerializer<SituacaoAtivo> {

    @Override
    public void serialize(final SituacaoAtivo value, JsonGenerator gen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
        if(value != null) {
            gen.writeString(value.getSigla());
        }
    }
}