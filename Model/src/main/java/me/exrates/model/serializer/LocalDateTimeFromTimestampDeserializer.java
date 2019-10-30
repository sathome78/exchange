package me.exrates.model.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;


public class LocalDateTimeFromTimestampDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        long raw = p.getValueAsLong();
        if (raw == 0) return null;
        raw *= 1000;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(raw),
                        TimeZone.getDefault().toZoneId());
    }
}
