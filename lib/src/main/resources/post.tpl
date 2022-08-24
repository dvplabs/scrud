@PostMapping
@ResponseStatus(HttpStatus.CREATED)
@Transactional
public void create(@RequestBody ${method.dto.name} dto) {
  var entity = new ${method.model.name}();
  mapper.${method.name}(entity, dto);
  entityManager.persist(entity);
}