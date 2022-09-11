package io.github.vpdavid.scrud;

import java.util.Arrays;
import static java.util.stream.Collectors.toList;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MethodTest {
  @Mock
  ExecutableElement el;
  
  Method method;
  
  @BeforeEach
  void init() {
    method = new Method(el, Model.class.getName(), Dto.class.getName());
  }
  
  static void mockReturnType(ExecutableElement el, String clazz) {
    TypeMirror returnType = mock(TypeMirror.class);
    when(el.getReturnType()).thenReturn(returnType);
    when(returnType.toString()).thenReturn(clazz);
  }
  
  static void mockParameter(ExecutableElement el, Class<?>...clazz) {
    var list = Arrays.stream(clazz)
        .map(c -> new Pair(mock(VariableElement.class), new Parameter(c, "n1")))
        .collect(toList());
    when(el.getParameters()).thenAnswer(m -> list.stream().map(Pair::getVariableElement).collect(toList()));
    list.forEach(p -> {
      var tm = mock(TypeMirror.class);
      when(p.getVariableElement().asType()).thenReturn(tm);
      when(tm.toString()).thenReturn(p.getParameter().getClazz().getName());
    });
  }
  
  static void mockParameters(ExecutableElement el, Parameter...params) {
    var variables = Arrays.stream(params)
        .map(MethodTest::prepareMock)
        .collect(toList());
   doReturn(variables).when(el).getParameters(); 
  }
  
  static VariableElement prepareMock(Parameter param) {
    var variableElement = mock(VariableElement.class);
    var mirror = mock(TypeMirror.class);
    
    lenient().when(variableElement.toString()).thenReturn(param.name);
    lenient().when(variableElement.asType()).thenReturn(mirror);
    lenient().when(mirror.toString()).thenReturn(param.clazz.getName());
    return variableElement;
  }
  
  static class Model {}
  static class Dto {}
  
  @AllArgsConstructor
  @Getter
  static class Pair {
    VariableElement variableElement;
    Parameter parameter;
  }
  
  @AllArgsConstructor
  @Getter
  static class Parameter {
    Class<?> clazz;
    String name;
  }
  
  @Test
  void returnsTypeResponse() {
    mockReturnType(el, Integer.class.getName());
    assertEquals("java.lang.Integer", method.getReturnType().getFullName());
  }
  
  @Test
  void checksContainsParameterModel() {
    mockParameter(el, Model.class);
    assertTrue(method.containsParameterModel());
    mockParameter(el, Double.class);
    assertFalse(method.containsParameterModel());
  }
  
  @Test
  void checkContainsParameterDto() {
    mockParameter(el, Dto.class);
    assertTrue(method.containsParameterDto());
    mockParameter(el, Double.class);
    assertFalse(method.containsParameterDto());
  }
  
  @Test
  void checkReturnTypeIsDto() {
    mockReturnType(el, Dto.class.getName());
    assertTrue(method.isReturnTypeDto());
    mockReturnType(el, Double.class.getName());
    assertFalse(method.isReturnTypeDto());
  }
  
  @Test
  void checkReturnTypeIsVoid() {
    mockReturnType(el, "void");
    assertTrue(method.isReturnTypeVoid());
    mockReturnType(el, Dto.class.getName());
    assertFalse(method.isReturnTypeVoid());
  }
  
  @Test
  void returnsModelName() {
    mockParameters(el, new Parameter(Model.class, "myModel"));
    assertEquals("myModel", method.getModelName());
  }
  
  @Test
  void returnsDtoName() {
    mockParameters(el, new Parameter(Dto.class, "myDto"));
    assertEquals("myDto", method.getDtoName());
  }
  
  @Test
  void generatesParametersSignature() {
    mockParameters(el, new Parameter(Dto.class, "myDto"), new Parameter(Model.class, "myModel"));
    assertEquals("MethodTest.Dto myDto, MethodTest.Model myModel", method.generateParametersSignature());
  }
  
  @Test
  void generatesArgumentsSignature() {
    mockParameters(el, new Parameter(Model.class, "iModel"), new Parameter(Dto.class, "iDto"));
    assertEquals("iModel, iDto", method.generateArgumentsSignature());
  }
  
  static void mockMethodName(ExecutableElement el, String methodName) {
    var name = mock(Name.class);
    when(el.getSimpleName()).thenReturn(name);
    when(name.toString()).thenReturn(methodName);
  }
  
  @Test
  void getMethodName() {
    mockMethodName(el, "method1");
    assertEquals("method1", method.getName());
  }
}
