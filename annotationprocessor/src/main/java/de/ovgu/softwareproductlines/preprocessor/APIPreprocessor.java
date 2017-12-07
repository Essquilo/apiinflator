package de.ovgu.softwareproductlines.preprocessor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import de.ovgu.softwareproductlines.annotation.API;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("de.ovgu.softwareproductlines.annotation.API")
public class APIPreprocessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Processing started");
        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(API.class)) {

                if (element.getKind() != ElementKind.INTERFACE) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Can only be applied to class.");
                    return true;
                }
                ClassGenerator generator = new ClassGenerator((TypeElement) element, processingEnv);
                Name pkg = elements.getPackageOf(element).getQualifiedName();
                JavaFile.builder(pkg.toString(), generator.generate().build()).build().writeTo(filer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(API.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
