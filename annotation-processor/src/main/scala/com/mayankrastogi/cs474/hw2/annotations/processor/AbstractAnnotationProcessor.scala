package com.mayankrastogi.cs474.hw2.annotations.processor

import java.lang.annotation.Annotation
import java.util

import javax.annotation.processing._
import javax.lang.model.SourceVersion
import javax.lang.model.`type`.TypeMirror
import javax.lang.model.element._
import javax.lang.model.util.{Elements, Types}
import javax.tools.Diagnostic

import scala.jdk.CollectionConverters._

/**
 * Provides helper methods and convenience methods that can be used by child annotation processors for performing
 * common tasks and easily print logs at the required level.
 */
abstract class AbstractAnnotationProcessor extends AbstractProcessor {

  /**
   * If the annotation processor is passed this option, irrespective of its value, debug messages are also logged during
   * the annotation processing.
   */
  private val DEBUG_OPTION_NAME = "AnnotationProcessor.debug"

  private[processor] var elementUtils: Elements = _
  private[processor] var typeUtils: Types = _
  private[processor] var treatMandatoryWarningsAsErrors = false

  private var messager: Messager = _
  private var debug = false

  override def init(processingEnv: ProcessingEnvironment): Unit = {
    super.init(processingEnv)

    messager = processingEnv.getMessager
    elementUtils = processingEnv.getElementUtils
    typeUtils = processingEnv.getTypeUtils
    debug = processingEnv.getOptions.containsKey(DEBUG_OPTION_NAME)
  }

  override def getSupportedAnnotationTypes: util.Set[String] = {
    // Get the annotations specified in @SupportedAnnotationTypes, if present on the annotation processor
    val supportedAnnotationTypesAnnotation = getClass.getAnnotation(classOf[SupportedAnnotationTypes])
    val superTypes =
      if (supportedAnnotationTypesAnnotation != null)
        supportedAnnotationTypesAnnotation.value
      else
        Array.empty[String]

    // Append this with the list of annotations specified in the `getSupportedAnnotations` method
    (getSupportedAnnotations.map(_.getCanonicalName) ++ superTypes).toSet.asJava
  }

  /**
   * Specifies the list of annotations that can be processed by this annotation processor.
   *
   * This list is merged with list of class names specified in the @[[SupportedAnnotationTypes]], if present.
   *
   * @return A list of classes of annotation types.
   */
  protected def getSupportedAnnotations: List[Class[_ <: Annotation]] = List.empty

  override def getSupportedOptions: util.Set[String] = Set(DEBUG_OPTION_NAME).asJava

  override def getSupportedSourceVersion: SourceVersion = {
    // If a value is specified in @SupportedSourceVersion, use that value, else default to SourceVersion.RELEASE_11
    val sourceVersionAnnotation = getClass.getAnnotation(classOf[SupportedSourceVersion])
    if (sourceVersionAnnotation == null) SourceVersion.RELEASE_11 else sourceVersionAnnotation.value
  }

  // ===================================================================================================================
  // Utility functions for extracting common information from elements
  // ===================================================================================================================

  /**
   * Performs an operation while treating any calls to `warning()` during this operation as if a call to `error()` was
   * made, if the optional `flag` is set to `true` (default).
   *
   * @param flag      Whether warnings should be treated as errors during the operation.
   * @param operation A block of code to execute, which may call the [[warning()]] method during its execution.
   * @tparam T The return type of the `operation`.
   * @return The value returned by the `operation` upon execution.
   */
  private[processor] def withTreatMandatoryWarningsAsErrors[T](flag: Boolean = true)(operation: => T): T = {
    // Store current state
    val previousState = treatMandatoryWarningsAsErrors

    // Change the state with new value and perform the operation
    treatMandatoryWarningsAsErrors = flag
    val result = operation

    // Restore the state
    treatMandatoryWarningsAsErrors = previousState
    result
  }

  /**
   * Extracts the `value()` property as a [[String]] from the annotation of the specified type, applied on the supplied
   * `element`.
   *
   * @param element         The [[Element]] that is annotated with the annotation of `annotationClass`.
   * @param annotationClass The [[Class]] of the annotation whose `value()` will be retuned.
   * @return The value of the `value()` property as a [[String]].
   */
  private[processor] def getAnnotationValueAsClassName(element: Element, annotationClass: Class[_ <: Annotation]): String = {
    val annotationValue = getAnnotationValueAsType(element, annotationClass)

    if (annotationValue != null) annotationValue.toString else ""
  }

