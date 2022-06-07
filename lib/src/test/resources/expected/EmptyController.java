package example;

import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import io.github.vpdavid.scrud.ResourceMapper;
import example.model.Product;
import example.dto.ProductDto;

@RestController
@RequestMapping(path = "/v1/products", methods = {})
public class ProductsCrudController {

  @Autowired
  private EntityManager entityManager;
  @Autowired
  private ResourceMapper<Product, ProductDto> mapper;
}