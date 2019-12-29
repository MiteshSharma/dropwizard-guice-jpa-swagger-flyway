package com.myth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class BaseModel {
    @JsonIgnore
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
