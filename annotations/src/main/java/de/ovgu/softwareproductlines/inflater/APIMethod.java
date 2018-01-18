package de.ovgu.softwareproductlines.inflater;

import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

public interface APIMethod {
    Method method();
    RequestBody buildBody();
    String buildUrl(String url);
    Object adaptResult(String responseBody) throws IOException;
}
