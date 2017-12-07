package de.ovgu.softwareproductlines.annotation.type;

import de.ovgu.softwareproductlines.annotation.type.RequestTypeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@RequestTypeAnnotation
public @interface GET {
}
