  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public ${method.returnType.simpleName} read(@PathVariable Long id) {
    var ${method.modelName} = entityManager.find(${method.modelType.simpleName}.class, id);
    if (Objects.isNull(${method.modelName})) {
      throw new EntityNotFoundException("Entity not found");
    }

    return mapper.${method.name}(${method.modelName});
  }
