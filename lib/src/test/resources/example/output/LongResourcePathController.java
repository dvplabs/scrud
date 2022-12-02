package example.input.mapper;

import javax.annotation.processing.Generated;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "io.github.dvplabs.scrud.CrudGenerator", date = "2022-05-12 18:23:12")
@RestController
@RequestMapping(path = "/21live/3/v1/info")
public class LiveV1InfoCrudController {

  @Autowired
  private EntityManager entityManager;
  @Autowired
  private LongResourcePath mapper;

}
