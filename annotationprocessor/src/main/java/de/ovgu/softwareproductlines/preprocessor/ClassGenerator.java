package de.ovgu.softwareproductlines.preprocessor;

import com.google.gson.Gson;
import com.squareup.javapoet.*;
import de.ovgu.softwareproductlines.annotation.API;
import de.ovgu.softwareproductlines.annotation.auth.OAuthTokenProvider;
import de.ovgu.softwareproductlines.annotation.json.JsonAdapter;
import de.ovgu.softwareproductlines.annotation.json.JsonAdapterFactory;
import de.ovgu.softwareproductlines.annotation.json.ParseWith;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;

public class ClassGenerator {
    private final Elements elemUtils;
    private TypeElement apiRoot;
    private String baseUrl;
    private List<MethodGenerator> methodGenerators = new ArrayList<>();


    public ClassGenerator(TypeElement apiRoot, ProcessingEnvironment proccessingEnv) {
        this.apiRoot = apiRoot;
        this.elemUtils = proccessingEnv.getElementUtils();
        API apiAnnotation = apiRoot.getAnnotation(API.class);
        baseUrl = apiAnnotation.value();
        for (Element element : apiRoot.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) element;
                methodGenerators.add(new MethodGenerator(method, proccessingEnv));
            }
        }
    }

    public TypeSpec.Builder generate() {
        boolean needsOAuth = methodGenerators.stream().anyMatch(MethodGenerator::needsAuth);
        boolean needsJsonParsing = methodGenerators.stream().anyMatch(MethodGenerator::needsJsonParsing);
        TypeSpec.Builder implDescription = TypeSpec
                .classBuilder(apiRoot.getSimpleName() + "$$" + "Impl")
                .addJavadoc("Generated from $S, do not modify.\n", apiRoot.getQualifiedName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(TypeName.get(apiRoot.asType()))
                .addField(FieldSpec.builder(Gson.class, "GSON", Modifier.FINAL, Modifier.PRIVATE)
                        .initializer("new $T()", Gson.class)
                        .build())
                .addField(FieldSpec.builder(OkHttpClient.class, "OKHTTP_CLIENT", Modifier.FINAL, Modifier.PRIVATE)
                        .initializer(getOkHttpClientInitializer(needsOAuth))
                        .build());
        if (needsJsonParsing) {
            ParseWith parseWith = apiRoot.getAnnotation(ParseWith.class);
            if (parseWith == null) {
                throw new IllegalArgumentException("One of the methods of " + apiRoot.toString() + " uses JSON body parsing, but no @ParseWith annotation found.");
            }
            implDescription
                    .addField(FieldSpec.builder(JsonAdapter.class, "adapter", Modifier.PRIVATE, Modifier.FINAL)
                            .initializer("new $T().produce()", getJsonAdapterClass(parseWith))
                            .build());
        }
        if (needsOAuth) {
            implDescription
                    .addField(OAuthTokenProvider.class, "oauthTokenProvider")
                    .addMethod(MethodSpec.constructorBuilder()
                            .addParameter(OAuthTokenProvider.class, "oauthTokenProvider")
                            .addStatement("this.oauthTokenProvider = oauthTokenProvider")
                            .build());
        }
        for (MethodGenerator methodGenerator : methodGenerators) {
            implDescription.addMethod(methodGenerator.generate(baseUrl));
        }
        return implDescription;
    }

    private TypeMirror getJsonAdapterClass(ParseWith parseWith) {
        try {
            // should always throw, don't ask
            parseWith.value();
        } catch (MirroredTypeException ex) {
            return ex.getTypeMirror();
        }
        return null;
    }

    private CodeBlock getOkHttpClientInitializer(boolean needsOAuth) {
        CodeBlock.Builder builder = CodeBlock.builder();

        if (!needsOAuth) {
            builder.addStatement("new $T().build()", OkHttpClient.Builder.class);
        } else {
            TypeElement authenticatorClass = elemUtils.getTypeElement(Authenticator.class.getCanonicalName());
            ExecutableElement authenticate = (ExecutableElement) authenticatorClass.getEnclosedElements().stream()
                    .filter(element ->
                            element.getKind() == ElementKind.METHOD && element.getSimpleName().contentEquals("authenticate"))
                    .findFirst().get();
            TypeSpec authenticator = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(Authenticator.class)
                    .addMethod(
                            MethodSpec.overriding(authenticate)
                                    .beginControlFlow("if (arg1.request().header($S) != null)", "Authorization")
                                    .addStatement("return null")
                                    .endControlFlow()
                                    .addStatement("return arg1.request().newBuilder()\n" +
                                            ".header(\"Authorization\", String.format(\"Bearer %s\", oauthTokenProvider.getToken()))" +
                                            ".build()")
                                    .build()
                    )
                    .build();
            builder.addStatement("new $T().authenticator($L).build()", OkHttpClient.Builder.class, authenticator);
        }
        return builder.build();
    }
}
