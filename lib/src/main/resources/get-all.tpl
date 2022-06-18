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