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
    private final Map<Method, APIMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    private final String baseUrl;

    private APIInflater(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Builds APIInflater backed with socket connection and method adapters.
     *
     * @param apiInterface interface representing APIInflater
     * @param <T>          interface generic
     * @return APIInflater that adapts interface methods to SocketConnection methods
     */
    @SuppressWarnings("unchecked")
    public <T> T inflate(final Class<T> apiInterface) {
        validate(apiInterface);
        return (T) Proxy.newProxyInstance(apiInterface.getClassLoader(), new Class[]{apiInterface},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        APIMethod<?> apiMethod = loadMethod(method);
                        return apiMethod.adapt(args);
                    }
                });
    }

    private APIMethod<?> loadMethod(Method method) {
        APIMethod<?> apiMethod = serviceMethodCache.get(method);
        if (apiMethod != null)
            return apiMethod;
        synchronized (this) {
            apiMethod = new APIMethod<>(okHttpClient, baseUrl, method);
            serviceMethodCache.put(method, apiMethod);
            return apiMethod;
        }
    }

    private <T> void validate(Class<T> apiInterface) {
        for (Method method : apiInterface.getMethods()) {
            if (method.getDeclaringClass() != Object.class) {
                if (!method.getReturnType().equals(Observable.class)) {
                    throw new IllegalArgumentException("Method " + method + " does not return observable");
                }
                for (Annotation[] parameterAnnotations : method.getParameterAnnotations()) {
                    boolean annotatedWithField = false;
                    boolean annotatedWithType = false;
                    for (Annotation parameterAnnotation : parameterAnnotations) {
                        if (parameterAnnotation instanceof Url) {
                            annotatedWithField = true;
                        }
                        if (parameterAnnotation.getClass().isAnnotationPresent(RequestTypeAnnotation.class)) {
                            annotatedWithType = true;
                        }
                    }
                    if (!annotatedWithField) {
                        throw new IllegalArgumentException("Parameters of " + method + " are not annotated with @Field()");
                    }
                    if (!annotatedWithType)
                        throw new IllegalArgumentException("Parameters of " + method + " are not annotated with request type annotation");
                }
            }
        }
    }

    public static class Builder {
        private String baseUrl;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public APIInflater build() {
            return new APIInflater(baseUrl);
        }
    }
}
