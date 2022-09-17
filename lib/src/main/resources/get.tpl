  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  <#assign params = method.generateParametersSignature()>
  public ${method.returnType.simpleName} read(@PathVariable Long id<#if params?length gt 0>, ${params}</#if>) {
    var ${method.modelName} = entityManager.find(${method.modelType.simpleName}.class, id);
    if (Objects.isNull(${method.modelName})) {
      throw new EntityNotFoundException("Entity not found");
    }

    return mapper.${method.name}(${method.generateArgumentsSignature()});
  }
