package io.github.vpdavid.scrud;

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
  private TypeName model, dto;
  
  public Method(Element el, TypeName model, TypeName dto) {
    if (el instanceof ExecutableElement) {
      this.el = (ExecutableElement) el;
    }
    this.model = model;
    this.dto = dto;
  }
  
  boolean validFor(Verb v) {
    var validReturnType = false;
    var validParameter = false;
    
    if (v == Verb.GET || v == Verb.GET_ALL) {
      validReturnType = dto.getFullName().equals(el.getReturnType().toString());
      validParameter = model.getFullName().equals(el.getParameters().get(0).asType().toString());
    } else if (v == Verb.POST || v == Verb.PUT) {
      validReturnType = "void".equals(el.getReturnType().toString());
      var params = el.getParameters().stream()
          .map(Element::asType)
          .map(Object::toString)
          .collect(toSet());
      validParameter = params.contains(model.getFullName()) && params.contains(dto.getFullName());
    } if (v == Verb.DELETE) {
      validReturnType = "void".equals(el.getReturnType().toString());
      var parms = el.getParameters().stream()
          .map(Element::asType)
          .map(Object::toString)
          .collect(toSet());
      validParameter = parms.contains(model.getFullName()) && parms.size() == 1;
    }
    
    return validReturnType && validParameter;
  }
  
  public String getName() {
    return el.getSimpleName().toString();
  }
  
}
