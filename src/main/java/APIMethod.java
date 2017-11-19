import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Ivan Prymak on 3/1/2017.
 * Method adapter that resolves method calls to Observables.
 */

@SuppressWarnings("WeakerAccess")
public class APIMethod<O> {
    private final OkHttpClient client;
    private Method method;
    private RequestType requestMethod;

    private Type[] parameterTypes;
    private String[] fieldNames;
    private String action;
    private String baseUrl;
    private String relativeUrl;
    private final ParameterizedType returnType;

    public APIMethod(OkHttpClient client, String baseUrl, Method method) {
        this.client = client;
        this.method = method;
        this.parameterTypes = method.getParameterTypes();

        Type genericReturnType = method.getGenericReturnType();
        if (hasUnresolvableType(genericReturnType)) {
            throw new IllegalArgumentException(
                    String.format("Method return type must not include a type variable or wildcard: %s", genericReturnType));
        }
        if (!(genericReturnType instanceof ParameterizedType))
            throw new IllegalArgumentException("Method " + method + " is not of parametrised Observable type");
        returnType = (ParameterizedType) genericReturnType;

        Annotation[] methodAnnotations = method.getAnnotations();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        this.fieldNames = new String[parameterTypes.length];

        for (Annotation annotation : methodAnnotations) {
           // get all method annotations
            if (annotation.getClass().isAnnotationPresent(RequestTypeAnnotation.class)) {
                if (annotation instanceof GET) {
                    requestMethod = RequestType.GET;
                } else if (annotation instanceof PUT) {
                    requestMethod = RequestType.PUT;
                } else if (annotation instanceof POST) {
                    requestMethod = RequestType.POST;
                } else {
                    requestMethod = RequestType.PUT;
                }
            }
            if (annotation instanceof Action) {
                action = ((Action) annotation).value();
            } else if (annotation instanceof Url) {
                relativeUrl = ((Url) annotation).value();
            }
        }

        for (int i = 0; i < fieldNames.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Param) {
                    relativeUrl = relativeUrl.replace("{" + ((Param) annotation).value() + "}", canonicalizeForPath(value, encoded));

                }
            }
        }
    }

    /**
     * Adapts the method call to actual actions based on parsed method annotations.
     *
     * @param args arguments to use in actual call to socket
     * @return {@link Observable} that can be used to listen to single result
     */
    Observable<O> adapt(Object[] args) {

        return null;
    }

    private Request generateRequest() {
        return new Request.Builder()
                .url()
                .method(requestMethod.name(), null)
                .build();
    }

    static boolean hasUnresolvableType(Type type) {
        if (type instanceof Class<?>) {
            return false;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                if (hasUnresolvableType(typeArgument)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof GenericArrayType) {
            return hasUnresolvableType(((GenericArrayType) type).getGenericComponentType());
        }
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof WildcardType) {
            return true;
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + className);
    }
}
