import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Ivan Prymak on 11/13/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String value();
}
