package de.ovgu.softwareproductlines.annotation.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

public class GsonAdapterFactory implements JsonAdapterFactory{
    @Override
    public JsonAdapter produce() {
        return new JsonAdapter() {
            private Gson gson = getGson();
            @Override
            public <T> T adapt(String json, Type type) throws JsonSyntaxException {
                return gson.fromJson(json, type);
            }
        };
    }

    /**
     * Override to customize the Gson
     * @return Gson that will be used to parse the results
     */
    protected Gson getGson(){
        return new Gson();
    }
}
