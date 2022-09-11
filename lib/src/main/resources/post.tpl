  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public void create(@RequestBody ${method.dtoType.simpleName} ${method.dtoName}) {
    var ${method.modelName} = new ${method.modelType.simpleName}();
    mapper.${method.name}(${method.generateArgumentsSignature()});
    entityManager.persist(${method.modelName});
  }
