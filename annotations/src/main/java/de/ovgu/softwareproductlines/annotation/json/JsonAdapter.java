package de.ovgu.softwareproductlines.annotation.json;

import java.io.IOException;
import java.lang.reflect.Type;

public interface JsonAdapter {
    <T> T adapt(String json, Type type) throws IOException;
}
