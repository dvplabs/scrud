package io.github.vpdavid.scrud;

import java.io.IOException;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import static io.github.vpdavid.scrud.MethodTest.Parameter;
import static io.github.vpdavid.scrud.MethodTest.mockMethodName;
import static io.github.vpdavid.scrud.MethodTest.mockParameters;
import static io.github.vpdavid.scrud.MethodTest.mockReturnType;

public class VerbTest {
  
  String EXPECTED_PUT =
      "  @PutMapping(path = \"/{id}\")\n" +
      "  @ResponseStatus(HttpStatus.OK)\n" +
      "  @Transactional\n" +
      "  public void update(@RequestBody MethodTest.Dto dto, @PathVariable Long id) {\n" +
      "    var entity = entityManager.find(MethodTest.Model.class, id);\n" +
      "    if (Objects.isNull(entity)) {\n" +
      "      throw new EntityNotFoundException(\"Entity not found\");\n" +
      "    }\n" +
      "\n" +
      "    mapper.updateEntity(entity, dto);\n" +
      "  }\n";
  
  String EXPECTED_DELETE = 
      "  @DeleteMapping(path = \"/{id}\")\n" +
      "  @ResponseStatus(HttpStatus.OK)\n" +
      "  @Transactional\n" +
      "  public void delete(@PathVariable Long id) {\n" +
      "    var entity = entityManager.find(MethodTest.Model.class, id);\n" +
      "    if (Objects.isNull(entity)) {\n" +
      "      throw new EntityNotFoundException(\"Entity not found\");\n" +
      "    }\n" +
      "\n" +
      "    mapper.assertValidDeletion(entity);\n" +
      "    entityManager.remove(entity);\n" +
      "  }\n";
  
  
  
//  @Test
//  void generateGet() throws Exception {
//    assertVerbGeneratesSourceCode(Verb.GET, "toDto", EXPECTED_GET);
//  }
//  
//  @Test
//  void generatePost() throws Exception {
//    assertVerbGeneratesSourceCode(Verb.POST, "updateEntity", EXPECTED_POST);
//  }
//  
//  @Test
//  void generatePostInverse() throws Exception {
//    assertVerbGeneratesSourceCode(Verb.POST, "updateEntity", EXPECTED_POST_INVERSE);
//  }
//  
//  @Test
//  void generatePut() throws Exception {
//    assertVerbGeneratesSourceCode(Verb.PUT, "updateEntity", EXPECTED_PUT);
//  }
//  
//  @Test
//  void generateDelete() throws Exception {
//    assertVerbGeneratesSourceCode(Verb.DELETE, "assertValidDeletion", EXPECTED_DELETE);
//  }
//  
//  @Test
//  void generateGetAll() throws Exception {
//    assertVerbGeneratesSourceCode(Verb.GET_ALL, "toDto", EXPECTED_GET_ALL);
//  }
//  
//  void assertVerbGeneratesSourceCode(Verb verb, String methodName, String expectedSource) throws Exception {
//    when(name.toString()).thenReturn(methodName);
//    when(el.getSimpleName()).thenReturn(name);
//    var method = new Method(el, new TypeName(Model.class.getName()), new TypeName(Dto.class.getName()));
//    
//    var source = verb.generateSource(CrudGenerator.configFreemarker(), method);
//    assertEquals(expectedSource, source);
//  }
  
  @Test
  void generatesSourceCode_get() throws Exception {
    var builder = new MethodDefinition()
        .withName("modelToDto")
        .withParameters(new Parameter(Model.class, "myModel"))
        .withReturnType(Dto.class.getName());
    
    String EXPECTED_GET = 
      "  @GetMapping(path = \"/{id}\")\n" +
      "  @ResponseStatus(HttpStatus.OK)\n" +
      "  @Transactional(readOnly = true)\n" +
      "  public Dto read(@PathVariable Long id) {\n" +
      "    var myModel = entityManager.find(Model.class, id);\n" +
      "    if (Objects.isNull(myModel)) {\n" +
      "      throw new EntityNotFoundException(\"Entity not found\");\n" +
      "    }\n" +
      "\n" +
      "    return mapper.modelToDto(myModel);\n" +
      "  }\n";
    
    assertSourceCodeMatches(EXPECTED_GET, builder, "get.tpl");
  }
  
  void assertSourceCodeMatches(String expectedValue, MethodDefinition md, String template) throws Exception {
    var method = new Method(md.build(), new TypeName(Model.class.getName()), new TypeName(Dto.class.getName()));
    
    var verb = new Verb(CrudGenerator.configFreemarker(), template);
    assertEquals(expectedValue, verb.generateSourceCode(method));
  }
  
  @Test
  void generatesSourceCode_getAll() throws Exception {
    var builder = new MethodDefinition()
        .withName("modelToDto")
        .withReturnType(Dto.class.getName());
    
    String EXPECTED_GET_ALL = 
      "  @GetMapping\n" +
      "  @ResponseStatus(HttpStatus.OK)\n" +
      "  @Transactional(readOnly = true)\n" +
      "  public Page<Dto> read(Pageable pageable) {\n" +
      "    var cb = entityManager.getCriteriaBuilder();\n" +
      "\n" +
      "    var cqTotal = cb.createQuery(Long.class);\n" +
      "    var selectTotal = cqTotal.select(cb.count(cqTotal.from(Model.class)));\n" +
      "    Long total = entityManager.createQuery(selectTotal).getSingleResult();\n" +
      "\n" +
      "    var cq = cb.createQuery(Model.class);\n" +
      "    var root = cq.from(Model.class);\n" +
      "    cq.select(root);\n" +
      "\n" +
      "    if (pageable.getSort().isSorted()) {\n" +
      "      var orders = new ArrayList<Order>();\n" +
      "      for (var order : pageable.getSort()) {\n" +
      "        if (order.isAscending()) {\n" +
      "          orders.add(cb.asc(root.get(order.getProperty())));\n" +
      "        } else {\n" +
      "          orders.add(cb.desc(root.get(order.getProperty())));\n" +
      "        }\n" +
      "      }\n" +
      "      cq.orderBy(orders);\n" +
      "    }\n" +
      "\n" +
      "    var query = entityManager.createQuery(cq);\n" +
      "    query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());\n" +
      "    query.setMaxResults(pageable.getPageSize());\n" +
      "    var results = query.getResultList().stream()\n" +
      "      .map(o -> mapper.modelToDto((Model)o))\n" +
      "      .collect(Collectors.toList());\n" +
      "    return new PageImpl(results, pageable, total);\n" +
      "  }\n";
    
    assertSourceCodeMatches(EXPECTED_GET_ALL, builder, "get-all.tpl");
  }
  
  @Test
  void generatesSourceCode_post() throws Exception {
    var builder = new MethodDefinition()
        .withName("updatesModel")
        .withParameters(new Parameter(Model.class, "myModel"), new Parameter(Dto.class, "myDto"));
    
      String EXPECTED_POST =
      "  @PostMapping\n" +
      "  @ResponseStatus(HttpStatus.CREATED)\n" +
      "  @Transactional\n" +
      "  public void create(@RequestBody Dto myDto) {\n" +
      "    var myModel = new Model();\n" +
      "    mapper.updatesModel(myModel, myDto);\n" +
      "    entityManager.persist(myModel);\n" +
      "  }\n";
      
      assertSourceCodeMatches(EXPECTED_POST, builder, "post.tpl");
  }
  
}

class Model {}
class Dto {}
