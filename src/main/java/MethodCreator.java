import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.*;
import okio.BufferedSink;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.sun.javafx.fxml.expression.Expression.add;

/**
 * Created by Milena on 19.11.2017.
 */
enum RequestType {
    GET, POST, PUT, PATCH
}

enum Authentication {SESSION, OAUTH}


enum ParametersEncoding {FORM, PLAINJSON}

enum HttpImplementation {BASICHTTP, OKHTTP}

public class MethodCreator {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String baseUrl;
    private RequestType requestType = RequestType.GET;
    private Authentication authentication;
    private String sessionId;
    private ParametersEncoding parametersEncoding = ParametersEncoding.PLAINJSON;
    private Map<String, String> requestParameters = new HashMap<String, String>();
    private HttpImplementation httpImplementation = HttpImplementation.OKHTTP;


    public MethodCreator baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public MethodCreator requestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public MethodCreator authentication(Authentication authentication, String sessionId) {
        this.authentication = authentication;
        this.sessionId = sessionId;
        return this;
    }


    public MethodCreator contentBasedAction(String contentBasedActionKey, String contentBasedActionTitle) {
        this.requestParameters.put(contentBasedActionKey, contentBasedActionTitle);
        return this;
    }

    public MethodCreator parametersEncoding(ParametersEncoding parametersEncoding) {
        this.parametersEncoding = parametersEncoding;
        return this;
    }

    public MethodCreator requestParameters(String key, String value) {
        this.requestParameters.put(key, value);
        return this;
    }

    public MethodCreator httpImplementation(HttpImplementation httpImplementation) {
        this.httpImplementation = httpImplementation;
        return this;
    }

    public Observable<Document> buildXML() {

        return buildRaw().map(response -> {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            if (response.body() == null){
                throw new EmptyBodyException();
            }else{
                return builder.parse(new InputSource(new StringReader(response.body().string())));
            }
        });
    }

    public <T> Observable<T> buildJson(Class <T> responseClass, ResponseParser responseParser) {
        return buildRaw().map(response -> {
            if (response.body() == null){
                throw new EmptyBodyException();
            }else{
                return responseParser.parse(response.body().string(), responseClass);
            }
        });
    }

    public Observable<Response> buildRaw() {
        return Observable.fromCallable(new Callable<Response>() {
            public Response call() throws Exception {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder request = new Request.Builder();
                RequestBody requestBody = null;

                if (requestType == RequestType.GET) {
                    HttpUrl.Builder httpBuider = HttpUrl.parse(baseUrl).newBuilder();
                    if (requestParameters != null) {
                        for (Map.Entry<String, String> param : requestParameters.entrySet()) {
                            httpBuider.addQueryParameter(param.getKey(), param.getValue());
                        }
                    }
                    request.url(httpBuider.build());

                } else if (parametersEncoding == ParametersEncoding.PLAINJSON) {
                    request.url(baseUrl);
                    Gson gson = new Gson();
                    String json = gson.toJson(requestParameters);
                    requestBody = RequestBody.create(JSON, json);
                } else {
                    request.url(baseUrl);
                    FormBody.Builder requestBodyBuilder = new FormBody.Builder();
                    for (Map.Entry<String, String> e : requestParameters.entrySet()) {
                        String key = e.getKey();
                        String value = e.getValue();
                        requestBodyBuilder.add(key, value);
                    }
                    requestBody = requestBodyBuilder.build();
                }
                switch (requestType) {
                    case GET:
                        request.get();
                        break;
                    case POST:
                        request.post(requestBody);
                        break;
                    case PUT:
                        request.put(requestBody);
                        break;
                    case PATCH:
                        request.patch(requestBody);
                        break;
                }
                return okHttpClient.newCall(request.build()).execute();
            }
        });
    }
}