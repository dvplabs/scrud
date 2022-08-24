package io.github.vpdavid.scrud;

import io.github.vpdavid.scrud.MethodTest.Dto;
import io.github.vpdavid.scrud.MethodTest.Model;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VerbTest {
  @Mock
  ExecutableElement el;
  @Mock
  Name name;
  
  String EXPECTED_GET = 
      "@GetMapping(path = \"/{id}\")\n" +
      "@ResponseStatus(HttpStatus.OK)\n" +
      "@Transactional(readOnly = true)\n" +
      "public MethodTest.Dto read(@PathVariable Long id) {\n" +
      "  var entity = entityManager.find(MethodTest.Model.class, id);\n" +
      "  if (Objects.isNull(entity)) {\n" +
      "    throw new EntityNotFoundException(\"Entity not found\");\n" +
      "  }\n" +
      "\n" +
      "  return mapper.toDto(entity);\n" +
      "}";
  
  String EXPECTED_POST =
      "@PostMapping\n" +
      "@ResponseStatus(HttpStatus.CREATED)\n" +
      "@Transactional\n" +
      "public void create(@RequestBody MethodTest.Dto dto) {\n" +
      "  var entity = new MethodTest.Model();\n" +
      "  mapper.updateEntity(entity, dto);\n" +
      "  entityManager.persist(entity);\n" +
      "}";
  
  String EXPECTED_PUT =
      "@PutMapping(path = \"/{id}\")\n" +
      "@ResponseStatus(HttpStatus.OK)\n" +
      "@Transactional\n" +
      "public void update(@RequestBody MethodTest.Dto dto, @PathVariable Long id) {\n" +
      "  var entity = entityManager.find(MethodTest.Model.class, id);\n" +
      "  if (Objects.isNull(entity)) {\n" +
      "    throw new EntityNotFoundException(\"Entity not found\");\n" +
      "  }\n" +
      "\n" +
      "  mapper.updateEntity(entity, dto);\n" +
      "}";
  
  String EXPECTED_DELETE = 
      "@DeleteMapping(path = \"/{id}\")\n" +
      "@ResponseStatus(HttpStatus.OK)\n" +
      "@Transactional\n" +
      "public void delete(@PathVariable Long id) {\n" +
      "  var entity = entityManager.find(MethodTest.Model.class, id);\n" +
      "  if (Objects.isNull(entity)) {\n" +
      "    throw new EntityNotFoundException(\"Entity not found\");\n" +
      "  }\n" +
      "  \n" +
      "  mapper.assertValidDeletion(entity);\n" +
      "  entityManager.remove(entity);\n" +
      "}";
  
  String EXPECTED_GET_ALL = 
      "@GetMapping\n" +
      "@ResponseStatus(HttpStatus.OK)\n" +
      "@Transactional(readOnly = true)\n" +
      "public Page<MethodTest.Dto> read(Pageable pageable) {\n" +
      "  var cb = entityManager.getCriteriaBuilder();\n" +
      "\n" +
      "  var cqTotal = cb.createQuery(Long.class);\n" +
      "  var selectTotal = cqTotal.select(cb.count(cqTotal.from(MethodTest.Model.class)));\n" +
      "  Long total = entityManager.createQuery(selectTotal).getSingleResult();\n" +
      "\n" +
      "  var cq = cb.createQuery(MethodTest.Model.class);\n" +
      "  var root = cq.from(MethodTest.Model.class);\n" +
      "  cq.select(root);\n" +
      "\n" +
      "  if (pageable.getSort().isSorted()) {\n" +
      "    var orders = new ArrayList<Order>();\n" +
      "    for (var order : pageable.getSort()) {\n" +
      "      if (order.isAscending()) {\n" +
      "        orders.add(cb.asc(root.get(order.getProperty())));\n" +
      "      } else {\n" +
      "        orders.add(cb.desc(root.get(order.getProperty())));\n" +
      "      }\n" +
      "    }\n" +
      "    cq.orderBy(orders);\n" +
      "  }\n" +
      "\n" +
      "  var query = entityManager.createQuery(cq);\n" +
      "  query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());\n" +
      "  query.setMaxResults(pageable.getPageSize());\n" +
      "  var results = query.getResultList().stream()\n" +
      "    .map(o -> mapper.toDto((MethodTest.Model)o))\n" +
      "    .collect(Collectors.toList());\n" +
      "  return new PageImpl(results, pageable, total);\n" +
      "}";
  
  @Test
  void generateSourceCode() throws Exception {
    assertVerbGeneratesSourceCode(Verb.GET, "toDto", EXPECTED_GET);
    assertVerbGeneratesSourceCode(Verb.POST, "updateEntity", EXPECTED_POST);
    assertVerbGeneratesSourceCode(Verb.PUT, "updateEntity", EXPECTED_PUT);
    assertVerbGeneratesSourceCode(Verb.DELETE, "assertValidDeletion", EXPECTED_DELETE);
    assertVerbGeneratesSourceCode(Verb.GET_ALL, "toDto", EXPECTED_GET_ALL);
  }
  
  void assertVerbGeneratesSourceCode(Verb verb, String methodName, String expectedSource) throws Exception {
    when(name.toString()).thenReturn(methodName);
    when(el.getSimpleName()).thenReturn(name);
    var method = new Method(el, new TypeName(Model.class.getName()), new TypeName(Dto.class.getName()));
    
    var source = verb.generateSource(CrudGenerator.configFreemarker(), method);
    assertEquals(expectedSource, source);
  }
  
}
