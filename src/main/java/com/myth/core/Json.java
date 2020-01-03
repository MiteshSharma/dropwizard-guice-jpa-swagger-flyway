package com.myth.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import java.io.IOException;
import java.io.StringWriter;

public class Json {
    private static final ObjectMapper defaultObjectMapper = defaultMapper();

    private static ObjectMapper defaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JSR310Module());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public static JsonNode toJson(Object value) {
        try {
            return defaultObjectMapper.valueToTree(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(JsonNode jsonNode, Class<T> clazz) {
        try {
            return defaultObjectMapper.treeToValue(jsonNode, clazz);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JsonNode parse(String value) {
        try {
            return defaultObjectMapper.readTree(value);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    public static String stringify(Object value) {
        try {
            StringWriter stringWritter = new StringWriter();
            JsonGenerator jsonGenerator = (new JsonFactory(defaultObjectMapper)).createGenerator(stringWritter);
            jsonGenerator.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);

            defaultObjectMapper.writeValue(jsonGenerator, value);
            stringWritter.flush();
            return stringWritter.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
