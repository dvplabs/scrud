package io.github.vpdavid.scrud;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import lombok.Getter;


/**
 *
 * @author david
 */
public class Method {

  private ExecutableElement el;
  @Getter
  private TypeName modelType, dtoType;
  
  public Method(Element el, TypeName model, TypeName dto) {
    if (el instanceof ExecutableElement) {
      this.el = (ExecutableElement) el;
    }
    this.modelType = model;
    this.dtoType = dto;
  }
  
  boolean validFor(Verb v) {
    var validReturnType = false;
    var validParameter = false;
    
//    if (v == Verb.GET || v == Verb.GET_ALL) {
//      validReturnType = dto.getFullName().equals(el.getReturnType().toString());
//      validParameter = model.getFullName().equals(el.getParameters().get(0).asType().toString());
//    } else if (v == Verb.POST || v == Verb.PUT) {
//      validReturnType = "void".equals(el.getReturnType().toString());
//      var params = el.getParameters().stream()
//          .map(Element::asType)
//          .map(Object::toString)
//          .collect(toSet());
//      validParameter = params.contains(model.getFullName()) && params.contains(dto.getFullName());
//    } if (v == Verb.DELETE) {
//      validReturnType = "void".equals(el.getReturnType().toString());
//      var parms = el.getParameters().stream()
//          .map(Element::asType)
//          .map(Object::toString)
//          .collect(toSet());
//      validParameter = parms.contains(model.getFullName()) && parms.size() == 1;
//    }
    
    return validReturnType && validParameter;
  }
  
  public String getName() {
    return el.getSimpleName().toString();
  }
  
  public String getReturnType() {
    return el.getReturnType().toString();
  }

  boolean containsParameter(String name) {
    var params = el.getParameters().stream()
          .map(Element::asType)
          .map(Object::toString)
          .collect(toSet());
    return params.contains(name);
  }

  public String getModelName() {
    return getParameterName(modelType.getFullName());
  }
  
  private String getParameterName(String clazzName) {
    var providedModel = el.getParameters().stream()
        .filter(p -> clazzName.equals(p.asType().toString()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Parameter of tye " + modelType.getName() + " does not exist"));
    
    return providedModel.toString();
  }

  public String getDtoName() {
    return getParameterName(dtoType.getFullName());
  }

  public String generateParametersSignature() {
    return el.getParameters().stream()
        .map(p -> new TypeName(p.asType().toString()).getName() + " " + p.toString())
        .collect(joining(", "));
  }

  public String generateArgumentsSignature() {
    return el.getParameters().stream()
        .map(p -> p.toString())
        .collect(joining(", "));
  }
  
}
