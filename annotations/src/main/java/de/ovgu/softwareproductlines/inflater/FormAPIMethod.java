package de.ovgu.softwareproductlines.inflater;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import java.lang.reflect.Method;
import java.util.Map;

public class FormAPIMethod<T> extends BaseAPIMethod {
    public FormAPIMethod(Method method, Object[] args) {
        super(method, args);
    }

    @Override
    public RequestBody buildBody() {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> param: params.entrySet()) {
            builder.add(param.getKey(), param.getValue().toString());
        }
        return builder.build();
    }
}
