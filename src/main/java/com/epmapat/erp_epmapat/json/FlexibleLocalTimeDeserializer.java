package com.epmapat.erp_epmapat.json;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class FlexibleLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

   @Override
   public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      String value = parser.getValueAsString();

      if (value == null || value.isBlank()) {
         return null;
      }

      try {
         return LocalTime.parse(value);
      } catch (DateTimeParseException ex) {
         // Continue with broader ISO date-time formats.
      }

      try {
         return OffsetDateTime.parse(value).toLocalTime();
      } catch (DateTimeParseException ex) {
         // Continue with other supported formats.
      }

      try {
         TimeZone timeZone = context.getTimeZone();
         ZoneId zoneId = timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault();
         return Instant.parse(value).atZone(zoneId).toLocalTime();
      } catch (DateTimeParseException ex) {
         // Continue with local date-time format.
      }

      try {
         return LocalDateTime.parse(value).toLocalTime();
      } catch (DateTimeParseException ex) {
         throw InvalidFormatException.from(
               parser,
               "No se pudo convertir el valor a LocalTime. Formatos soportados: HH:mm[:ss[.SSS]] o fecha ISO.",
               value,
               LocalTime.class);
      }
   }
}
