package com.epmapat.erp_epmapat.json;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class FlexibleDateDeserializer extends JsonDeserializer<Date> {

   private static final DateTimeFormatter SPACE_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final DateTimeFormatter SPACE_DATE_TIME_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

   @Override
   public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
      String value = parser.getValueAsString();

      if (value == null || value.isBlank()) {
         return null;
      }

      String trimmedValue = value.trim();
      ZoneId zoneId = resolveZoneId(context);

      try {
         return Date.from(Instant.parse(trimmedValue));
      } catch (DateTimeParseException ex) {
         // Continue with other supported formats.
      }

      try {
         return Date.from(OffsetDateTime.parse(trimmedValue).toInstant());
      } catch (DateTimeParseException ex) {
         // Continue with other supported formats.
      }

      try {
         return Date.from(LocalDateTime.parse(trimmedValue).atZone(zoneId).toInstant());
      } catch (DateTimeParseException ex) {
         // Continue with other supported formats.
      }

      try {
         return Date.from(LocalDateTime.parse(trimmedValue, SPACE_DATE_TIME).atZone(zoneId).toInstant());
      } catch (DateTimeParseException ex) {
         // Continue with other supported formats.
      }

      try {
         return Date.from(LocalDateTime.parse(trimmedValue, SPACE_DATE_TIME_MILLIS).atZone(zoneId).toInstant());
      } catch (DateTimeParseException ex) {
         // Continue with date-only format.
      }

      try {
         return Date.from(LocalDate.parse(trimmedValue).atStartOfDay(zoneId).toInstant());
      } catch (DateTimeParseException ex) {
         throw InvalidFormatException.from(
               parser,
               "No se pudo convertir el valor a Date. Formatos soportados: ISO-8601, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd HH:mm:ss.SSS o yyyy-MM-dd.",
               trimmedValue,
               Date.class);
      }
   }

   private ZoneId resolveZoneId(DeserializationContext context) {
      TimeZone timeZone = context.getTimeZone();
      return timeZone != null ? timeZone.toZoneId() : ZoneId.systemDefault();
   }
}
