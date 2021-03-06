package br.com.iwstech.util.infraestructure.domain.model.serializer;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(final Date value, JsonGenerator gen, SerializerProvider provider)
        throws IOException, JsonProcessingException {

        if(value != null) {
            gen.writeString(TimeDeserializer.getParser().format(value));
        }

    }
}