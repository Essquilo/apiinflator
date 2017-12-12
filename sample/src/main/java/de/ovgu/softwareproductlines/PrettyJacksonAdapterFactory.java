package de.ovgu.softwareproductlines;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ovgu.softwareproductlines.annotation.json.JacksonAdapterFactory;

public class PrettyJacksonAdapterFactory extends JacksonAdapterFactory {
    @Override
    protected ObjectMapper getMapper() {
        return super.getMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    }
}
