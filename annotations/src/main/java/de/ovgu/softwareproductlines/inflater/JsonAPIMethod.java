package de.ovgu.softwareproductlines.inflater;

import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

public class JsonAPIMethod<T> implements APIMethod {
    private final APIMethod delegate;

    public JsonAPIMethod(APIMethod delegate) {
        this.delegate = delegate;
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
    public Object adaptResult(String responseBody) throws IOException {
        return delegate.adaptResult(responseBody);
    }
}
