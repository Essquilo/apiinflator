package de.ovgu.softwareproductlines.inflater;

import okhttp3.Request;

public class PatchBuilder implements MethodBuilder {

    private final APIMethod method;

    PatchBuilder(APIMethod method){

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
