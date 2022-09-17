  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  <#assign params = method.generateParametersSignature()>
  public void update(@RequestBody ${method.dtoType.simpleName} ${method.dtoName}, @PathVariable Long id<#if params?length gt 0>, ${params}</#if>) {
    var ${method.modelName} = entityManager.find(${method.modelType.simpleName}.class, id);
    if (Objects.isNull(${method.modelName})) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.${method.name}(${method.generateArgumentsSignature()});
  }
