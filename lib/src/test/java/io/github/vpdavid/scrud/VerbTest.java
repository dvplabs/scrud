package io.github.vpdavid.scrud;

import io.github.vpdavid.scrud.MethodTest.Parameter;
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
  MethodDefinition METHOD_ARGUMENT_MODEL_RETURNS_VOID = new MethodDefinition()
      .withParameters(new Parameter(Model.class, "myModel"))
      .withReturnType("void");
  MethodDefinition METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_DTO = new MethodDefinition()
      .withParameters(new Parameter(Dto.class, "myDto"), new Parameter(Model.class, "myModel"))
      .withReturnType(Dto.class.getName());
  MethodDefinition METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_VOID = new MethodDefinition()
      .withParameters(new Parameter(Dto.class, "myDto"), new Parameter(Model.class, "myModel"))
      .withReturnType("void");

  boolean validFor(Verb verb, MethodDefinition md) {
    var method = new Method(md.build(), Model.class.getName(), Dto.class.getName());
    return verb.validFor(method);
  }

  @Test
  void validForGetAndGetAll() {
    assertTrue(validFor(Verb.GET, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_DTO));
    assertFalse(validFor(Verb.GET, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_VOID));

    assertTrue(validFor(Verb.GET_ALL, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_DTO));
    assertFalse(validFor(Verb.GET_ALL, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_VOID));
  }

  @Test
  void validForPostandPut() {
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.POST, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_DTO));
    assertTrue(validFor(Verb.POST, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_VOID));

    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.PUT, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_DTO));
    assertTrue(validFor(Verb.PUT, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_VOID));
  }

  @Test
  void validForDelete() {
    assertFalse(validFor(Verb.DELETE, METHOD_ARGUMENT_MODEL_RETURNS_DTO));
    assertTrue(validFor(Verb.DELETE, METHOD_ARGUMENT_MODEL_RETURNS_VOID));
    assertFalse(validFor(Verb.DELETE, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_DTO));
    assertFalse(validFor(Verb.DELETE, METHOD_ARGUMENT_MODEL_AND_DTO_RETURNS_VOID));
  }
}
