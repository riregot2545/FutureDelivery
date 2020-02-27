package com.nix.futuredelivery.entity.value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;

@Slf4j
public class LocationConverter implements AttributeConverter<Location, String> {
    @Override
    public String convertToDatabaseColumn(Location location) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(location);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Location convertToEntityAttribute(String s) {
        if (s == null) {
            return new Location(0D,0D);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, Location.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
