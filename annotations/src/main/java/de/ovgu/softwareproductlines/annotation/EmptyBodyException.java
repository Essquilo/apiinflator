package de.ovgu.softwareproductlines.annotation;

public class EmptyBodyException extends Exception{
    public EmptyBodyException(String methodName) {
        super("Body was empty when processing the result from " + methodName);
    }
}
