package com.mayankrastogi.cs474.hw2.annotations.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractAnnotationProcessor extends AbstractProcessor {

    private static final String DEBUG_OPTION_NAME = "AnnotationProcessor.debug";

    Elements elementUtils;
    Types typeUtils;
    boolean treatMandatoryWarningsAsErrors = false;

    private Messager messager;
    private boolean debug = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        debug = processingEnv.getOptions().containsKey(DEBUG_OPTION_NAME);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        var supportedAnnotationTypesAnnotation = this.getClass().getAnnotation(SupportedAnnotationTypes.class);

        Stream<String> superTypes = Stream.empty();
        if (supportedAnnotationTypesAnnotation != null)
            superTypes = Arrays.stream(supportedAnnotationTypesAnnotation.value());

        return Stream.concat(superTypes, getSupportedAnnotations().map(Class::getCanonicalName))
                .collect(Collectors.toUnmodifiableSet());
    }

    protected Stream<Class> getSupportedAnnotations() {
        return Stream.empty();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Stream.of(DEBUG_OPTION_NAME).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        var sourceVersionAnnotation = this.getClass().getAnnotation(SupportedSourceVersion.class);
        return sourceVersionAnnotation == null ? SourceVersion.RELEASE_11 : sourceVersionAnnotation.value();
    }

    // =================================================================================================================
    // Utility functions for extracting common information from elements
    // =================================================================================================================

    boolean elementImplementsInterface(Element element, String interfaceClassName) {
        var elementType = typeUtils.erasure(element.asType());
        var iteratorType = typeUtils.erasure(elementUtils.getTypeElement(interfaceClassName).asType());

        return typeUtils.isSubtype(elementType, iteratorType);
    }

    boolean assertElementDefinesMethod(Element element, String methodName, TypeMirror returnType, List<? extends VariableElement> parameters) {
        var methodFound = element.getEnclosedElements()
                .stream()
                .filter(e -> e instanceof ExecutableElement)
                .map(e -> (ExecutableElement) e)
                .filter(e ->
                        e.getSimpleName().toString().equals(methodName) &&
                                typeUtils.isSameType(e.getReturnType(), returnType) &&
                                e.getParameters().equals(parameters))
                .count();
        if (methodFound != 1) {
            error(String.format("%s does not define the method `%s` with return type `%s` and parameters `%s`", element, methodName, returnType, parameters), element);
            return false;
        }
        return true;
    }

    Optional<? extends TypeMirror> getTypeParameterFromImplementedInterface(Element element, String interfaceClassName) {
        return getTypeParameterFromImplementedInterface(element, elementUtils.getTypeElement(interfaceClassName).asType());
    }

    Optional<? extends TypeMirror> getTypeParameterFromImplementedInterface(Element element, TypeMirror interfaceType) {
        if (element instanceof TypeElement) {
            return ((TypeElement) element).getInterfaces()
                    .stream()
                    .filter(i -> typeUtils.isSameType(typeUtils.erasure(i), typeUtils.erasure(interfaceType)))
                    .flatMap(this::getTypeParametersFromGenericElement)
                    .findFirst();
        }
        return Optional.empty();
    }

    private Stream<? extends TypeMirror> getTypeParametersFromGenericElement(TypeMirror element) {
        if (element instanceof DeclaredType) {
            return ((DeclaredType) element).getTypeArguments().stream();
        } else {
            return Stream.empty();
        }
    }

    String getAnnotationValueAsClassName(Element element, Class<? extends Annotation> annotationClass) {
        var annotationValue = getAnnotationValueAsType(element, annotationClass);
        return annotationValue != null ? annotationValue.toString() : "";
    }

    TypeMirror getAnnotationValueAsType(Element element, Class<? extends Annotation> annotationClass) {
        var annotationMirror = getAnnotationMirror(element, annotationClass);
        if (annotationMirror.isEmpty()) return null;
        return getAnnotationValueAsType(annotationMirror.get(), "value");
    }

    // Borrowed from Dave Dopson's answer on StackOverflow: https://stackoverflow.com/a/10167558/4463881
    private TypeMirror getAnnotationValueAsType(AnnotationMirror annotationMirror, String key) {
        var annotationValue = getAnnotationValue(annotationMirror, key);
        if (annotationValue.isEmpty()) {
            return null;
        }
        return (TypeMirror) annotationValue.get().getValue();
    }

    // Borrowed from tquadrat's answer on StackOverflow: https://stackoverflow.com/a/52257877/4463881
    private Optional<? extends AnnotationMirror> getAnnotationMirror(Element element, Class<? extends Annotation> annotationClass) {
        var annotationClassName = annotationClass.getName();
        return element.getAnnotationMirrors().stream()
                .filter(m -> m.getAnnotationType().toString().equals(annotationClassName))
                .findFirst();
    }

    // Borrowed from tquadrat's answer on StackOverflow: https://stackoverflow.com/a/52257877/4463881
    private Optional<? extends AnnotationValue> getAnnotationValue(AnnotationMirror annotationMirror, String name) {
        var elementValues = elementUtils.getElementValuesWithDefaults(annotationMirror);
        return elementValues.keySet().stream()
                .filter(k -> k.getSimpleName().toString().equals(name))
                .map(elementValues::get)
                .findAny();
    }

    // =================================================================================================================
    // Helpers for printing diagnostic messages during annotation processing
    // =================================================================================================================

    void error(String message) {
        sendDiagnostic(Diagnostic.Kind.ERROR, message);
    }

    void error(String message, Element element) {
        sendDiagnostic(Diagnostic.Kind.ERROR, message, element);
    }

    void debug(String message) {
        if (debug) sendDiagnostic(Diagnostic.Kind.OTHER, "[DEBUG] " + message);
    }

    void debug(String message, Element element) {
        if (debug) sendDiagnostic(Diagnostic.Kind.OTHER, "[DEBUG]" + message, element);
    }

    void note(String message) {
        sendDiagnostic(Diagnostic.Kind.NOTE, message);
    }

    void note(String message, Element element) {
        sendDiagnostic(Diagnostic.Kind.NOTE, message, element);
    }

    void mandatoryWarning(String message) {
        if (treatMandatoryWarningsAsErrors)
            error(message);
        else
            sendDiagnostic(Diagnostic.Kind.MANDATORY_WARNING, message);
    }

    void mandatoryWarning(String message, Element element) {
        if (treatMandatoryWarningsAsErrors)
            error(message, element);
        else
            sendDiagnostic(Diagnostic.Kind.MANDATORY_WARNING, message, element);
    }

    void warning(String message) {
        sendDiagnostic(Diagnostic.Kind.WARNING, message);
    }

    void warning(String message, Element element) {
        sendDiagnostic(Diagnostic.Kind.WARNING, message, element);
    }

    private void sendDiagnostic(Diagnostic.Kind kind, String message) {
        messager.printMessage(kind, message);
    }

    private void sendDiagnostic(Diagnostic.Kind kind, String message, Element element) {
        messager.printMessage(kind, message, element);
    }
}
