/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.util.function.Function;


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
}
