package de.ovgu.softwareproductlines.inflater;

import okhttp3.Request;

public class PutBuilder implements MethodBuilder {

    private final APIMethod method;

    PutBuilder(APIMethod method){
        this.method = method;
    }

    @Override
    public APIMethod method() {
        return method;
    }

    @Override
    public Request buildRequest() {
        return null;
    }
}
