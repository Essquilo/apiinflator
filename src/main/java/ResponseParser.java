import java.io.IOException;

/**
 * Created by Milena on 21.11.2017.
 */
public interface ResponseParser {
    <T> T parse(String string, Class<T> clazz) throws IOException;
}
