package de.ovgu.softwareproductlines.annotation.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface AuthToken {
    String nameKey() default "token";
    String dataKey() default "data";
}
