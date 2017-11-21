import com.google.gson.Gson;

/**
 * Created by Milena on 21.11.2017.
 */
public class GsonResponseParser implements ResponseParser {
    private Gson gson;

    public GsonResponseParser(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T> T parse(String string, Class<T> clazz) {
        return gson.fromJson(string, clazz);
    }
}
