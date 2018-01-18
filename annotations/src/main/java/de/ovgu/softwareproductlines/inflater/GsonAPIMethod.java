package de.ovgu.softwareproductlines.inflater;

import com.google.gson.Gson;
import okhttp3.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Observable;

public class GsonAPIMethod implements APIMethod {
    private final APIMethod delegate;
    private final Type responseType;

    public GsonAPIMethod(APIMethod delegate) {
       this.delegate = delegate;
       Type type = method().getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            if (pt.getActualTypeArguments() != null && pt.getActualTypeArguments().length == 1) {
                responseType = pt.getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("Return type of " + method() + " is not a subtype of " + Observable.class.getName());
            }
        } else {
            throw new IllegalArgumentException("Return type of " + method() + " is not a subtype of " + Observable.class.getName());
        }
    }

    @Override
    public Method method() {
        return delegate.method();
    }

    @Override
    public RequestBody buildBody() {
        return delegate.buildBody();
    }

    @Override
    public String buildUrl(String url) {
        return delegate.buildUrl(url);
    }

    @Override
    public Object adaptResult(String responseBody) {
        return new Gson().fromJson(responseBody, responseType);
    }
}
