@PutMapping(path = "/{id}")
@ResponseStatus(HttpStatus.OK)
@Transactional
public void update(@RequestBody ${dto} dto, @PathVariable Long id) {
  var entity = entityManager.find(${model}.class, id);
  if (Objects.isNull(entity)) {
    throw new EntityNotFoundException("Entity not found");
  }

  mapper.updateEntity(entity, dto);
}