  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  <#assign params = method.generateParametersSignature()>
  public void create(@RequestBody ${method.dtoType.name} ${method.dtoName}<#if params?length gt 0>, ${params}</#if>) {
    var ${method.modelName} = new ${method.modelType.name}();
    mapper.${method.name}(${method.generateArgumentsSignature()});
    entityManager.persist(${method.modelName});
  }
