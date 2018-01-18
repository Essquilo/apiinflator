package de.ovgu.softwareproductlines.inflater;

import okhttp3.Request;

public class GetBuilder implements MethodBuilder {

    private final APIMethod method;

    GetBuilder(APIMethod method) {

        this.method = method;
    }

    @Override
    public APIMethod method() {
        return method;
    }

    @Override
    public Request buildRequest() {
        return new Request.Builder().url(method.buildUrl(null))
                .get()
                .build();
    }
}
