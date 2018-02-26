package br.com.iwstech.util.infraestructure.domain.model.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DecimalSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(final Double value, JsonGenerator gen, SerializerProvider provider)
        throws IOException, JsonProcessingException {
        if(value != null) {
            gen.writeString(value.toString());
        }
    }
}