package meme_db;


import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;



public class TagSetConverter implements AttributeConverter<Set<String>, String> {


    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(Set<String> tags) {
        try {
            return mapper.writeValueAsString(tags);
        }

        catch (Exception e) {
            System.out.println("No tags found");
            return "[]";
        }

    }

    @Override
    public Set<String> convertToEntityAttribute(String tagsJson) {
        try {
            return mapper.readValue(tagsJson, new TypeReference<Set<String>>() {});
        }

        catch (Exception e) {
            System.out.println("No tags found");
            return new HashSet<>();
        }
    }
    
}
