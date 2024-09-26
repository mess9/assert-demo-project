package ru.demo.util.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        arg1.writeString(df.format(arg0));
    }
}