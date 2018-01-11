package br.com.iwstech.util.infraestructure.domain.model.serializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.com.iwstech.util.infraestructure.log.Log;

public class DataDeserializer extends JsonDeserializer<Date> {

    private static SimpleDateFormat sdf;

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {

        final String value = p.getText();
        if(value != null && value.length() > 0) {
            try {
                return DataDeserializer.getParser().parse(value);
            } catch (ParseException ex) {
                Log.error(this.getClass(), "Erro processamento de data: " + value, ex);
                return null;
            }
        }
        return null;

    }

    public static DateFormat getParser() {
        if(sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        return sdf;
    }
}