package io.github.dvplabs.scrud;

import io.github.dvplabs.scrud.MethodTest.Parameter;
import java.util.Arrays;
import java.util.function.BiConsumer;
import static java.util.stream.Collectors.toList;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 *
 * @author david
 */
public class MethodDefinition {
  ExecutableElement element;
  Parameter[] params;
  String returnTypeName;
  String methodName;
  
  public MethodDefinition() {
    element = mock(ExecutableElement.class);
  }
  
  public MethodDefinition withParameters(Parameter...params) {
    this.params = params;
    return this;
  }
  
  public MethodDefinition withReturnType(String typeName) {
    returnTypeName = typeName;
    return this;
  }
  
  public MethodDefinition withName(String name) {
    methodName = name;
    return this;
  }
  
  public ExecutableElement build() {
    BiConsumer<Boolean, Runnable> executeIf = (flagIsTrue, runnable) -> {
      if (flagIsTrue) {
        runnable.run();
      }
    };
    
    element = mock(ExecutableElement.class);
    executeIf.accept(params != null, this::mockParameters);
    executeIf.accept(returnTypeName != null, this::mockReturnType);
    executeIf.accept(methodName != null, this::mockMethodName);
    return element;
  }
  
  void mockParameters() {    
    var variables = Arrays.stream(params)
        .map(this::prepareMock)
        .collect(toList());
   lenient().doReturn(variables).when(element).getParameters(); 
  }
  
  VariableElement prepareMock(Parameter param) {
    var variableElement = mock(VariableElement.class);
    var mirror = mock(TypeMirror.class);
    
    lenient().when(variableElement.toString()).thenReturn(param.name);
    lenient().when(variableElement.asType()).thenReturn(mirror);
    lenient().when(mirror.toString()).thenReturn(param.clazz.getName());
    return variableElement;
  }
  
  void mockReturnType() {
    TypeMirror returnType = mock(TypeMirror.class);
    lenient().when(element.getReturnType()).thenReturn(returnType);
    lenient().when(returnType.toString()).thenReturn(returnTypeName);
  }
  
  void mockMethodName() {
    var name = mock(Name.class);
    lenient().when(element.getSimpleName()).thenReturn(name);
    lenient().when(name.toString()).thenReturn(methodName);
  }
}
