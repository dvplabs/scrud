@PostMapping
@ResponseStatus(HttpStatus.CREATED)
@Transactional
public void create(@RequestBody ${dto} dto) {
  var entity = new ${model}();
  mapper.updateEntity(entity, dto);
  entityManager.persist(entity);
}