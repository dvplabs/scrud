package io.github.vpdavid.scrud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static io.github.vpdavid.scrud.MethodTest.Parameter;
import io.github.vpdavid.scrud.util.CustomSession;

public class VerbProcessorTest {
  
  @Test
  void ableToProcessTemplates() throws Exception {
    var methodDef = new MethodDefinition()
        .withName("someMethod")
        .withParameters(new Parameter(Model.class, "model"), new Parameter(Dto.class, "dto"))
        .withReturnType("void");
    var method = new Method(methodDef.build(), Model.class.getName(), Dto.class.getName());
    
    var verb = new VerbProcessor(null, CrudGenerator.configFreemarker(), "method-output.tpl");
    assertEquals("void someMethod();", verb.generateSourceCode(method));
  }
}

class Model {}
class Dto {}
