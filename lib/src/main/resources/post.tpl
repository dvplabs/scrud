  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public void create(@RequestBody ${method.dtoType.name} ${method.dtoName}) {
    var ${method.modelName} = new ${method.modelType.name}();
    mapper.${method.name}(${method.generateArgumentsSignature()});
    entityManager.persist(${method.modelName});
  }
