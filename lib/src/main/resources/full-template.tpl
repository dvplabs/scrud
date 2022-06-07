  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public Page<${dto}> read(Pageable pageable) {
    var cb = entityManager.getCriteriaBuilder();

    var cqTotal = cb.createQuery(Long.class);
    var selectTotal = cqTotal.select(cb.count(cqTotal.from(${model}.class)));
    Long total = entityManager.createQuery(selectTotal).getSingleResult();

    var cq = cb.createQuery(${model}.class);
    var root = cq.from(${model}.class);
    cq.select(root);

    if (pageable.getSort().isSorted()) {
      var orders = new ArrayList<Order>();
      for (var order : pageable.getSort()) {
        if (order.isAscending()) {
          orders.add(cb.asc(root.get(order.getProperty())));
        } else {
          orders.add(cb.desc(root.get(order.getProperty())));
        }
      }
      cq.orderBy(orders);
    }

    var query = entityManager.createQuery(cq);
    query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
    query.setMaxResults(pageable.getPageSize());
    return new PageImpl(query.getResultList(), pageable, total);
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public ${dto} read(@PathVariable Long id) {
    var entity = entityManager.find(${model}.class, id);
    if (Objects.isNull(entity)) {
      throw new EntityNotFoundException("Entity not found");
    }

    return mapper.toDto(entity);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public void create(@RequestBody ${dto} dto) {
    var entity = new ${model}();
    mapper.updateEntity(entity, dto);
    entityManager.persist(entity);
  }

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