  /**
   * Extracts the `value()` property as a [[TypeMirror]] from the annotation of the specified type, applied on the
   * supplied `element`.
   *
   * @param element         The [[Element]] that is annotated with the annotation of `annotationClass`.
   * @param annotationClass The [[Class]] of the annotation whose `value()` will be retuned.
   * @return The [[TypeMirror]] corresponding to the value of the `value()` on the specified annotation.
   */
  private[processor] def getAnnotationValueAsType(element: Element, annotationClass: Class[_ <: Annotation]): TypeMirror = {
    val annotationMirror = getAnnotationMirror(element, annotationClass)

    if (annotationMirror.isDefined) getAnnotationValueAsType(annotationMirror.get, "value") else null
  }

  /**
   * Extracts a value as a [[TypeMirror]] from the specified [[AnnotationMirror]].
   *
   * Borrowed from Dave Dopson's answer on StackOverflow: [[https://stackoverflow.com/a/10167558/4463881]].
   *
   * @param annotationMirror The annotation object from which the the value is to be extracted.
   * @param key              The name of the property, defined in the annotation, which contains the required value.
   * @return The value stored in the given `key` in the supplied `annotationMirror` as a [[TypeMirror]].
   */
  private def getAnnotationValueAsType(annotationMirror: AnnotationMirror, key: String): TypeMirror = {
    val annotationValue = getAnnotationValue(annotationMirror, key)

    if (annotationValue.isDefined) annotationValue.get.getValue.asInstanceOf[TypeMirror] else null
  }

  /**
   * Extracts a value from the specified property `name` defined in the supplied [[AnnotationMirror]].
   *
   * @param annotationMirror The annotation object from which the the value is to be extracted.
   * @param name             The name of the property, defined in the annotation, which contains the required value.
   * @return The value stored in the given `key` in the supplied `annotationMirror`.
   */
  private def getAnnotationValue(annotationMirror: AnnotationMirror, name: String): Option[_ <: AnnotationValue] = {
    val elementValues =
      elementUtils.getElementValuesWithDefaults(annotationMirror)
        .asScala
        .toMap[ExecutableElement, AnnotationValue]

    elementValues
      .keySet
      .filter(_.getSimpleName.toString == name)
      .map(elementValues.get)
      .head
  }

  /**
   * Gets the instance of the given `annotationClass` which is applied on the specified `element`.
   *
   * Borrowed from tquadrat's answer on StackOverflow: [[https://stackoverflow.com/a/52257877/4463881]].
   *
   * @param element         The [[Element]] that is annotated with the annotation of `annotationClass`.
   * @param annotationClass The [[Class]] of the annotation which is applied on the given `element`.
   * @return The instance of the `annotationClass` applied on the specified `element`.
   */
  private def getAnnotationMirror(element: Element, annotationClass: Class[_ <: Annotation]): Option[_ <: AnnotationMirror] = {
    element
      .getAnnotationMirrors.asScala
      .find(_.getAnnotationType.toString == annotationClass.getName)
  }

  // ===================================================================================================================
  // Helpers for printing diagnostic messages during annotation processing
  // ===================================================================================================================

  private[processor] def debug(message: String, element: Element = null): Unit = {
    if (debug) sendDiagnostic(Diagnostic.Kind.OTHER, "[DEBUG]" + message, element)
  }

  private[processor] def note(message: String, element: Element = null): Unit = {
    sendDiagnostic(Diagnostic.Kind.NOTE, message, element)
  }

  private[processor] def warning(message: String, element: Element): Unit = {
    if (treatMandatoryWarningsAsErrors) error(message, element)
    else sendDiagnostic(Diagnostic.Kind.MANDATORY_WARNING, message, element)
  }

  private[processor] def error(message: String, element: Element = null): Unit = {
    sendDiagnostic(Diagnostic.Kind.ERROR, message, element)
  }

  private def sendDiagnostic(kind: Diagnostic.Kind, message: String, element: Element): Unit = {
    if (element == null)
      messager.printMessage(kind, message)
    else
      messager.printMessage(kind, message, element)
  }
}
