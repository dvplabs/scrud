package io.github.vpdavid.scrud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static io.github.vpdavid.scrud.MethodTest.Parameter;

public class VerbProcessorTest {
  
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
    var method = new Method(md.build(), Model.class.getName(), Dto.class.getName());
    
    var verb = new VerbProcessor(null, CrudGenerator.configFreemarker(), template);
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
  
  @Test
  void generateSourceCode_put() throws Exception {
    var builder = new MethodDefinition()
        .withName("executeSimpleUpdate")
        .withParameters(new Parameter(Model.class, "myModel"), new Parameter(Dto.class, "myDto"));
    String EXPECTED_PUT =
      "  @PutMapping(path = \"/{id}\")\n" +
      "  @ResponseStatus(HttpStatus.OK)\n" +
      "  @Transactional\n" +
      "  public void update(@RequestBody Dto myDto, @PathVariable Long id) {\n" +
      "    var myModel = entityManager.find(Model.class, id);\n" +
      "    if (Objects.isNull(myModel)) {\n" +
      "      throw new EntityNotFoundException(\"Entity not found\");\n" +
      "    }\n" +
      "\n" +
      "    mapper.executeSimpleUpdate(myModel, myDto);\n" +
      "  }\n";
    assertSourceCodeMatches(EXPECTED_PUT, builder, "put.tpl");
  }
  
  @Test
  void generateSourceCode_delete() throws Exception {
    var builder = new MethodDefinition()
        .withName("assertIsRemovable")
        .withParameters(new Parameter(Model.class, "someModel"));
    String EXPECTED_DELETE = 
      "  @DeleteMapping(path = \"/{id}\")\n" +
      "  @ResponseStatus(HttpStatus.OK)\n" +
      "  @Transactional\n" +
      "  public void delete(@PathVariable Long id) {\n" +
      "    var someModel = entityManager.find(Model.class, id);\n" +
      "    if (Objects.isNull(someModel)) {\n" +
      "      throw new EntityNotFoundException(\"Entity not found\");\n" +
      "    }\n" +
      "\n" +
      "    mapper.assertIsRemovable(someModel);\n" +
      "    entityManager.remove(someModel);\n" +
      "  }\n";
    assertSourceCodeMatches(EXPECTED_DELETE, builder, "delete.tpl");
  }
  
}

class Model {}
class Dto {}
