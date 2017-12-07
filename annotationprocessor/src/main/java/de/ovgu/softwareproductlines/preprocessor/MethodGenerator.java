package de.ovgu.softwareproductlines.preprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.squareup.javapoet.*;
import de.ovgu.softwareproductlines.annotation.*;
import de.ovgu.softwareproductlines.annotation.auth.AuthToken;
import de.ovgu.softwareproductlines.annotation.auth.OAuth;
import de.ovgu.softwareproductlines.annotation.response.Jackson;
import de.ovgu.softwareproductlines.annotation.type.GET;
import de.ovgu.softwareproductlines.annotation.type.PATCH;
import de.ovgu.softwareproductlines.annotation.type.POST;
import de.ovgu.softwareproductlines.annotation.type.PUT;
import io.reactivex.Observable;
import okhttp3.*;
import org.w3c.dom.Document;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MethodGenerator {
    private ExecutableElement method;
    private ProcessingEnvironment proccessingEnv;
    private Types typeUtils;
    private Elements elemUtils;
    String url;

    public MethodGenerator(ExecutableElement method, ProcessingEnvironment proccessingEnv) {
        this.method = method;
        this.proccessingEnv = proccessingEnv;
        typeUtils = proccessingEnv.getTypeUtils();
        elemUtils = proccessingEnv.getElementUtils();
    }

    public MethodSpec generate(String baseUrl) {
        TypeMirror observableType = elemUtils.getTypeElement(Observable.class.getCanonicalName()).asType();
        TypeMirror returnGenericType = typeUtils.erasure(method.getReturnType());
        if (!typeUtils.isAssignable(observableType, returnGenericType))
            throw new IllegalArgumentException("Return type of a method " + method.toString() + "is not io.reactivex.Observable");
        Url url = method.getAnnotation(Url.class);
        if (url == null)
            throw new IllegalArgumentException("Method " + method.toString() + " is not annotated with @Url.");
        this.url = url.value();

        TypeMirror modelType = ((DeclaredType) method.getReturnType()).getTypeArguments().get(0);
        MethodSpec.Builder call = MethodSpec.methodBuilder("call")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(modelType))
                .addException(ClassName.get(Exception.class))
                .addStatement("String url = $S", baseUrl + url.value())
                .addCode(fillPath())
                .addStatement("$T requestBuilder = new $T()", Request.Builder.class, Request.Builder.class)
                .addStatement("requestBuilder.url(url)");

        TypeSpec.Builder callable = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Callable.class), TypeName.get(modelType)));

        if ((method.getAnnotation(Form.class) != null || method.getAnnotation(JSON.class) != null)
                && method.getAnnotation(GET.class) != null) {
            throw new IllegalArgumentException("Method " + call.toString() + ": GET requests can't have body.");
        }

        if (method.getAnnotation(GET.class) == null) {
            call.addStatement("$T requestBody", RequestBody.class);
            if (method.getAnnotation(Form.class) != null) {
                call.addCode(buildFormBody());
            } else {
                call.addCode(buildJsonBody());
            }
        }

        if (method.getAnnotation(GET.class) != null) {
            call.addStatement("requestBuilder.get()");
        } else if (method.getAnnotation(POST.class) != null) {
            call.addStatement("requestBuilder.post(requestBody)");
        } else if (method.getAnnotation(PUT.class) != null) {
            call.addStatement("requestBuilder.put(requestBody)");
        } else if (method.getAnnotation(PATCH.class) != null) {
            call.addStatement("requestBuilder.patch(requestBody)");
        }

        if (typeUtils.isAssignable(modelType, elemUtils.getTypeElement(Response.class.getCanonicalName()).asType())) {
            call.addStatement("return OKHTTP_CLIENT.newCall(requestBuilder.build()).execute()");
        } else if (typeUtils.isAssignable(modelType, elemUtils.getTypeElement(Document.class.getCanonicalName()).asType())) {
            call.addStatement("$T response = OKHTTP_CLIENT.newCall(requestBuilder.build()).execute()", Response.class)
                    .beginControlFlow("if (response.body() == null)")
                    .addStatement("throw new $T($S)", EmptyBodyException.class, method.getSimpleName())
                    .endControlFlow()
                    .beginControlFlow("else")
                    .addStatement("$T builder = $T.newInstance().newDocumentBuilder()", DocumentBuilder.class, DocumentBuilderFactory.class)
                    .addStatement("return builder.parse(response.body().source().inputStream())")
                    .endControlFlow();
        } else if (method.getAnnotation(Jackson.class) != null) {
            call.addStatement("$T response = OKHTTP_CLIENT.newCall(requestBuilder.build()).execute()", Response.class)
                    .beginControlFlow("if (response.body() == null)")
                    .addStatement("throw new $T($S)", EmptyBodyException.class, method.getSimpleName())
                    .endControlFlow()
                    .beginControlFlow("else")
                    .addStatement("$T jsonMapper = new $T()", ObjectMapper.class, ObjectMapper.class)
                    .addStatement("return jsonMapper.readValue(response.body().string(), $L.class)", modelType)
                    .endControlFlow();
        } else {
            call.addStatement("$T response = OKHTTP_CLIENT.newCall(requestBuilder.build()).execute()", Response.class)
                    .beginControlFlow("if (response.body() == null)")
                    .addStatement("throw new $T($S)", EmptyBodyException.class, method.getSimpleName())
                    .endControlFlow()
                    .beginControlFlow("else")
                    .addStatement("$T jsonMapper = new $T()", Gson.class, Gson.class)
                    .addStatement("return jsonMapper.fromJson(response.body().string(), $L.class)", modelType)
                    .endControlFlow();
        }

        callable.addMethod(call.build());
        MethodSpec.Builder observable = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(method.getReturnType()))
                .addParameters(generateParameters())
                .addStatement("return $T.fromCallable($L)", ClassName.get(typeUtils.erasure(observableType)), callable.build());

        return observable.build();
    }

    private Iterable<ParameterSpec> generateParameters() {
        return method.getParameters().stream().map(variableElem ->
                ParameterSpec.builder(
                        TypeName.get(variableElem.asType()),
                        variableElem.getSimpleName().toString(),
                        Modifier.FINAL
                )
                        .build())
                .collect(Collectors.toList());
    }

    private CodeBlock fillPath() {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (VariableElement variableElem : method.getParameters()) {
            Path getParam = variableElem.getAnnotation(Path.class);
            if (getParam != null) {
                builder.addStatement("url = url.replace($S, $S)", "{" + getParam.value() + "}", variableElem.getSimpleName());
            }
        }
        return builder.build();
    }

    private CodeBlock buildFormBody() {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T requestBodyBuilder = new $T()", FormBody.Builder.class, FormBody.Builder.class);
        for (VariableElement variableElem : method.getParameters()) {
            Param bodyParam = variableElem.getAnnotation(Param.class);
            if (bodyParam != null) {
                builder.addStatement("requestBodyBuilder.add($S, GSON.toJson($L, $T.class))", bodyParam.value(), variableElem.getSimpleName(), TypeName.get(variableElem.asType()));
            } else {
                AuthToken token = variableElem.getAnnotation(AuthToken.class);
                if (token != null) {
                    builder.addStatement("requestBodyBuilder.add($S, $L)", token.nameKey(), variableElem.getSimpleName());
                }
            }
        }
        builder.addStatement("requestBody = requestBodyBuilder.build()");
        return builder.build();
    }

    private CodeBlock buildJsonBody() {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.addStatement("$T<String, Object> parameters = new $T<>()", Map.class, HashMap.class);
        for (VariableElement variableElem : method.getParameters()) {
            Param bodyParam = variableElem.getAnnotation(Param.class);
            if (bodyParam != null) {
                builder.addStatement("parameters.put($S, $L)", bodyParam.value(), variableElem.getSimpleName());
            } else {
                AuthToken token = variableElem.getAnnotation(AuthToken.class);
                if (token != null) {
                    builder.addStatement("parameters.put($S, $L)", token.nameKey(), variableElem.getSimpleName());
                }
            }
        }
        builder.addStatement("String body = GSON.toJson(parameters)")
                .addStatement("requestBody = $T.create($T.parse(\"application/response; charset=utf-8\"), body)", RequestBody.class, MediaType.class);
        return builder.build();
    }

    public boolean needsAuth() {
        return method.getAnnotation(OAuth.class) != null;
    }
}
