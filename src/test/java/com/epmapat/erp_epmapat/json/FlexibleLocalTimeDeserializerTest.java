package com.epmapat.erp_epmapat.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.epmapat.erp_epmapat.modelo.Recaudaxcaja;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class FlexibleLocalTimeDeserializerTest {

   private final ObjectMapper objectMapper = new ObjectMapper()
         .registerModule(new JavaTimeModule())
         .setTimeZone(TimeZone.getTimeZone("America/Guayaquil"));

   @Test
   public void shouldDeserializePlainLocalTime() throws Exception {
      Recaudaxcaja value = objectMapper.readValue(
            "{\"horainicio\":\"10:48:02\",\"horafin\":\"11:15:00\"}",
            Recaudaxcaja.class);

      assertEquals(LocalTime.of(10, 48, 2), value.getHorainicio());
      assertEquals(LocalTime.of(11, 15, 0), value.getHorafin());
   }

   @Test
   public void shouldDeserializeIsoInstantIntoLocalTime() throws Exception {
      Recaudaxcaja value = objectMapper.readValue(
            "{\"horainicio\":\"2026-04-07T15:48:02.198Z\"}",
            Recaudaxcaja.class);

      assertEquals(LocalTime.of(10, 48, 2, 198_000_000), value.getHorainicio());
   }
}
