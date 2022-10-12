package io.github.vpdavid.scrud;

import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import lombok.Getter;


/**
 *
 * @author david
 */
public class Method {

  private ExecutableElement el;
  @Getter
  private TypeName modelType, dtoType;
  
  public Method(Element el, String modelType, String dtoType) {
    if (el instanceof ExecutableElement) {
      this.el = (ExecutableElement) el;
    }
    this.modelType = new TypeName(modelType);
    this.dtoType = new TypeName(dtoType);
  }
  
  public String getName() {
    return el.getSimpleName().toString();
  }
  
  public TypeName getReturnType() {
    return new TypeName(el.getReturnType().toString());
  }

  private boolean containsParameterType(String typeName) {
    var params = el.getParameters().stream()
          .map(Element::asType)
          .map(Object::toString)
          .collect(toSet());
    return params.contains(typeName);
  }

  public String getModelName() {
    return getParameterName(modelType.getFullName());
  }
  
  private String getParameterName(String clazzName) {
    var providedModel = el.getParameters().stream()
        .filter(p -> clazzName.equals(p.asType().toString()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Parameter of tye " + modelType.getSimpleName() + " does not exist"));
    
    return providedModel.toString();
  }

  public String getDtoName() {
    return getParameterName(dtoType.getFullName());
  }

  public String generateParametersSignature() {
    return el.getParameters().stream()
        .filter(p -> !modelType.getFullName().equals(p.asType().toString()))
        .filter(p -> !dtoType.getFullName().equals(p.asType().toString()))
        .map(p -> new TypeName(p.asType().toString()).getSimpleName() + " " + p.toString())
        .collect(joining(", "));
  }
  
  public List<String> getDependencies() {
    return el.getParameters().stream()
        .map(p -> p.asType().toString())
        .map(dep -> dep.replaceAll("\\$", "."))
        .collect(toList());
  }

  public String generateArgumentsSignature() {
    return el.getParameters().stream()
        .map(p -> p.toString())
        .collect(joining(", "));
  }

  public boolean containsParameterModel() {
    return containsParameterType(modelType.getFullName());
  }
  
  public boolean containsParameterDto() {
    return containsParameterType(dtoType.getFullName());
  }

  public boolean isReturnTypeDto() {
    return getReturnType().equals(dtoType);
  }

  public boolean isReturnTypeVoid() {
    return getReturnType().getFullName().equals("void");
  }
  
}
