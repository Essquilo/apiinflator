package de.ovgu.softwareproductlines.annotation.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;

public class JacksonAdapterFactory implements JsonAdapterFactory{
    @Override
    public JsonAdapter produce() {
        return new JsonAdapter() {
            private ObjectMapper objectMapper = getMapper();
            @Override
            public <T> T adapt(String json, Type type) throws IOException {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructType(type));
            }
        };
    }

    /**
     * Override to customize the Gson
     * @return Gson that will be used to parse the results
     */
    protected ObjectMapper getMapper(){
        return new ObjectMapper();
    }
}
