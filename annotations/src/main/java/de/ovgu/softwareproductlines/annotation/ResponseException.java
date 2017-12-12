package de.ovgu.softwareproductlines.annotation;

public class ResponseException extends Exception {
    private int code;

    public ResponseException(int code){
        super("Server returned an error code " + code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
