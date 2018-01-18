package de.ovgu.softwareproductlines.inflater;

import okhttp3.Request;

public interface MethodBuilder {
    APIMethod method();
    Request buildRequest();
}
