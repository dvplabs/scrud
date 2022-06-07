/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.String.format;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 *
 * @author david
 */
@SupportedAnnotationTypes("io.github.vpdavid.scrud.Crud")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class CrudGenerator extends AbstractProcessor {
  private final Pattern PATTERN = Pattern.compile("^.*/([^/]+)$");
  private final Pattern NAME = Pattern.compile("^(?:.*\\.)?([^.]+)$");
  private final String COMMOM_IMPORTS = 
      "import java.util.*;\n" +
      "import javax.persistence.*;\n" +
      "import javax.persistence.criteria.Order;\n" +
      "import org.springframework.beans.factory.annotation.Autowired;\n" +
      "import org.springframework.data.domain.*;\n" +
      "import org.springframework.http.HttpStatus;\n" +
      "import org.springframework.transaction.annotation.Transactional;\n" +
      "import org.springframework.web.bind.annotation.*;\n" +
      "import io.github.vpdavid.scrud.ResourceMapper;";

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
    try {
      var elements = set.stream()
          .flatMap(e -> re.getElementsAnnotatedWith(e).stream())
          .collect(toList());
      for (var el : elements) {
        var packageName = ((PackageElement)el.getEnclosingElement()).getQualifiedName().toString();
        var crudAnnotation = el.getAnnotation(Crud.class);
        
        String name = crudAnnotation.resource();
        var matcher = PATTERN.matcher(name);
        matcher.find();
        var resourceName = matcher.group(1);
        String className = resourceName.substring(0, 1).toUpperCase() + resourceName.substring(1);
        var file = processingEnv.getFiler().createSourceFile(packageName + format(".%sCrudController", className));
        
        var interfaces = ((TypeElement)el).getInterfaces();
        var mapper = interfaces.stream()
            .filter(e -> e.toString().startsWith(ResourceMapper.class.getName()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("element does not implement ResourceMapper"));
        
        var mirror = ((DeclaredType)mapper).getTypeArguments();
        //TODO If mirror size != 2 throw exception
        var model = mirror.get(0).toString();
        var dto = mirror.get(1).toString();        
        
        var writer = new PrintWriter(file.openWriter());
        writer.println("package " + packageName + ";\n");
        writer.println("");
        writer.println(COMMOM_IMPORTS);
        writer.println("import " + model + ";");
        writer.println("import " + dto + ";");
        writer.println("");
        writer.println("@RestController");
        writer.println(format("@RequestMapping(path = \"%s\")", name));
        writer.println(format("public class %sCrudController {", className));
        writer.println("  @Autowired\n  private EntityManager entityManager;");
        writer.println(format("  @Autowired\n  private ResourceMapper<%s, %s> mapper;", 
            simpleName(model), simpleName(dto)));
        content(simpleName(model), simpleName(dto)).forEach(writer::println);
        writer.println("}");
        writer.close();
      }
      return true;
    }
    catch (IOException ex) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
      return false;
    }
  }
  
  private List<String> content(String model, String dto) throws IOException {
    var file = new File(getClass().getClassLoader().getResource("full-template.tpl").getFile());
    return Files.readAllLines(file.toPath()).stream()
        .map(s -> s.replace("${dto}", dto))
        .map(s -> s.replace("${model}", model))
        .collect(toList());
  }
  
  private String simpleName(String className) {
    var m = NAME.matcher(className);
    m.find();
    return m.group(1);
  }
}
