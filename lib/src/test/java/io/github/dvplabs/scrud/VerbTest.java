package io.github.dvplabs.scrud;

import io.github.dvplabs.scrud.util.CustomSession;
import io.github.dvplabs.scrud.MethodTest.Parameter;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author david
 */
public class VerbTest {

  MethodDefinition METHOD_ARGUMENT_MODEL_RETURNS_DTO = new MethodDefinition()
      .withParameters(new Parameter(Model.class, "myModel"))
      .withReturnType(Dto.class.getName());
  MethodDefinition METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_DTO = new MethodDefinition()
      .withParameters(new Parameter(CustomSession.class, "s1"), new Parameter(Model.class, "m1"), new Parameter(Object.class, "obj"))
      .withReturnType(Dto.class.getName());
  MethodDefinition METHOD_ARGUMENT_MODEL_RETURNS_VOID = new MethodDefinition()
      .withParameters(new Parameter(Model.class, "myModel"))
      .withReturnType("void");
  MethodDefinition METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_VOID = new MethodDefinition()
      .withParameters(new Parameter(Model.class, "myModel"))
      .withReturnType("void");
  MethodDefinition METHOD_ARGUMENTS_MODEL_AND_DTO_RETURNS_DTO = new MethodDefinition()
      .withParameters(new Parameter(Dto.class, "myDto"), new Parameter(Model.class, "myModel"))
      .withReturnType(Dto.class.getName());
  MethodDefinition METHOD_ARGUMENTS_MODEL_AND_DTO_AND_SESSION_AND_OBJECT_RETURNS_VOID = new MethodDefinition()
      .withParameters(new Parameter(Model.class, "myModel"), new Parameter(Dto.class, "myDto"), new Parameter(CustomSession.class, "s1"), new Parameter(Object.class, "obj"))
      .withReturnType("void");
  MethodDefinition METHOD_ARGUMENTS_DTO_AND_MODEL_RETURNS_VOID = new MethodDefinition()
      .withParameters(new Parameter(Dto.class, "myDto"), new Parameter(Model.class, "myModel"))
      .withReturnType("void");

  boolean validFor(Verb verb, MethodDefinition md) {
    var method = new Method(md.build(), Model.class.getName(), Dto.class.getName());
    return verb.validFor(method);
  }

  @Test
  void validForGetAndGetAll() {
    assertTrue(validFor(Verb.GET, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertTrue(validFor(Verb.GET, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_DTO));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENTS_MODEL_AND_DTO_RETURNS_DTO));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENTS_DTO_AND_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENTS_MODEL_AND_DTO_AND_SESSION_AND_OBJECT_RETURNS_VOID));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_VOID));

    assertTrue(validFor(Verb.GET_ALL, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertTrue(validFor(Verb.GET_ALL, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_DTO));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENTS_MODEL_AND_DTO_RETURNS_DTO));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENTS_DTO_AND_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENTS_MODEL_AND_DTO_AND_SESSION_AND_OBJECT_RETURNS_VOID));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_VOID));
  }

  @Test
  void validForPostandPut() {
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_DTO));
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENTS_MODEL_AND_DTO_RETURNS_DTO));
    assertTrue(validFor(Verb.POST, METHOD_ARGUMENTS_DTO_AND_MODEL_RETURNS_VOID));
    assertTrue(validFor(Verb.POST, METHOD_ARGUMENTS_MODEL_AND_DTO_AND_SESSION_AND_OBJECT_RETURNS_VOID));
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_VOID));

    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_DTO));
    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENTS_MODEL_AND_DTO_RETURNS_DTO));
    assertTrue(validFor(Verb.PUT, METHOD_ARGUMENTS_DTO_AND_MODEL_RETURNS_VOID));
    assertTrue(validFor(Verb.PUT, METHOD_ARGUMENTS_MODEL_AND_DTO_AND_SESSION_AND_OBJECT_RETURNS_VOID));
    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_VOID));
  }

  @Test
  void validForDelete() {
    assertFalse(validFor(Verb.DELETE, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertTrue(validFor(Verb.DELETE, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertTrue(validFor(Verb.DELETE, METHOD_ARGUMENTS_SESSION_AND_MODEL_AND_OBJECT_RETURNS_VOID));
    assertFalse(validFor(Verb.DELETE, METHOD_ARGUMENTS_MODEL_AND_DTO_RETURNS_DTO));
    assertFalse(validFor(Verb.DELETE, METHOD_ARGUMENTS_DTO_AND_MODEL_RETURNS_VOID));
  }
}
