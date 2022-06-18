@GetMapping(path = "/{id}")
@ResponseStatus(HttpStatus.OK)
@Transactional(readOnly = true)
public ${dto} read(@PathVariable Long id) {
  var entity = entityManager.find(${model}.class, id);
  if (Objects.isNull(entity)) {
    throw new EntityNotFoundException("Entity not found");
  }

  return mapper.toDto(entity);
}