  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public void delete(@PathVariable Long id) {
    var entity = entityManager.find(${method.model.name}.class, id);
    if (Objects.isNull(entity)) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.${method.name}(entity);
    entityManager.remove(entity);
  }
