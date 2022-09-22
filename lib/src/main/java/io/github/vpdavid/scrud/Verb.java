/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import static java.lang.String.format;
import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import lombok.Getter;


/**
 *
 * @author david
 */
public enum Verb {
  GET(method -> 
        method.containsParameterModel() &&
        !method.containsParameterDto() &&
        method.isReturnTypeDto()),
      
  GET_ALL(method -> 
        method.containsParameterModel() &&
        !method.containsParameterDto() &&
        method.isReturnTypeDto()), 
  
  POST(method -> 
        method.containsParameterModel() &&
        method.containsParameterDto() &&
        method.isReturnTypeVoid()),
  
  PUT(method -> 
        method.containsParameterModel() &&
        method.containsParameterDto() &&
        method.isReturnTypeVoid()), 
  
  DELETE(method -> 
        method.containsParameterModel() &&
        !method.containsParameterDto() &&
        method.isReturnTypeVoid());
  
  private Verb(Function<Method, Boolean> verifier) {
    this.verifier = verifier;
  }
  
  private final Function<Method, Boolean> verifier;
  
  public boolean validFor(Method method) {
    return verifier.apply(method);
  }
  
  public String getMissingMethodMsg() {
    return format("No suitable method found for %s operation.", name());
  }
  
  public String getTooManyMethodsMsg(List<Method> methods) {
    return format(
        "Too many methods found for %s operation: %s.", 
        name(), 
        methods.stream().map(Method::getName).collect(joining(", ")));
  }
}
