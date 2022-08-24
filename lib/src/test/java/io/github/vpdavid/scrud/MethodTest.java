package io.github.vpdavid.scrud;

import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
    method = new Method(el, new TypeName(Model.class.getName()), new TypeName(Dto.class.getName()));
  }
  
  @Test
  void returnsDtoAndParameterIsModel() {
    mockReturnType(el, Dto.class.getName());
    mockParameter(el, Model.class);
    
    assertTrue(method.validFor(Verb.GET));
    assertTrue(method.validFor(Verb.GET_ALL));
  }
  
  static void mockReturnType(ExecutableElement el, String clazz) {
    TypeMirror returnType = mock(TypeMirror.class);
    when(el.getReturnType()).thenReturn(returnType);
    when(returnType.toString()).thenReturn(clazz);
  }
  
  static void mockParameter(ExecutableElement el, Class<?>...clazz) {
    var list = Arrays.stream(clazz)
        .map(c -> new Pair(mock(VariableElement.class), c))
        .collect(toList());
    when(el.getParameters()).thenAnswer(m -> list.stream().map(Pair::getVe).collect(toList()));
    list.forEach(p -> {
      var tm = mock(TypeMirror.class);
      when(p.getVe().asType()).thenReturn(tm);
      when(tm.toString()).thenReturn(p.getClazz().getName());
    });
  }
  
  @Test
  void returnsVoidAndParameterIsModelAndDto() {
    mockReturnType(el, "void");
    mockParameter(el, Model.class, Dto.class);
    
    assertTrue(method.validFor(Verb.POST));
    assertTrue(method.validFor(Verb.PUT));
  }
  
  @Test
  void returnsVoidnAndParameterIsModel() {
    mockReturnType(el, "void");
    mockParameter(el, Model.class);
    assertTrue(method.validFor(Verb.DELETE));
    
    mockParameter(el, Model.class, Dto.class);
    assertFalse(method.validFor(Verb.DELETE));
  }
  
  static class Model {}
  static class Dto {}
  
  @AllArgsConstructor
  @Getter
  static class Pair {
    VariableElement ve;
    Class<?> clazz;
  }
}
