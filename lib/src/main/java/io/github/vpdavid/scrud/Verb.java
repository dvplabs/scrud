/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.util.function.Function;
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
        method.isReturnTypeDto(),
      "No suitable method found to GET operation."),
      
  GET_ALL(method -> 
        method.containsParameterModel() &&
        !method.containsParameterDto() &&
        method.isReturnTypeDto(),
      "No suitable method found to GET_ALL operation."), 
  
  POST(method -> 
        method.containsParameterModel() &&
        method.containsParameterDto() &&
        method.isReturnTypeVoid(),
      "No suitable method found to POST operation."),
  
  PUT(method -> 
        method.containsParameterModel() &&
        method.containsParameterDto() &&
        method.isReturnTypeVoid(),
      "No suitable method found to PUT operation."), 
  
  DELETE(method -> 
        method.containsParameterModel() &&
        !method.containsParameterDto() &&
        method.isReturnTypeVoid(),
      "No suitable method found to DELETE operation.");
  
  private Verb(Function<Method, Boolean> verifier, String missingMethodMsg) {
    this.verifier = verifier;
    this.missingMethodMsg = missingMethodMsg;
  }
  
  private final Function<Method, Boolean> verifier;
  @Getter
  private final String missingMethodMsg;
  
  public boolean validFor(Method method) {
    return verifier.apply(method);
  }
}
