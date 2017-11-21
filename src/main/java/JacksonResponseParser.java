import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Milena on 21.11.2017.
 */
public class JacksonResponseParser implements ResponseParser {
    private ObjectMapper jacksonMapper;

    public JacksonResponseParser(ObjectMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public <T> T parse(String string, Class<T> clazz) throws IOException {
        return jacksonMapper.readValue(string, clazz);
    }
}
