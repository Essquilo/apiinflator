package de.ovgu.softwareproductlines.inflater;

import java.lang.reflect.Method;

public class ActionAPIMethod extends BaseAPIMethod {

    public ActionAPIMethod(Method method, Object[] args, String action) {
        super(method, args);
        params.put("action", action);
    }
}
