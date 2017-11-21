import io.reactivex.Observable;
import okhttp3.OkHttpClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ivan Prymak on 11/12/2017.
 */

public class APIInflater {
    private final String baseUrl;

    private APIInflater(String baseUrl) {
        this.baseUrl = baseUrl;
    }



    public static class Builder {
        private String baseUrl;
        private String method = "GET";

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public APIInflater build() {
            return new APIInflater(baseUrl);
        }
    }
}
