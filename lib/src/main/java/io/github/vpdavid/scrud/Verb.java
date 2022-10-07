package io.github.vpdavid.scrud;

import static java.lang.String.format;
import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import lombok.Getter;

public enum Verb {
  GET(
      method -> 
          method.containsParameterModel() &&
          !method.containsParameterDto() &&
          method.isReturnTypeDto(),
      List.of(
          "java.util.Objects",
          "javax.persistence.EntityNotFoundException",
          "org.springframework.http.HttpStatus",
          "org.springframework.transaction.annotation.Transactional",
          "org.springframework.web.bind.annotation.GetMapping",
          "org.springframework.web.bind.annotation.PathVariable",
          "org.springframework.web.bind.annotation.ResponseStatus")),
      
  GET_ALL(
      method -> 
          method.containsParameterModel() &&
          !method.containsParameterDto() &&
          method.isReturnTypeDto(),
      List.of(
          "java.util.ArrayList",
          "java.util.stream.Collectors",
          "javax.persistence.criteria.Order",
          "org.springframework.data.domain.Page",
          "org.springframework.data.domain.PageImpl",
          "org.springframework.data.domain.Pageable",
          "org.springframework.http.HttpStatus",
          "org.springframework.transaction.annotation.Transactional",
          "org.springframework.web.bind.annotation.GetMapping",
          "org.springframework.web.bind.annotation.ResponseStatus")), 
  
  POST(
      method -> 
          method.containsParameterModel() &&
          method.containsParameterDto() &&
          method.isReturnTypeVoid(),
      List.of(
          "org.springframework.http.HttpStatus",
          "org.springframework.transaction.annotation.Transactional",
          "org.springframework.web.bind.annotation.PostMapping",
          "org.springframework.web.bind.annotation.RequestBody",
          "org.springframework.web.bind.annotation.ResponseStatus")),
  
  PUT(
      method -> 
          method.containsParameterModel() &&
          method.containsParameterDto() &&
          method.isReturnTypeVoid(),
      List.of(
          "java.util.Objects",
          "javax.persistence.EntityNotFoundException",
          "org.springframework.http.HttpStatus",
          "org.springframework.transaction.annotation.Transactional",
          "org.springframework.web.bind.annotation.PathVariable",
          "org.springframework.web.bind.annotation.PutMapping",
          "org.springframework.web.bind.annotation.RequestBody",
          "org.springframework.web.bind.annotation.ResponseStatus")), 
  
  DELETE(
      method -> 
          method.containsParameterModel() &&
          !method.containsParameterDto() &&
          method.isReturnTypeVoid(),
      List.of(
          "java.util.Objects",
          "javax.persistence.EntityNotFoundException",
          "org.springframework.http.HttpStatus",
          "org.springframework.transaction.annotation.Transactional",
          "org.springframework.web.bind.annotation.DeleteMapping",
          "org.springframework.web.bind.annotation.PathVariable",
          "org.springframework.web.bind.annotation.ResponseStatus"));
  
  private Verb(Function<Method, Boolean> verifier, List<String> imports) {
    this.verifier = verifier;
    this.dependencies = imports;
  }
  
  private final Function<Method, Boolean> verifier;
  @Getter
  private final List<String> dependencies;
  
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
