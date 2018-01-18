package de.ovgu.softwareproductlines.inflater;

import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

public class UrlAPIMethod implements APIMethod{

    private final APIMethod delegate;
    private final String url;

    public UrlAPIMethod(APIMethod delegate, String url) {
        this.delegate = delegate;
        this.url = url;
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
        return delegate.buildUrl(this.url);
    }

    @Override
    public Object adaptResult(String responseBody) throws IOException {
        return null;
    }
}
