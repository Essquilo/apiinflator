package de.ovgu.softwareproductlines.inflater;

import de.ovgu.softwareproductlines.annotation.Param;
import de.ovgu.softwareproductlines.annotation.Path;
import okhttp3.RequestBody;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BaseAPIMethod implements APIMethod {
    private final Method method;
    protected Map<String, Object> params = new HashMap<>();
    protected Map<String, Object> paths = new HashMap<>();

    public BaseAPIMethod(Method method, Object[] args) {
        this.method = method;
        Annotation[][] parameterAnnotations = this.method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (parameterAnnotations[i] == null || parameterAnnotations[i].length > 0) {
                for (Annotation a : parameterAnnotations[i]) {
                    if (a instanceof Param) {
                        params.put(((Param) a).value(), args[i]);
                        break;
                    } else if (a instanceof Path) {
                        paths.put(((Path) a).value(), args[i]);
                    }
                }
            }
        }
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public RequestBody buildBody() {
        throw new NotImplementedException();
    }

    @Override
    public String buildUrl(String url) {
        throw new NotImplementedException();
    }

    @Override
    public Object adaptResult(String response) {
        throw new NotImplementedException();
    }
}
