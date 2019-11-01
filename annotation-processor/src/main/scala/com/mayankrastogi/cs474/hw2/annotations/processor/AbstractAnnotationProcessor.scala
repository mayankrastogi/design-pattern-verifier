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

abstract class AbstractAnnotationProcessor extends AbstractProcessor {

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
    val supportedAnnotationTypesAnnotation = getClass.getAnnotation(classOf[SupportedAnnotationTypes])

    val superTypes =
      if (supportedAnnotationTypesAnnotation != null)
        supportedAnnotationTypesAnnotation.value
      else
        Array.empty[String]

    (getSupportedAnnotations.map(_.getCanonicalName) ++ superTypes).toSet.asJava
  }

  protected def getSupportedAnnotations: List[Class[_ <: Annotation]] = List.empty

  override def getSupportedOptions: util.Set[String] = Set(DEBUG_OPTION_NAME).asJava

  override def getSupportedSourceVersion: SourceVersion = {
    val sourceVersionAnnotation = getClass.getAnnotation(classOf[SupportedSourceVersion])
    if (sourceVersionAnnotation == null) SourceVersion.RELEASE_11 else sourceVersionAnnotation.value
  }

  // ===================================================================================================================
  // Utility functions for extracting common information from elements
  // ===================================================================================================================

  def withTreatMandatoryWarningsAsErrors(flag: Boolean)(operation: => Boolean): Boolean = { // Store current state
    val previousState = treatMandatoryWarningsAsErrors
    // Change the state with new value and perform the operation
    treatMandatoryWarningsAsErrors = flag

    val success = operation

    // Restore the state
    treatMandatoryWarningsAsErrors = previousState
    success
  }

  private[processor] def getAnnotationValueAsClassName(element: Element, annotationClass: Class[_ <: Annotation]): String = {
    val annotationValue = getAnnotationValueAsType(element, annotationClass)

    if (annotationValue != null) annotationValue.toString else ""
  }

  private[processor] def getAnnotationValueAsType(element: Element, annotationClass: Class[_ <: Annotation]): TypeMirror = {
    val annotationMirror = getAnnotationMirror(element, annotationClass)

    if (annotationMirror.isDefined) getAnnotationValueAsType(annotationMirror.get, "value") else null
  }

  // Borrowed from Dave Dopson's answer on StackOverflow: https://stackoverflow.com/a/10167558/4463881
  private def getAnnotationValueAsType(annotationMirror: AnnotationMirror, key: String): TypeMirror = {
    val annotationValue = getAnnotationValue(annotationMirror, key)

    if (annotationValue.isDefined) annotationValue.get.getValue.asInstanceOf[TypeMirror] else null
  }

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

  // Borrowed from tquadrat's answer on StackOverflow: https://stackoverflow.com/a/52257877/4463881
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
