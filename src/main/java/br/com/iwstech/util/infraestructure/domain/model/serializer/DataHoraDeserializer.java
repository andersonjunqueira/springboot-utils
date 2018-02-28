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

public class DataHoraDeserializer extends JsonDeserializer<Date> {

    private static SimpleDateFormat sdfDT;
    private static SimpleDateFormat sdfD;

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {

        final String value = p.getText();
        if(value != null && value.length() > 0) {
            try {
                return DataHoraDeserializer.getDateTimeParser().parse(value);

            } catch (ParseException ex) {

                Log.error(this.getClass(), "Erro processamento de data: " + value, ex);

                try {
                    return DataHoraDeserializer.getDateParser().parse(value);
                } catch (ParseException ex1) {
                    Log.error(this.getClass(), "Erro processamento de data: " + value, ex1);
                    return null;
                }

            }
        }
        return null;

    }

    public static DateFormat getDateTimeParser() {
        if(sdfDT == null) {
            sdfDT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        return sdfDT;
    }

    public static DateFormat getDateParser() {
        if(sdfD == null) {
            sdfD = new SimpleDateFormat("yyyy-MM-dd");
        }
        return sdfD;
    }
}