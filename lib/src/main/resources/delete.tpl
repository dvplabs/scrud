@DeleteMapping(path = "/{id}")
@ResponseStatus(HttpStatus.OK)
@Transactional
public void delete(@PathVariable Long id) {
  var entity = entityManager.find(${model}.class, id);
  if (Objects.isNull(entity)) {
    throw new EntityNotFoundException("Entity not found");
  }

  entityManager.remove(entity);
}