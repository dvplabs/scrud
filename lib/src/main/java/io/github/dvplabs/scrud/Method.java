package io.github.dvplabs.scrud;

import java.util.List;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;


/**
 *
 * @author david
 */
public class Method {

  private ExecutableElement el;
  private TypeName modelType, dtoType;
  private ConflictDetector conflictDetector;
  
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
    assertContextExists();
    return new TypeName(el.getReturnType().toString(), conflictDetector);
  }
  
  private void assertContextExists() {
    if (conflictDetector == null) {
      throw new IllegalStateException("No context defined");
    }
  }

  private boolean containsParameterType(String typeName) {
    var params = el.getParameters().stream()
          .map(Element::asType)
          .map(o -> new TypeName(o.toString()).getFullName())
          .collect(toSet());
    return params.contains(typeName);
  }

  public String getModelName() {
    return getParameterName(modelType.getFullName());
  }
  
  private String getParameterName(String clazzName) {
    var providedModel = el.getParameters().stream()
        .filter(p -> {
          var type = new TypeName(p.asType().toString());
          return type.getFullName().equals(clazzName);
        })
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Parameter of tye " + modelType.getSimpleName() + " does not exist"));
    
    return providedModel.toString();
  }

  public String getDtoName() {
    return getParameterName(dtoType.getFullName());
  }

  public String generateParametersSignature() {
    assertContextExists();
    return el.getParameters().stream()
        .filter(p -> !modelType.getFullName().equals(
            new TypeName(p.asType().toString()).getFullName()))
        .filter(p -> !dtoType.getFullName().equals(
            new TypeName(p.asType().toString()).getFullName()))
        .map(p -> new TypeName(p.asType().toString(), conflictDetector).getName() 
            + " " + p.toString())
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
    return new TypeName(el.getReturnType().toString()).getFullName().equals(dtoType.getFullName());
  }

  public boolean isReturnTypeVoid() {
    return new TypeName(el.getReturnType().toString()).getFullName().equals("void");
  }
  
  public Method withContext(ConflictDetector detector) {
    var m = new Method(el, modelType.getFullName(), dtoType.getFullName());
    m.conflictDetector = detector;
    return m;
  }
  
  public TypeName getDtoType() {
    assertContextExists();
    return new TypeName(dtoType.getFullName(), conflictDetector);
  }
  
  public TypeName getModelType() {
    assertContextExists();
    return new TypeName(modelType.getFullName(), conflictDetector);
  }
}
