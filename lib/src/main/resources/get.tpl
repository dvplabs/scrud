@GetMapping(path = "/{id}")
@ResponseStatus(HttpStatus.OK)
@Transactional(readOnly = true)
public ${method.dto.name} read(@PathVariable Long id) {
  var entity = entityManager.find(${method.model.name}.class, id);
  if (Objects.isNull(entity)) {
    throw new EntityNotFoundException("Entity not found");
  }

  return mapper.${method.name}(entity);
}