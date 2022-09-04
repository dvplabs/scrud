  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public Page<${method.returnType}> read(Pageable pageable) {
    var cb = entityManager.getCriteriaBuilder();

    var cqTotal = cb.createQuery(Long.class);
    var selectTotal = cqTotal.select(cb.count(cqTotal.from(${method.modelType.name}.class)));
    Long total = entityManager.createQuery(selectTotal).getSingleResult();

    var cq = cb.createQuery(${method.modelType.name}.class);
    var root = cq.from(${method.modelType.name}.class);
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
    var results = query.getResultList().stream()
      .map(o -> mapper.${method.name}((${method.modelType.name})o))
      .collect(Collectors.toList());
    return new PageImpl(results, pageable, total);
  }
